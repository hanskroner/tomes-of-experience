package com.hangbunny;

import com.hangbunny.config.TomesOfExperienceConfig;
import com.hangbunny.item.TomeOfExperience;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemGroup.StackVisibility;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomesOfExperience implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("tomes-of-experience");

	public static TomesOfExperienceConfig CONFIG = new TomesOfExperienceConfig();

	// Base Tome of Experience Item
	public static final Item TomeOfExperience = new TomeOfExperience(new FabricItemSettings());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// Configuration
		AutoConfig.register(TomesOfExperienceConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TomesOfExperienceConfig.class).getConfig();

		// Items
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_experience"), TomeOfExperience);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
			content.add(TomeOfExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
		});
	}
}