package com.hangbunny.item;

import java.util.List;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.item.component.TomeComponent;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public abstract class BaseTomeOfExperience extends Item {

    public static final String EXPERIENCE = "experience";

    public BaseTomeOfExperience() { 
        super(new Settings());
    }

    public BaseTomeOfExperience(Settings settings) {
        super(settings);
    }

    // Use accessors for configuration values.
    // This allows subclasses to override them while allowing for live
    // changes in the configuration file.
    protected int getCapacity() {
        return 0;
    }

    protected float getEfficiency() {
        return 1.0f;
    }

    protected int getMinimumLevel() {
        return 0;
    }

    // Overridden by the subclasses to control how many
    // experience points are transferred.
    protected int transferToTome(ItemStack tomeItemStack, PlayerEntity user) {
        return 0;
    }

    protected int transferToPlayer(ItemStack tomeItemStack, PlayerEntity user) {
        return 0;
    }

    protected int roundingMethod(float value) {
        return 0;
    }
   
    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = super.getDefaultStack();
        itemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(0));

        return itemStack;
    }

    @Override
    public void onCraft(ItemStack itemStack, World world) {
        if (world.isClient) { return; }

        itemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(0));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        TomeComponent tomeComponent = itemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
        int tomeExperience = tomeComponent.experience();
        if (tomeExperience > 0) {
            // Lacking a 'switch' statement with range capabilities, do this
            // instead to add flavor text to the tooltip depending on how full
            // of experience points the tome is.
            int percentFull = (int) Math.floor(((float) tomeExperience / this.getCapacity()) * 10);
            switch (percentFull) {
                case 0:
                case 1:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0-20"));
                    break;
                case 2:
                case 3:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.20-40"));
                    break;
                case 4:
                case 5:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.40-60"));
                    break;
                case 6:
                case 7:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.60-80"));
                    break;
                case 8:
                case 9:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.80-100"));
                    break; 
                default:
                    tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.100"));
                    break; 
            }
        } else {
            tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0"));
        }

        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.tomes_of_experience.tome_of_experience.tooltip.points", tomeExperience, this.getCapacity()));
        }

        super.appendTooltip(itemStack, context, tooltip, type);
    }

    /**
     * Removes up to the amount passed in of experience points from a tome.
     * 
     * Returns the amount of experience points that were delivered - which
     * might be a different amount than those that were passed in depending
     * on the amount of points stored in the tome.
     * 
     * @param tomeItemStack the tome that will lose points
     * @param points        the amount of experience points to remove from the tome
     * @return              the amount of experience points delivered
     */
    protected int removePointsFromTome(ItemStack tomeItemStack, int points) {
        // Make sure points aren't negative.
        if (points <= 0) { return 0; }

        // Get tome configuration.
        TomeComponent tomeComponent = tomeItemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
        int pointsTome = tomeComponent.experience();

        int pointsDelivered = 0;
        if (points >= pointsTome) {
            pointsDelivered = pointsTome;
            pointsTome = 0;
        } else {
            pointsDelivered = points;
            pointsTome -= points;
        }

        tomeItemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(pointsTome));
        return pointsDelivered;
    }

    /**
     * Add up to the amount passed in of experience points to a tome.
     * Depending on the tome's efficiency and capacity, the amount of points
     * stored might be different than the amount passed it.
     * 
     * Returns the amount of experience points that were consumed - which
     * might be a different amount than those that were passed in.
     * 
     * @param tomeItemStack the tome that will receive points
     * @param points        the amount of experience points to add to the tome
     * @return              the amount of experience points consumed
     */
    protected int addPointsToTome(ItemStack tomeItemStack, int points) {
        // Make sure points aren't negative.
        if (points <= 0) { return 0; }

        // Get tome configuration.
        TomeComponent tomeComponent = tomeItemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
        int capacity = this.getCapacity();
        float efficiency = this.getEfficiency();
        int pointsTome = tomeComponent.experience();

        int pointsStored = 0;
        int pointsConsumed = 0;
        if (points + pointsTome >= capacity) {
            // If there isn't enough capacity in the tome to store the requested amount,
            // fill the tome to it's maximum capacity but ensure that the amount consumed
            // takes into consideration the efficiency losses that apply to the stored amount.
            pointsStored = capacity - pointsTome;
            pointsConsumed = this.roundingMethod(pointsStored / efficiency);
            pointsTome = capacity;
        } else {
            // Apply efficiency loses. Store the amount after efficiency loses, but consume
            // the amount that was originally requested to store.
            pointsStored = this.roundingMethod(points * efficiency);
            pointsConsumed = points;
            pointsTome += pointsStored;
        }

        tomeItemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(pointsTome));
        return pointsConsumed;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Get tome configuration.
        TomeComponent tomeComponent = itemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
        int pointsTome = tomeComponent.experience();

        if (!world.isClient) {
            // Split a tome stack if there's more than one tome
            // in it. This mimics the behavior of vanilla stacks.
            if (itemStack.getCount() > 1) {
                int splitStackSlot = user.getInventory().getEmptySlot();

                ItemStack newItemStack = itemStack.split(itemStack.getCount() - 1);
                user.getInventory().insertStack(splitStackSlot, newItemStack);
            }

            if (user.isSneaking()) {
                // Enforce the minimum experience level the player must have
                // to be allowed to transfer experience points to the tome.
                if (user.experienceLevel < this.getMinimumLevel()) {
                    return TypedActionResult.pass(itemStack);
                }

                // Transfer experience points from the player to the tome.
                int pointsRemoved = this.transferToTome(itemStack, user);

                if (pointsRemoved == 0) {
                    return TypedActionResult.pass(itemStack);
                }
            } else {
                // Transfer experience points from the tome to the player.
                int pointsAdded = this.transferToPlayer(itemStack, user);
                if (pointsAdded == 0) {
                    return TypedActionResult.pass(itemStack);
                }
            }
        } else {
            int pointsToNextLevel = user.getNextLevelExperience();
            int pointsCurrentLevel = (int) Math.ceil(user.experienceProgress * (float)pointsToNextLevel);
            // On the client side
            // Don't swing the player's arm when:
            //   The player has no experience points
            //   The tome is either empty or full
            //   No experience points would be transferred in either direction
            //   The player doesn't have the minimum experience level required by the tome
            boolean canReceivePoints = (pointsTome < this.getCapacity()) && !(user.experienceLevel == 0 && pointsCurrentLevel == 0);
            boolean canProvidePoints = (pointsTome > 0);
            boolean playerHasPoints = (user.experienceLevel > 0) || (user.experienceProgress > 0);

            if ((user.isSneaking() && !playerHasPoints)
                || (user.isSneaking() && pointsTome >= this.getCapacity())
                || (user.isSneaking() && !canReceivePoints)
                || (user.isSneaking() && (user.experienceLevel < this.getMinimumLevel()))
                || (!user.isSneaking() && pointsTome <= 0)
                || (!user.isSneaking() && !canProvidePoints)) {
                return TypedActionResult.pass(itemStack);
            }
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean hasGlint(ItemStack itemStack) {
        // Make the tome glint if it has experience points stored.
        TomeComponent tomeComponent = itemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
        int tomeExperience = tomeComponent.experience();

        return (tomeExperience > 0);
    }
}