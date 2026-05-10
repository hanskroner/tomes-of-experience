package com.hangbunny;

import com.hangbunny.config.LootTableConfig;
import com.hangbunny.config.TomesOfExperienceConfig;
import com.hangbunny.item.TomeOfMinorExperience;
import com.hangbunny.item.TomeOfLesserExperience;
import com.hangbunny.item.BaseTomeOfExperience;
import com.hangbunny.item.TomeOfExperience;
import com.hangbunny.item.TomeOfGreaterExperience;
import com.hangbunny.item.TomeOfSuperiorExperience;
import com.hangbunny.item.component.TomeComponent;
import com.hangbunny.loot.TomesLootTypes;
import com.hangbunny.item.TomeOfMajorExperience;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomesOfExperience implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("tomes-of-experience");

	public static TomesOfExperienceConfig CONFIG = new TomesOfExperienceConfig();

	// Tomes of Experience Components
	public static final DataComponentType<TomeComponent> TOME_DATA = registerComponent("tome_data", builder -> builder.persistent(TomeComponent.CODEC).networkSynchronized(TomeComponent.PACKET_CODEC));

	// Tomes of Experience Items
	public static final Item TomeOfMinorExperience = new TomeOfMinorExperience(new Item.Properties());
	public static final Item TomeOfLesserExperience = new TomeOfLesserExperience(new Item.Properties());
	public static final Item TomeOfExperience = new TomeOfExperience(new Item.Properties());
	public static final Item TomeOfGreaterExperience = new TomeOfGreaterExperience(new Item.Properties());
	public static final Item TomeOfSuperiorExperience = new TomeOfSuperiorExperience(new Item.Properties());
	public static final Item TomeOfMajorExperience = new TomeOfMajorExperience(new Item.Properties());

	// Tomes of Experience Item Group
	private static final CreativeModeTab TomesOfExperienceItemGroup = FabricCreativeModeTab.builder()
		.icon(() -> new ItemStack(TomeOfSuperiorExperience))
		.title(Component.translatable("group.tomes_of_experience.tomes_of_experience"))
			.displayItems((context, entries) -> {
				entries.accept(TomeOfMinorExperience.getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
				entries.accept(TomeOfLesserExperience.getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
				entries.accept(TomeOfExperience.getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
				entries.accept(TomeOfGreaterExperience.getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
				entries.accept(TomeOfSuperiorExperience.getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
				entries.accept(TomeOfMajorExperience.getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
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
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("tomes_of_experience", "tome_of_minor_experience"), TomeOfMinorExperience);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("tomes_of_experience", "tome_of_lesser_experience"), TomeOfLesserExperience);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("tomes_of_experience", "tome_of_experience"), TomeOfExperience);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("tomes_of_experience", "tome_of_greater_experience"), TomeOfGreaterExperience);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("tomes_of_experience", "tome_of_superior_experience"), TomeOfSuperiorExperience);
		Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("tomes_of_experience", "tome_of_major_experience"), TomeOfMajorExperience);

		// Item Group
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("tomes_of_experience", "tomes_of_experience"), TomesOfExperienceItemGroup);

		// Loot Tables
		if (CONFIG.enable_loot) {
			LootTableConfig.init();
		}

		TomesLootTypes.bootstrap();
	}

	private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE, 
			Identifier.fromNamespaceAndPath("tomes_of_experience", id), 
			builderOperator.apply(DataComponentType.builder()).build()
		);
	}
}