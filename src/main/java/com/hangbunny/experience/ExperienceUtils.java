package com.hangbunny.experience;

import net.minecraft.entity.player.PlayerEntity;

public class ExperienceUtils {

	public static int getExperiencePoints(PlayerEntity user) {
		int experienceCurrentLevel = ExperienceUtils.getExperienceForLevel(user.experienceLevel);
		int experienceForNextLevel = ExperienceUtils.getExperienceForLevel(user.experienceLevel + 1) - experienceCurrentLevel;

		return (int)(experienceCurrentLevel + (user.experienceProgress * experienceForNextLevel));
	}

	public static int getExperienceForLevel(int level) {
		// https://minecraft.fandom.com/wiki/Experience#Leveling_up

		if (level ==  0) { return 0; }
		if (level <= 16) { return (int) (Math.pow(level, 2) + 6 * level); }
		if (level <= 31) { return (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360); }
		return (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
	}
}