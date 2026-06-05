# Tomes of Experience — Engineering Journal

*Author: Hans Kröner ([@hanskroner](https://github.com/hanskroner))*

---

## The Big Picture

Tomes of Experience is a Minecraft mod that adds six book-like items — tomes — that let
players store and retrieve experience points. Think of them as XP savings accounts: you
charge a tome by shift-right-clicking (withdrawing XP from yourself), then spend it later
by right-clicking (depositing XP back). Each tier holds more, transfers more per use, and
demands a higher minimum level to charge, forming a natural progression ladder from the
humble Minor Tome all the way up to the legendary Major Tome.

The core problem it solves is the anxiety tax of high-level enchanting. In vanilla
Minecraft, dying at level 30 means losing hundreds of hard-earned experience points with
no recourse. Tomes give players a place to bank XP before risky ventures — making
ambitious gameplay feel less punishing without removing the stakes entirely.

---

## Architecture Deep Dive

Think of the mod as a bank with a very specific product: XP certificates. The analogy maps
cleanly:

- **The tomes** are the certificates themselves. Each one is an item (`BaseTomeOfExperience`
  subclass) with a face value stamped on it — stored as a `TomeComponent` data component
  attached to the item stack. The data component is the number printed on the certificate;
  the item class is the paper it's printed on.
- **`BaseTomeOfExperience`** is the bank teller. It handles all deposits (`transferToTome`)
  and withdrawals (`transferToPlayer`), enforces the minimum balance required to open an
  account (`getMinimumLevel`), applies the processing fee (`getEfficiency`), and knows the
  vault's capacity (`getCapacity`). Subclasses are different account tiers — each just
  overrides those four numbers and the transfer granularity.
- **`TomesOfExperienceConfig`** is the bank's policy manual. Every number the teller
  consults — capacity, efficiency, minimum level — is read from there at call time, never
  cached. This means the policy can change while the bank is open (live config edits) and
  the next transaction immediately reflects the new terms.
- **`LootTableConfig`** is the branch-opening team. When the world loads, it registers a
  loot table listener that injects tomes into vanilla structure chests, so players discover
  them in the wild before they've figured out the crafting recipe.

Data flow for a deposit: player shift-right-clicks → `use()` runs server-side →
`transferToTome()` is called → the appropriate `addPointsToTome()` variant deducts XP from
the player and stamps a new `TomeComponent` onto the stack → `updateGlint()` sets or clears
the enchantment shimmer depending on whether the tome is now non-empty.

One important quirk: `player.totalExperience` is unreliable in Minecraft — the value it
reports doesn't match what the XP bar shows. All XP arithmetic is done from
`player.experienceLevel` and `player.experienceProgress * player.getXpNeededForNextLevel()`
instead.

---

## The Codebase Map

```
src/
├── main/java/com/hangbunny/
│   ├── TomesOfExperience.java       ← mod entry point; registers items, component, creative tab
│   ├── config/
│   │   ├── TomesOfExperienceConfig  ← Cloth Config POJO — all tunable values live here
│   │   └── LootTableConfig          ← wires up loot injection on world load
│   ├── item/
│   │   ├── BaseTomeOfExperience     ← abstract base; all game logic lives here
│   │   ├── TomeOf*.java             ← six thin subclasses; each overrides 4 config accessors
│   │   └── component/
│   │       └── TomeComponent        ← the data component record stamped on every tome stack
│   └── loot/
│       ├── SkewedLootNumberProvider ← custom skewed-normal distribution for loot XP amounts
│       ├── SetExperienceLootFunction← stamps XP onto loot-found tomes
│       └── TomesLootTypes           ← registers the two custom loot types into Minecraft
│
├── main/resources/
│   ├── fabric.mod.json              ← mod metadata, entrypoints, dependencies
│   ├── assets/tomes_of_experience/  ← textures, item models, lang files
│   └── data/
│       ├── tomes_of_experience/recipe/  ← six crafting/smithing recipes
│       └── **/tags/item/bookshelf_books ← makes tomes count as books near bookshelves
│
└── client/java/com/hangbunny/client/
    ├── TomesOfExperienceClient      ← client entry point; tooltip callbacks
    ├── ClientInputHelper            ← LWJGL shift-key detection (client-only)
    └── config/ModMenuIntegration    ← hooks into Mod Menu's config screen API
```

To add a new tome tier: create a subclass in `item/`, override the four config accessors,
register it in `TomesOfExperience.java`, add recipes under `data/.../recipe/`, and add
entries to the lang file and loot table config.

---

## Tech Stack & Why

| Technology | Why we chose it | Alternatives considered |
|---|---|---|
| **Fabric** (mod loader) | Lightweight, fast update cycle when new MC versions drop, strong modding community for 1.21+ | Forge (heavier, slower to update); NeoForge (newer, less community tooling at the time) |
| **Cloth Config** | Zero-boilerplate config with a built-in GUI screen and live reload; integrates natively with Mod Menu | Manual JSON config (no GUI); Forge's config system (Fabric-incompatible) |
| **Mod Menu** | De-facto standard for in-game config entry points on Fabric; declared as `suggested` so the mod works without it | No in-game config (worse UX for server admins) |
| **Fabric API** | Required for events (`LootTableEvents.MODIFY`, `ItemTooltipCallback`), creative tab API, and the split client/server source set pattern | None — this is effectively the standard library for Fabric mods |
| **Jankson (via Cloth Config)** | Produces JSON5-flavored config files that support comments — useful for documenting what each value does directly in the file | Plain JSON (no comments); TOML (less familiar to Minecraft players) |

---

## The Journey

### Lessons Learned

#### `appendHoverText` is deprecated — and the obvious fix doesn't work
*2026-06-05*

Minecraft 26.1 marks `Item.appendHoverText` as `@Deprecated`. The mod overrode it in
`BaseTomeOfExperience` to show flavor text describing how full the tome is ("A faint
shimmer...", "Almost bursting...", etc.) based on the percentage of capacity used.

The obvious migration is `TooltipProvider`: implement it on `TomeComponent`, register the
component type via Fabric's `ItemComponentTooltipProviderRegistry`, and the vanilla tooltip
pipeline calls `addToTooltip` automatically. Vanilla components like `EnchantmentList` and
`PotionContents` all work this way.

The catch: `TooltipProvider.addToTooltip` receives a `DataComponentGetter` — it can read
other components from the stack, but it has no path to the `Item` instance. The fill-level
tooltip needs `tome.getCapacity()`, which is a live read from `TomesOfExperienceConfig`.
Making this work via `TooltipProvider` would require storing capacity inside `TomeComponent`
itself — duplicating config data onto every stack and breaking the invariant that item
classes always read config at call time rather than caching it.

The fix was to consolidate into the `ItemTooltipCallback.EVENT` already registered in
`TomesOfExperienceClient`. That callback receives the full `ItemStack`, so
`item instanceof BaseTomeOfExperience tome` gives direct access to `tome.getCapacity()`.
Moving the logic to `src/client/` is also architecturally correct — tooltips are a
client-side concern and this code was misplaced in `src/main/` to begin with.

We learned that when a Minecraft API deprecation offers a component-based replacement, the
first question to ask is: *does my tooltip logic need the item instance?* If yes, the
component path is a dead end until you're willing to restructure what the component knows.

---

### Gotchas & Pitfalls

> ⚠️ **Never read `player.totalExperience`** — it does not report the value shown in the XP
> bar. Always derive total XP from `player.experienceLevel` plus
> `player.experienceProgress * player.getXpNeededForNextLevel()`. This is a longstanding
> Minecraft inconsistency and the compiler won't warn you about it.

---

### Bugs We Squashed

_Nothing yet — check back soon._

### New Technologies Discovered

_Nothing yet — check back soon._

---

## Engineer's Wisdom

**Read config at call time, never cache it.** Every item accessor (`getCapacity`,
`getEfficiency`, `getMinimumLevel`) delegates directly to `TomesOfExperience.CONFIG.<field>`
rather than storing the value in a field. This makes live config changes (via Cloth Config's
in-game screen) take effect on the very next use without a restart. Cache a value in a
constructor and you silently break that guarantee.

---

## If I Were Starting Over...

_Nothing yet — check back soon._
