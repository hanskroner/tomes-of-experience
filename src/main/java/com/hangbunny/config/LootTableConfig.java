package com.hangbunny.config;

import java.util.List;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.loot.SetExperienceLootFunction;
import com.hangbunny.loot.SkewedLootNumberProvider;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class LootTableConfig {
    protected static final List<Identifier> lootContainers = List.of(
            LootTables.ABANDONED_MINESHAFT_CHEST,
            LootTables.ANCIENT_CITY_CHEST,
            LootTables.ANCIENT_CITY_ICE_BOX_CHEST,
            LootTables.BASTION_BRIDGE_CHEST,
            LootTables.BASTION_HOGLIN_STABLE_CHEST,
            LootTables.BASTION_OTHER_CHEST,
            LootTables.BASTION_TREASURE_CHEST,
            LootTables.BURIED_TREASURE_CHEST,
            LootTables.DESERT_PYRAMID_CHEST,
            LootTables.END_CITY_TREASURE_CHEST,
            LootTables.IGLOO_CHEST_CHEST,
            LootTables.JUNGLE_TEMPLE_CHEST,
            LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST,
            LootTables.NETHER_BRIDGE_CHEST,
            LootTables.PILLAGER_OUTPOST_CHEST,
            LootTables.RUINED_PORTAL_CHEST,
            LootTables.SHIPWRECK_MAP_CHEST,
            LootTables.SHIPWRECK_SUPPLY_CHEST,
            LootTables.SHIPWRECK_TREASURE_CHEST,
            LootTables.SIMPLE_DUNGEON_CHEST,
            LootTables.SPAWN_BONUS_CHEST,
            LootTables.STRONGHOLD_CORRIDOR_CHEST,
            LootTables.STRONGHOLD_CROSSING_CHEST,
            LootTables.STRONGHOLD_LIBRARY_CHEST,
            LootTables.UNDERWATER_RUIN_BIG_CHEST,
            LootTables.UNDERWATER_RUIN_SMALL_CHEST,
            LootTables.VILLAGE_ARMORER_CHEST,
            LootTables.VILLAGE_BUTCHER_CHEST,
            LootTables.VILLAGE_CARTOGRAPHER_CHEST,
            LootTables.VILLAGE_DESERT_HOUSE_CHEST,
            LootTables.VILLAGE_FISHER_CHEST,
            LootTables.VILLAGE_FLETCHER_CHEST,
            LootTables.VILLAGE_MASON_CHEST,
            LootTables.VILLAGE_PLAINS_CHEST,
            LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
            LootTables.VILLAGE_SHEPARD_CHEST,
            LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
            LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
            LootTables.VILLAGE_TANNERY_CHEST,
            LootTables.VILLAGE_TEMPLE_CHEST,
            LootTables.VILLAGE_TOOLSMITH_CHEST,
            LootTables.VILLAGE_WEAPONSMITH_CHEST,
            LootTables.WOODLAND_MANSION_CHEST);

    public static void init() {
        // Skewed Normal Distribution
        // The distribution of experience points in the tomes is skewed towards
        // the 'min' side of the range.
        // All tomes are skewed equally.
        final float MEAN = 0.0f;
        final float DEVIATION = 1.2f;
        final float SKEW = 0.7f;
        final float BIAS = -0.8f;      

        // Loot probabilities
        float lootChanceCommon = TomesOfExperience.CONFIG.common_loot_chance;
        float lootChanceUncommon = TomesOfExperience.CONFIG.uncommon_loot_chance;
        float lootChanceRare = TomesOfExperience.CONFIG.rare_loot_chance;
        float lootChanceEpic = TomesOfExperience.CONFIG.epic_loot_chance;

        // Tome capacities
        int capacityMinorTome = TomesOfExperience.CONFIG.minor_experience_points_capacity;
        int capacityLesserTome = TomesOfExperience.CONFIG.lesser_experience_points_capacity;
        int capacityTome = TomesOfExperience.CONFIG.experience_points_capacity;
        int capacityGreaterTome = TomesOfExperience.CONFIG.greater_experience_points_capacity;
        int capacitySuperiorTome = TomesOfExperience.CONFIG.superior_experience_points_capacity;
        int capacityMajorTome = TomesOfExperience.CONFIG.major_experience_points_capacity;

        // Common Tomes
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(2))
                        .conditionally(RandomChanceLootCondition.builder(lootChanceCommon))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfMinorExperience)
                            .weight(6)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityMinorTome, MEAN, DEVIATION, SKEW, BIAS))))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfLesserExperience)
                            .weight(4)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityLesserTome, MEAN, DEVIATION, SKEW, BIAS))));

                tableBuilder.pool(poolBuilder);
            }
        });

        // Uncommon Tomes
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(lootChanceUncommon))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfExperience)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityTome, MEAN, DEVIATION, SKEW, BIAS))));

                tableBuilder.pool(poolBuilder);
            }
        });

        // Rare Tomes
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(lootChanceRare))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfGreaterExperience)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityGreaterTome, MEAN, DEVIATION, SKEW, BIAS))));

                tableBuilder.pool(poolBuilder);
            }
        });

        // Epic Tomes
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(lootChanceEpic))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfSuperiorExperience)
                            .weight(5)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacitySuperiorTome, MEAN, DEVIATION, SKEW, BIAS))))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfMajorExperience)
                            .weight(1)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityMajorTome, MEAN, DEVIATION, SKEW, BIAS))));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}