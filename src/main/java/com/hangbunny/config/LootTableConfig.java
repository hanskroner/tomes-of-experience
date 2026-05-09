package com.hangbunny.config;

import java.util.List;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.loot.SetExperienceLootFunction;
import com.hangbunny.loot.SkewedLootNumberProvider;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;

public class LootTableConfig {
    protected static final List<RegistryKey<LootTable>> lootContainers = List.of(
            LootTables.field_472,
            LootTables.field_38438,
            LootTables.field_38439,
            LootTables.field_24048,
            LootTables.field_24049,
            LootTables.field_24047,
            LootTables.field_24046,
            LootTables.field_251,
            LootTables.field_885,
            LootTables.field_274,
            LootTables.field_662,
            LootTables.field_803,
            LootTables.field_751,
            LootTables.field_615,
            LootTables.field_16593,
            LootTables.field_24050,
            LootTables.field_841,
            LootTables.field_880,
            LootTables.field_665,
            LootTables.field_356,
            LootTables.SPAWN_BONUS_CHEST,
            LootTables.field_842,
            LootTables.field_800,
            LootTables.field_683,
            LootTables.field_300,
            LootTables.field_397,
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
            LootTables.field_484);

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
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
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
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(lootChanceUncommon))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfExperience)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityTome, MEAN, DEVIATION, SKEW, BIAS))));

                tableBuilder.pool(poolBuilder);
            }
        });

        // Rare Tomes
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(lootChanceRare))
                        .with(ItemEntry.builder(TomesOfExperience.TomeOfGreaterExperience)
                            .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityGreaterTome, MEAN, DEVIATION, SKEW, BIAS))));

                tableBuilder.pool(poolBuilder);
            }
        });

        // Epic Tomes
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Only affect vanilla loot tables.
            if (source.isBuiltin()
                    && lootContainers.contains(key)) {
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