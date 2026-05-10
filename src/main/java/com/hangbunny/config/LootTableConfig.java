package com.hangbunny.config;

import java.util.List;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.loot.SetExperienceLootFunction;
import com.hangbunny.loot.SkewedLootNumberProvider;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class LootTableConfig {
    protected static final List<ResourceKey<LootTable>> lootContainers = List.of(
        BuiltInLootTables.ABANDONED_MINESHAFT,
        BuiltInLootTables.ANCIENT_CITY,
        BuiltInLootTables.ANCIENT_CITY_ICE_BOX,
        BuiltInLootTables.BASTION_BRIDGE,
        BuiltInLootTables.BASTION_HOGLIN_STABLE,
        BuiltInLootTables.BASTION_OTHER,
        BuiltInLootTables.BASTION_TREASURE,
        BuiltInLootTables.BURIED_TREASURE,
        BuiltInLootTables.DESERT_PYRAMID,
        BuiltInLootTables.END_CITY_TREASURE,
        BuiltInLootTables.IGLOO_CHEST,
        BuiltInLootTables.JUNGLE_TEMPLE,
        BuiltInLootTables.NETHER_BRIDGE,
        BuiltInLootTables.PILLAGER_OUTPOST,
        BuiltInLootTables.RUINED_PORTAL,
        BuiltInLootTables.SHIPWRECK_MAP,
        BuiltInLootTables.SHIPWRECK_SUPPLY,
        BuiltInLootTables.SHIPWRECK_TREASURE,
        BuiltInLootTables.STRONGHOLD_CORRIDOR,
        BuiltInLootTables.STRONGHOLD_CROSSING,
        BuiltInLootTables.SPAWN_BONUS_CHEST,
        BuiltInLootTables.STRONGHOLD_LIBRARY,
        BuiltInLootTables.UNDERWATER_RUIN_BIG,
        BuiltInLootTables.UNDERWATER_RUIN_SMALL,
        BuiltInLootTables.WOODLAND_MANSION,
        BuiltInLootTables.VILLAGE_ARMORER,
        BuiltInLootTables.VILLAGE_BUTCHER,
        BuiltInLootTables.VILLAGE_CARTOGRAPHER,
        BuiltInLootTables.VILLAGE_DESERT_HOUSE,
        BuiltInLootTables.VILLAGE_FISHER,
        BuiltInLootTables.VILLAGE_FLETCHER,
        BuiltInLootTables.VILLAGE_MASON,
        BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
        BuiltInLootTables.VILLAGE_SHEPHERD,
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
        BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
        BuiltInLootTables.VILLAGE_TANNERY,
        BuiltInLootTables.VILLAGE_TEMPLE,
        BuiltInLootTables.VILLAGE_TOOLSMITH,
        BuiltInLootTables.VILLAGE_WEAPONSMITH,
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE
    );


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

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin() && lootContainers.contains(key)) {
                
                // Common Pool
                tableBuilder.pool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(lootChanceCommon))
                    .add(LootItem.lootTableItem(TomesOfExperience.TomeOfMinorExperience)
                        .setWeight(6)
                        .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityMinorTome, MEAN, DEVIATION, SKEW, BIAS))))
                    .add(LootItem.lootTableItem(TomesOfExperience.TomeOfLesserExperience)
                        .setWeight(4)
                        .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityLesserTome, MEAN, DEVIATION, SKEW, BIAS))))
                    .build()
                );

                // Uncommon Pool
                tableBuilder.pool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(lootChanceUncommon))
                    .add(LootItem.lootTableItem(TomesOfExperience.TomeOfExperience)
                        .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityTome, MEAN, DEVIATION, SKEW, BIAS))))
                    .build()
                );

                // Rare Pool
                tableBuilder.pool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(lootChanceRare))
                    .add(LootItem.lootTableItem(TomesOfExperience.TomeOfGreaterExperience)
                        .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityGreaterTome, MEAN, DEVIATION, SKEW, BIAS))))
                    .build()
                );

                // Epic Pool
                tableBuilder.pool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .when(LootItemRandomChanceCondition.randomChance(lootChanceEpic))
                    .add(LootItem.lootTableItem(TomesOfExperience.TomeOfSuperiorExperience)
                        .setWeight(5)
                        .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacitySuperiorTome, MEAN, DEVIATION, SKEW, BIAS))))
                    .add(LootItem.lootTableItem(TomesOfExperience.TomeOfMajorExperience)
                        .setWeight(1)
                        .apply(SetExperienceLootFunction.builder(SkewedLootNumberProvider.create(0.0f, (float) capacityMajorTome, MEAN, DEVIATION, SKEW, BIAS))))
                    .build()    
                );
            }
        });
    }
}