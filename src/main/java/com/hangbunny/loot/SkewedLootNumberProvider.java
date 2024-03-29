package com.hangbunny.loot;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.math.MathHelper;

public class SkewedLootNumberProvider implements LootNumberProvider {
    final LootNumberProvider min;
    final LootNumberProvider max;
    final LootNumberProvider mean;
    final LootNumberProvider deviation;
    final LootNumberProvider skew;
    final LootNumberProvider bias;

    SkewedLootNumberProvider(LootNumberProvider min, LootNumberProvider max, LootNumberProvider mean, LootNumberProvider deviation, LootNumberProvider skew, LootNumberProvider bias) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.deviation = deviation;
        this.skew = skew;
        this.bias = bias;
    }

    public LootNumberProviderType getType() {
        return LootNumberProviderTypes.UNIFORM;
    }

    public static SkewedLootNumberProvider create(float min, float max, float mean, float deviation, float skew, float bias) {
        return new SkewedLootNumberProvider(
            ConstantLootNumberProvider.create(min),
            ConstantLootNumberProvider.create(max),
            ConstantLootNumberProvider.create(mean),
            ConstantLootNumberProvider.create(deviation),
            ConstantLootNumberProvider.create(skew),
            ConstantLootNumberProvider.create(bias));
    }

    public int nextInt(LootContext context) {
        return MathHelper.nextInt(context.getRandom(), this.min.nextInt(context), this.max.nextInt(context));
    }

    public float nextFloat(LootContext context) {
        return MathHelper.nextFloat(context.getRandom(), this.min.nextFloat(context), this.max.nextFloat(context));
    }

    /*
     * skew -   the degree to which the values cluster around the mode of the distribution; 
     *          higher values mean tighter clustering
     * bias -   the tendency of the mode to approach the min, max or midpoint value; 
     *          positive values bias toward max, negative values toward min
     */
    public int nextSkewedInt(LootContext context) {
        float min = this.min.nextInt(context);
        float max = this.max.nextInt(context);
        float mean = this.mean.nextInt(context);
        float deviation = this.deviation.nextInt(context);
        float skew = this.skew.nextInt(context);
        float bias = this.bias.nextInt(context);

        float range = max - min;
        float mid = min + range / 2.0F;
        float unitGaussian = MathHelper.nextGaussian(context.getRandom(), mean, deviation);
        float biasFactor = (float) Math.exp(bias);

        return (int) Math.round(mid + (range * (biasFactor / (biasFactor + (float) Math.exp(-unitGaussian / skew)) - 0.5F)));
    }

    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.copyOf(Iterables.concat(
            this.min.getRequiredParameters(),
            this.max.getRequiredParameters(),
            this.mean.getRequiredParameters(),
            this.deviation.getRequiredParameters(),
            this.skew.getRequiredParameters(),
            this.bias.getRequiredParameters()
        ));
    }
}
