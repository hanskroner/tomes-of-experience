package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.experience.ExperienceUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Rarity;

public class TomeOfLesserExperience extends BaseTomeOfExperience {

    public TomeOfLesserExperience(Settings settings) {
        super(settings.maxCount(6).rarity(Rarity.COMMON));
   }

    @Override
    protected int getCapacity() {
        return TomesOfExperience.CONFIG.lesser_experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.lesser_experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.minor_minimum_level;
    }

    @Override
    protected int pointsToTransferToTome(PlayerEntity user) {
        int currentLevel = user.experienceLevel;
        int halfOfCurrentLevel = (int) Math.ceil((ExperienceUtils.getExperienceForLevel(currentLevel + 1) - ExperienceUtils.getExperienceForLevel(currentLevel)) * 0.5f);
        int pointsCurrentLevel = ExperienceUtils.getExperiencePoints(user) - ExperienceUtils.getExperienceForLevel(currentLevel);

        // Transfer half of the points that the current level could hold.
        // If the player has less points than that, just transfer enough
        // points to go down to the previous level.
        int pointsToTransfer = pointsCurrentLevel > halfOfCurrentLevel
            ? halfOfCurrentLevel
            : pointsCurrentLevel + 1;

        return pointsToTransfer;
    }

    @Override
    protected int pointsToTransferToPlayer(PlayerEntity user) {
        int currentLevel = user.experienceLevel;
        int halfOfCurrentLevel = (int) Math.ceil((ExperienceUtils.getExperienceForLevel(currentLevel + 1) - ExperienceUtils.getExperienceForLevel(currentLevel)) * 0.5f);
        int pointsTotal = ExperienceUtils.getExperiencePoints(user);
        int pointsNextLevel = ExperienceUtils.getExperienceForLevel(currentLevel + 1);
        
        // Transfer a third of the points that the current level could hold.
        // If the transfer would cause the player to level up, just transfer enough
        // points to go up to the next level.
        int pointsToTransfer = pointsTotal + halfOfCurrentLevel > pointsNextLevel
            ? pointsNextLevel - pointsTotal + 1
            : halfOfCurrentLevel;

        return pointsToTransfer;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.floor(value);
    }
}