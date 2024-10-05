package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class TomeOfExperience extends BaseTomeOfExperience {

    public TomeOfExperience(Settings settings) {
        super(settings.maxCount(6).rarity(Rarity.UNCOMMON));
   }

    @Override
    protected int getCapacity() {
        return TomesOfExperience.CONFIG.experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.minimum_level;
    }

    @Override
    protected int transferToTome(ItemStack tomeItemStack, PlayerEntity user) {
        int pointsToNextLevel = user.getNextLevelExperience();
        int pointsCurrentLevel = (int) Math.ceil(user.experienceProgress * (float)pointsToNextLevel);

        // Transfer all the points in the current level plus
        // a single point to go down to the previous level.
        int pointsToTransfer = pointsCurrentLevel + 1;
        if (pointsToTransfer > user.totalExperience) { pointsToTransfer = user.totalExperience; }

        // Try to transfer the points to the tome and subtract the amount that
        // could be transferred from the player.
        int pointsConsumed = this.addPointsToTome(tomeItemStack, pointsToTransfer);
        user.addExperience(-pointsConsumed);

        return pointsConsumed;
    }

    @Override
    protected int transferToPlayer(ItemStack tomeItemStack, PlayerEntity user) {
        int pointsToNextLevel = user.getNextLevelExperience();

        // Transfer as many points as needed to get to the next level.
        int pointsToTransfer = pointsToNextLevel;

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