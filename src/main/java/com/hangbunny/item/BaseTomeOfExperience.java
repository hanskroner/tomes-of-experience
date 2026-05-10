package com.hangbunny.item;

import java.util.function.Consumer;

import com.hangbunny.TomesOfExperience;
import com.hangbunny.item.component.TomeComponent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public abstract class BaseTomeOfExperience extends Item {

    public static final String EXPERIENCE = "experience";

    public BaseTomeOfExperience() { 
        super(new Properties());
    }

    public BaseTomeOfExperience(Properties properties) {
        super(properties);
    }

    // Use accessors for configuration values.
    // This allows subclasses to override them while allowing for live
    // changes in the configuration file.
    public int getCapacity() {
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
    protected int transferToTome(ItemStack tomeItemStack, Player user) {
        return 0;
    }

    protected int transferToPlayer(ItemStack tomeItemStack, Player user) {
        return 0;
    }

    protected int roundingMethod(float value) {
        return 0;
    }
   
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemStack = super.getDefaultInstance();
        itemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(0));

        return itemStack;
    }

    @Override
    public void onCraftedPostProcess(ItemStack itemStack, Level level) {
        if (level.isClientSide()) { return; }

        itemStack.set(TomesOfExperience.TOME_DATA, new TomeComponent(0));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag type) {
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
                    tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0-20"));
                    break;
                case 2:
                case 3:
                    tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.20-40"));
                    break;
                case 4:
                case 5:
                    tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.40-60"));
                    break;
                case 6:
                case 7:
                    tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.60-80"));
                    break;
                case 8:
                case 9:
                    tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.80-100"));
                    break; 
                default:
                    tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.100"));
                    break; 
            }
        } else {
            tooltip.accept(Component.translatable("item.tomes_of_experience.tome_of_experience.tooltip.0"));
        }

        super.appendHoverText(itemStack, context, display, tooltip, type);
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
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Get tome configuration.
        TomeComponent tomeComponent = itemStack.getOrDefault(TomesOfExperience.TOME_DATA, TomeComponent.DEFAULT);
        int pointsTome = tomeComponent.experience();

        if (!level.isClientSide()) {
            // Split a tome stack if there's more than one tome
            // in it. This mimics the behavior of vanilla stacks.
            if (itemStack.getCount() > 1) {
                int splitStackSlot = player.getInventory().getFreeSlot();

                ItemStack newItemStack = itemStack.split(itemStack.getCount() - 1);
                player.getInventory().setItem(splitStackSlot, newItemStack);
            }

            if (player.isShiftKeyDown()) {
                // Enforce the minimum experience level the player must have
                // to be allowed to transfer experience points to the tome.
                if (player.experienceLevel < this.getMinimumLevel()) {
                    return InteractionResult.PASS;
                }

                // Transfer experience points from the player to the tome.
                int pointsRemoved = this.transferToTome(itemStack, player);

                if (pointsRemoved == 0) {
                    return InteractionResult.PASS;
                }
            } else {
                // Transfer experience points from the tome to the player.
                int pointsAdded = this.transferToPlayer(itemStack, player);
                if (pointsAdded == 0) {
                    return InteractionResult.PASS;
                }
            }
        } else {
            int pointsToNextLevel = player.getXpNeededForNextLevel();
            int pointsCurrentLevel = (int) Math.ceil(player.experienceProgress * (float)pointsToNextLevel);
            // On the client side
            // Don't swing the player's arm when:
            //   The player has no experience points
            //   The tome is either empty or full
            //   No experience points would be transferred in either direction
            //   The player doesn't have the minimum experience level required by the tome
            boolean canReceivePoints = (pointsTome < this.getCapacity()) && !(player.experienceLevel == 0 && pointsCurrentLevel == 0);
            boolean canProvidePoints = (pointsTome > 0);
            boolean playerHasPoints = (player.experienceLevel > 0) || (player.experienceProgress > 0);

            if ((player.isShiftKeyDown() && !playerHasPoints)
                || (player.isShiftKeyDown() && pointsTome >= this.getCapacity())
                || (player.isShiftKeyDown() && !canReceivePoints)
                || (player.isShiftKeyDown() && (player.experienceLevel < this.getMinimumLevel()))
                || (!player.isShiftKeyDown() && pointsTome <= 0)
                || (!player.isShiftKeyDown() && !canProvidePoints)) {
                return InteractionResult.PASS;
            }
        }

        this.updateGlint(itemStack);
        return InteractionResult.SUCCESS;
    }

    public void updateGlint(ItemStack stack) {
        TomeComponent data = stack.get(TomesOfExperience.TOME_DATA);
        boolean shouldGlint = data != null && data.experience() > 0;

        if (shouldGlint) {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        } else {
            stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        }
    }
}