package com.hangbunny.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class TomesLootTypes {
    // In 26.1, the registry holds the MapCodec directly
    public static final MapCodec<SetExperienceLootFunction> SET_EXPERIENCE = Registry.register(
        BuiltInRegistries.LOOT_FUNCTION_TYPE,
        Identifier.fromNamespaceAndPath("tomes_of_experience", "set_experience"),
        SetExperienceLootFunction.CODEC
    );

    // NUMBER_PROVIDER_TYPES is the correct plural registry name in 26.1
    public static final MapCodec<SkewedLootNumberProvider> SKEWED_PROVIDER = Registry.register(
        BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE,
        Identifier.fromNamespaceAndPath("tomes_of_experience", "skewed"),
        SkewedLootNumberProvider.CODEC
    );

    public static void bootstrap() {}
}