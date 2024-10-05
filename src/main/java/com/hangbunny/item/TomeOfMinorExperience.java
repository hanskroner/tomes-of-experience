package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class TomeOfMinorExperience extends BaseTomeOfExperience {

    public TomeOfMinorExperience(Settings settings) {
        super(settings.maxCount(6).rarity(Rarity.COMMON));
   }

    @Override
    protected int getCapacity() {
        return TomesOfExperience.CONFIG.minor_experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.minor_experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.minor_minimum_level;
    }

    @Override
    protected int transferToTome(ItemStack tomeItemStack, PlayerEntity user) {
        int pointsToNextLevel = user.getNextLevelExperience();
        int pointsCurrentLevel = (int) Math.ceil(user.experienceProgress * (float)pointsToNextLevel);

        // Transfer a third of the points that the current level could hold.
        // If the player has less points than that, just transfer enough
        // points to go down to the previous level.
        int thirdOfCurrentLevel = (int) Math.ceil((float)pointsToNextLevel / 3f);
        int pointsToTransfer = pointsCurrentLevel > thirdOfCurrentLevel
            ? thirdOfCurrentLevel
            : pointsCurrentLevel + 1;
        if (pointsToTransfer > user.totalExperience) { pointsToTransfer = user.totalExperience; }

        // Try to transfer the points to the tome and subtract the amount that
        // could be transferred from the player.
        int pointsConsumed = this.addPointsToTome(tomeItemStack, pointsToTransfer);
        user.addExperience(-pointsConsumed);

        return pointsConsumed;
    }

    @Override
    protected int transferToPlayer(ItemStack tomeItemStack, PlayerEntity user) {
        int pointsTotal = user.totalExperience;
        int pointsToNextLevel = user.getNextLevelExperience();
        int pointsCurrentLevel = (int) Math.ceil(user.experienceProgress * (float)pointsToNextLevel);
        int pointsNextLevel = pointsTotal - pointsCurrentLevel + pointsToNextLevel;

        // Transfer a third of the points that the current level could hold.
        // If the transfer would cause the player to level up, just transfer enough
        // points to go up to the next level.
        int thirdOfCurrentLevel = (int) Math.ceil((float)pointsToNextLevel / 3f);
        int pointsToTransfer = pointsTotal + thirdOfCurrentLevel > pointsNextLevel
            ? pointsNextLevel - pointsTotal + 1
            : thirdOfCurrentLevel;

        // Try to get the points from the tome - or as many as it can provide - and 
        // add that amount to the player.
        int pointsTransferred = this.removePointsFromTome(tomeItemStack, pointsToTransfer);
        user.addExperience(pointsTransferred);

        return pointsTransferred;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.floor(value);
    }
}