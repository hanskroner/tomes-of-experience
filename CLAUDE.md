# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What is this mod?

Tomes of Experience is a Minecraft mod for the [Fabric](https://fabricmc.net/) mod loader. It adds six book items — tomes — that can absorb and release player experience points. The tomes form a progression ladder: each tier stores more XP, transfers more XP per use, and requires a higher player level to charge. All per-tome parameters (capacity, efficiency, minimum level) and loot-table injection settings are configurable at runtime via [Cloth Config](https://github.com/shedaniel/cloth-config) and exposed in-game through [Mod Menu](https://github.com/TerraformersMC/ModMenu).

Mod ID / namespace: `tomes-of-experience` / `tomes_of_experience`  
Package root: `com.hangbunny`  
Published at: <https://modrinth.com/mod/tomes-of-experience>

---

## Project Memory

`JOURNAL.md` (project root) is the living record of this codebase: architectural decisions and why they were made, bugs and the false leads that preceded them, Minecraft/Fabric gotchas discovered in practice, and lessons that aren't derivable from reading the code alone.

**Read `JOURNAL.md` before starting any non-trivial task.** It records pitfalls that will otherwise be rediscovered the hard way, and captures the reasoning behind decisions that look arbitrary without context. The *Gotchas & Pitfalls* and *Lessons Learned* subsections are especially worth checking when working in unfamiliar areas of the mod.

When a task produces a new lesson, architectural decision, or non-obvious bug fix, add an entry to `JOURNAL.md` before considering the task done.

---

## Tome Tiers

| Tome | Rarity | Capacity | Efficiency | Min Level | Transfer per use |
|---|---|---|---|---|---|
| **Minor** | Common | 160 pts | 90% | 0 | ⅓ of current level |
| **Lesser** | Common | 315 pts | 90% | 0 | ½ of current level |
| **Experience** | Uncommon | 550 pts | 85% | 5 | 1 full level |
| **Greater** | Rare | 1 395 pts | 85% | 10 | 1 full level |
| **Superior** | Epic | 2 920 pts | 90% | 15 | 3 full levels |
| **Major** | Epic | 5 345 pts | 95% | 20 | 5 full levels |

All values above are defaults; every one of them is a field in `TomesOfExperienceConfig`.

---

## Crafting Recipes

| Tome | Recipe type | Ingredients |
|---|---|---|
| Minor | Shaped crafting | Book surrounded by Redstone (plus pattern) |
| Lesser | Shaped crafting | Minor Tome surrounded by Lapis Lazuli (plus pattern) |
| Experience | Shaped crafting | Leather + Book + Diamond + Ender Pearl (L-shape) |
| Greater | Shaped crafting | Experience Tome + Gold Nuggets + Amethyst Shards + Blaze Powder |
| Superior | Shaped crafting | Enchanted Book + Gold Nuggets + Ghast Tears + Diamond |
| Major | Smithing transform | Superior Tome + Netherite Ingot + Netherite Upgrade template |

---

## Platform & Tooling

- **Minecraft version:** 26.1
- **Fabric Loader:** 0.18.4
- **Fabric API:** 0.144.3+26.1
- **Java:** 25
- **Dependencies:** Cloth Config 26.1.154 (required), Mod Menu 18.0.0-alpha.8 (optional/suggested)

---

## Build & Run Commands

```bash
# Build the mod jar (output in build/libs/)
./gradlew build

# Run a local client instance (uses run/ as game directory)
./gradlew runClient

# Run a local server instance
./gradlew runServer
```

The `run/` directory is the live game directory created by Fabric Loom for local testing. CI runs `./gradlew build` on every push of a version tag (`v*.*.*` or `v*.*.*-<label>`).

---

## Source Layout

The project uses Fabric Loom's split-environment source sets:

```
src/
├── main/java/com/hangbunny/
│   ├── TomesOfExperience.java          # ModInitializer: registers items, component, creative tab, loot
│   ├── config/
│   │   ├── TomesOfExperienceConfig.java # Cloth Config @Config POJO — all tunable values
│   │   └── LootTableConfig.java         # Fabric LootTableEvents.MODIFY registration
│   ├── item/
│   │   ├── BaseTomeOfExperience.java    # Abstract Item base — use interaction, tooltips, addPoints/removePoints
│   │   ├── TomeOfMinorExperience.java
│   │   ├── TomeOfLesserExperience.java
│   │   ├── TomeOfExperience.java
│   │   ├── TomeOfGreaterExperience.java
│   │   ├── TomeOfSuperiorExperience.java
│   │   └── TomeOfMajorExperience.java
│   │   └── component/
│   │       └── TomeComponent.java       # DataComponent record: Integer experience, Codec + PacketCodec
│   ├── loot/
│   │   ├── SkewedLootNumberProvider.java # Custom NumberProvider: skewed-normal distribution
│   │   ├── SetExperienceLootFunction.java # Custom LootItemConditionalFunction: stamps XP on found tomes
│   │   └── TomesLootTypes.java           # Registers loot function + number provider types into built-in registries
│   └── mixin/
│       └── TomesOfExperienceMixin.java   # Injects into MinecraftServer.loadLevel() — currently empty scaffolding
│
├── main/resources/
│   ├── fabric.mod.json                  # Mod metadata + entrypoints
│   ├── tomes_of_experience.mixins.json  # Mixin config
│   ├── assets/tomes_of_experience/      # Textures, item models, lang files
│   └── data/
│       ├── tomes_of_experience/recipe/  # Six shaped/smithing recipes
│       ├── tomes_of_experience/tags/item/bookshelf_books.json  # Mod-side tag list
│       └── minecraft/tags/item/bookshelf_books.json            # Delegates to mod tag
│
└── client/java/com/hangbunny/client/
    ├── TomesOfExperienceClient.java     # ClientModInitializer: registers shift-tooltip callback
    ├── ClientInputHelper.java           # GLFW shift-key detection (LWJGL, client-only)
    └── config/
        └── ModMenuIntegration.java      # ModMenuApi: opens Cloth Config screen
```

---

## Architecture

### Item inheritance

```
Item (Minecraft)
  └─ BaseTomeOfExperience          ← abstract; handles use(), tooltips, addPointsToTome(), removePointsFromTome()
       ├─ TomeOfMinorExperience
       ├─ TomeOfLesserExperience
       ├─ TomeOfExperience
       ├─ TomeOfGreaterExperience
       ├─ TomeOfSuperiorExperience
       └─ TomeOfMajorExperience
```

Each subclass overrides four accessors that delegate to `TomesOfExperience.CONFIG` (so config changes are live without a restart) and two transfer methods:

| Method | Purpose |
|---|---|
| `getCapacity()` | Max XP points the tome can hold |
| `getEfficiency()` | Fraction of transferred points actually stored |
| `getMinimumLevel()` | Player level required to charge the tome |
| `roundingMethod(float)` | Lower tomes use `Math.floor`; Greater/Superior/Major use `Math.ceil` |
| `transferToTome(stack, player)` | Shift+Right-click: pulls XP from player |
| `transferToPlayer(stack, player)` | Right-click: pushes XP to player |

### Data component

`TomeComponent(Integer experience)` is a Java `record` registered as the data component `tomes_of_experience:tome_data`. It is stamped onto the item stack in:
- `BaseTomeOfExperience.onCraftedPostProcess()` (server, on crafting)
- `BaseTomeOfExperience.getDefaultInstance()` (creative-tab display)
- `SetExperienceLootFunction.run()` (loot table injection)
- `addPointsToTome()` / `removePointsFromTome()` (every use)

The glint effect (`DataComponents.ENCHANTMENT_GLINT_OVERRIDE`) is set/removed in `updateGlint()` after every use.

### Experience transfer model

`player.totalExperience` is unreliable — never use it. Always compute total XP from `player.experienceLevel` and `player.experienceProgress * player.getXpNeededForNextLevel()`.

The transfer granularity per tier:

- **Minor / Lesser:** fractions (⅓ or ½) of the current level's point range
- **Experience / Greater:** the full current level's points + 1 (crosses the level boundary)
- **Superior / Major:** repeats the full-level transfer in a loop (3× and 5× respectively)

On charge (to-tome): `addPointsToTome` applies the efficiency multiplier to the stored amount, consuming the raw amount from the player.  
On discharge (to-player): `removePointsFromTome` delivers raw stored points; no efficiency loss on retrieval.

### Loot injection

`LootTableConfig.init()` registers a `LootTableEvents.MODIFY` listener that appends four loot pools to every vanilla structure chest in `lootContainers`. Each pool:
1. Rolls once
2. Gates on a random-chance condition (per-rarity probability from config)
3. Adds a tome item with a `SetExperienceLootFunction` that uses `SkewedLootNumberProvider`

`SkewedLootNumberProvider` implements a skewed normal distribution (mean=0, deviation=1.2, skew=0.7, bias=−0.8) so chests tend to contain tomes that are lightly rather than fully charged.

Loot injection is skipped entirely when `CONFIG.enable_loot = false`.

### Tags

All six tomes are included in the `tomes_of_experience:bookshelf_books` item tag, which `data/minecraft/tags/item/bookshelf_books.json` delegates to via `#tomes_of_experience:bookshelf_books`. This makes tomes count as books for bookshelf-adjacent mechanics.

---

## Configuration

Config file: `config/tomes_of_experience.json5` (Jankson serializer, written by Cloth Config)  
Accessible in-game at: Mod Menu → Tomes of Experience → Config

All config fields are public non-final fields on `TomesOfExperienceConfig`. Item classes read them through accessor methods so that any in-memory change is immediately reflected — there is no caching of config values in item instances.

---

## External References

**Fabric Developer Guides:** <https://docs.fabricmc.net/develop>

Consult this when working on any Fabric-specific API. Sections most relevant to this codebase:

| Section | When to use it |
|---|---|
| **Items** | Adding item behaviors, use actions, tooltip APIs, creative tab registration |
| **Serialization** | Codec / PacketCodec patterns for data components (`TomeComponent`) |
| **Loom** | Gradle build tasks, split source sets, `runClient`/`runServer` configuration |
| **Loader** | `fabric.mod.json` entrypoints, dependency declarations, environment annotations |
| **Mixins** | Expanding `TomesOfExperienceMixin` if bytecode injection becomes necessary |
| **Data Generation** | Automating recipes, loot tables, or tag JSON files |
| **Miscellaneous → Events** | Fabric event patterns (e.g. `LootTableEvents.MODIFY`, `ItemTooltipCallback`) |

---

## Things to Avoid

- Do not read `player.totalExperience` — it does not report reliable values; derive XP from level + progress instead.
- Do not add a no-arg constructor to tome subclasses — `registerItem` in `TomesOfExperience` passes `Item.Properties` (with its resource key pre-set) to the factory; every tome constructor must accept `Properties`.
- Do not cache config values in fields — always call `TomesOfExperience.CONFIG.<field>` at call time so live config edits take effect.
- Do not add client-side code to `src/main/` — use `src/client/` and declare it under the `"client"` entrypoint in `fabric.mod.json`.
- Do not add third-party dependencies without discussion.
- The mixin (`TomesOfExperienceMixin`) is currently empty scaffolding — do not add logic there without a concrete reason.
