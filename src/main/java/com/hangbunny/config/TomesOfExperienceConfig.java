package com.hangbunny.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "tomes_of_experience")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class TomesOfExperienceConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean enable_loot = true;
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public float common_loot_chance = 0.10f;
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public float uncommon_loot_chance = 0.07f;
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public float rare_loot_chance = 0.05f;
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public float epic_loot_chance = 0.01f;

    @ConfigEntry.Category("minor_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int minor_experience_points_capacity = 160;
    @ConfigEntry.Category("minor_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float minor_experience_points_efficiency = 0.90f;
    @ConfigEntry.Category("minor_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int minor_minimum_level = 0;

    @ConfigEntry.Category("lesser_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int lesser_experience_points_capacity = 315;
    @ConfigEntry.Category("lesser_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float lesser_experience_points_efficiency = 0.90f;
    @ConfigEntry.Category("lesser_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int lesser_minimum_level = 0;

    @ConfigEntry.Category("tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int experience_points_capacity = 550;
    @ConfigEntry.Category("tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float experience_points_efficiency = 0.85f;
    @ConfigEntry.Category("tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int minimum_level = 5;

    @ConfigEntry.Category("greater_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int greater_experience_points_capacity = 1395;
    @ConfigEntry.Category("greater_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float greater_experience_points_efficiency = 0.85f;
    @ConfigEntry.Category("greater_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int greater_minimum_level = 10;

    @ConfigEntry.Category("superior_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int superior_experience_points_capacity = 2920;
    @ConfigEntry.Category("superior_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float superior_experience_points_efficiency = 0.90f;
    @ConfigEntry.Category("superior_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int superior_minimum_level = 15;

    @ConfigEntry.Category("major_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int major_experience_points_capacity = 5345;
    @ConfigEntry.Category("major_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public float major_experience_points_efficiency = 0.95f;
    @ConfigEntry.Category("major_tome_of_experience")
    @ConfigEntry.Gui.Tooltip
    public int major_minimum_level = 20;
}
