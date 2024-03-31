package com.hangbunny;

import com.hangbunny.config.LootTableConfig;
import com.hangbunny.config.TomesOfExperienceConfig;
import com.hangbunny.item.TomeOfMinorExperience;
import com.hangbunny.item.TomeOfLesserExperience;
import com.hangbunny.item.TomeOfExperience;
import com.hangbunny.item.TomeOfGreaterExperience;
import com.hangbunny.item.TomeOfSuperiorExperience;
import com.hangbunny.item.TomeOfMajorExperience;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup.StackVisibility;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomesOfExperience implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("tomes-of-experience");

	public static TomesOfExperienceConfig CONFIG = new TomesOfExperienceConfig();

	// Tomes of Experience Items
	public static final Item TomeOfMinorExperience = new TomeOfMinorExperience(new FabricItemSettings());
	public static final Item TomeOfLesserExperience = new TomeOfLesserExperience(new FabricItemSettings());
	public static final Item TomeOfExperience = new TomeOfExperience(new FabricItemSettings());
	public static final Item TomeOfGreaterExperience = new TomeOfGreaterExperience(new FabricItemSettings());
	public static final Item TomeOfSuperiorExperience = new TomeOfSuperiorExperience(new FabricItemSettings());
	public static final Item TomeOfMajorExperience = new TomeOfMajorExperience(new FabricItemSettings());

	// Tomes of Experience Item Group
	private static final ItemGroup TomesOfExperienceItemGroup = FabricItemGroup.builder()
		.icon(() -> new ItemStack(TomeOfSuperiorExperience))
		.displayName(Text.translatable("group.tomes_of_experience.tomes_of_experience"))
			.entries((context, entries) -> {
				entries.add(TomeOfMinorExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
				entries.add(TomeOfLesserExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
				entries.add(TomeOfExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
				entries.add(TomeOfGreaterExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
				entries.add(TomeOfSuperiorExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
				entries.add(TomeOfMajorExperience.getDefaultStack(), StackVisibility.PARENT_AND_SEARCH_TABS);
		})
		.build();
	
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// Configuration
		AutoConfig.register(TomesOfExperienceConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TomesOfExperienceConfig.class).getConfig();

		// Items
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_minor_experience"), TomeOfMinorExperience);
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_lesser_experience"), TomeOfLesserExperience);
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_experience"), TomeOfExperience);
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_greater_experience"), TomeOfGreaterExperience);
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_superior_experience"), TomeOfSuperiorExperience);
		Registry.register(Registries.ITEM, new Identifier("tomes_of_experience", "tome_of_major_experience"), TomeOfMajorExperience);

		// Item Group
		Registry.register(Registries.ITEM_GROUP, new Identifier("tomes_of_experience", "tomes_of_experience"), TomesOfExperienceItemGroup);

		// Loot Tables
		if (CONFIG.enable_loot) {
			LootTableConfig.init();
		}
	}
}