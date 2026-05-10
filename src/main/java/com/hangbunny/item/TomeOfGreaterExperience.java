package com.hangbunny.item;

import com.hangbunny.TomesOfExperience;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TomeOfGreaterExperience extends BaseTomeOfExperience {

    public TomeOfGreaterExperience(Properties properties) {
        super(properties.stacksTo(6).rarity(Rarity.RARE));
   }

    @Override
    public int getCapacity() {
        return TomesOfExperience.CONFIG.greater_experience_points_capacity;
    }

    @Override
    protected float getEfficiency() {
        return TomesOfExperience.CONFIG.greater_experience_points_efficiency;
    }

    @Override
    protected int getMinimumLevel() {
        return TomesOfExperience.CONFIG.greater_minimum_level;
    }

    @Override
    protected int transferToTome(ItemStack tomeItemStack, Player player) {
        int pointsToNextLevel = player.getXpNeededForNextLevel();
        int pointsCurrentLevel = (int) Math.ceil(player.experienceProgress * (float)pointsToNextLevel);

        // Transfer all the points in the current level plus
        // a single point to go down to the previous level.
        int pointsToTransfer = pointsCurrentLevel + 1;

        // 'player.totalExperience' doesn't seem to report reliable values.
        // Check that we're not transferring more points than the player has by
        // checking both the level and the points in the current level.
        if (player.experienceLevel == 0 && pointsToTransfer > pointsCurrentLevel) {
            pointsToTransfer = pointsCurrentLevel;
        }

        // Try to transfer the points to the tome and subtract the amount that
        // could be transferred from the player.
        int pointsConsumed = this.addPointsToTome(tomeItemStack, pointsToTransfer);
        player.giveExperiencePoints(-pointsConsumed);

        return pointsConsumed;
    }

    @Override
    protected int transferToPlayer(ItemStack tomeItemStack, Player player) {
        int pointsToNextLevel = player.getXpNeededForNextLevel();
        int pointsCurrentLevel = (int) Math.ceil(player.experienceProgress * (float)pointsToNextLevel);
        int pointsNextLevel = pointsToNextLevel - pointsCurrentLevel;

        // Transfer as many points as needed to get to the next level.
        int pointsToTransfer = pointsNextLevel + 1;

        // Try to get the points from the tome - or as many as it can provide - and 
        // add that amount to the player.
        int pointsTransferred = this.removePointsFromTome(tomeItemStack, pointsToTransfer);
        player.giveExperiencePoints(pointsTransferred);

        return pointsTransferred;
    }

    @Override
    protected int roundingMethod(float value) {
        return (int) Math.ceil(value);
    }
}