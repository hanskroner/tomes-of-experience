package com.hangbunny.loot;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SkewedLootNumberProvider implements NumberProvider {
    final NumberProvider min;
    final NumberProvider max;
    final NumberProvider mean;
    final NumberProvider deviation;
    final NumberProvider skew;
    final NumberProvider bias;

    SkewedLootNumberProvider(NumberProvider min, NumberProvider max, NumberProvider mean, NumberProvider deviation, NumberProvider skew, NumberProvider bias) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.deviation = deviation;
        this.skew = skew;
        this.bias = bias;
    }

    public static final MapCodec<SkewedLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        instance.group(
            NumberProviders.CODEC.fieldOf("min").forGetter(p -> p.min),
            NumberProviders.CODEC.fieldOf("max").forGetter(p -> p.max),
            NumberProviders.CODEC.fieldOf("mean").forGetter(p -> p.mean),
            NumberProviders.CODEC.fieldOf("deviation").forGetter(p -> p.deviation),
            NumberProviders.CODEC.fieldOf("skew").forGetter(p -> p.skew),
            NumberProviders.CODEC.fieldOf("bias").forGetter(p -> p.bias)
        ).apply(instance, SkewedLootNumberProvider::new)
    );

    @Override
    public MapCodec<SkewedLootNumberProvider> codec() {
        return CODEC;
    }

    public static SkewedLootNumberProvider create(float min, float max, float mean, float deviation, float skew, float bias) {
        return new SkewedLootNumberProvider(
            ConstantValue.exactly(min),
            ConstantValue.exactly(max),
            ConstantValue.exactly(mean),
            ConstantValue.exactly(deviation),
            ConstantValue.exactly(skew),
            ConstantValue.exactly(bias));
    }

    public int getInt(LootContext context) {
        return Mth.nextInt(context.getRandom(), this.min.getInt(context), this.max.getInt(context));
    }

    public float getFloat(LootContext context) {
        return Mth.nextFloat(context.getRandom(), this.min.getFloat(context), this.max.getFloat(context));
    }

    public static float nextGaussian(RandomSource random, float mean, float deviation) {
        return (float) (mean + random.nextGaussian() * deviation);
    }

    /*
     * skew -   the degree to which the values cluster around the mode of the distribution; 
     *          higher values mean tighter clustering
     * bias -   the tendency of the mode to approach the min, max or midpoint value; 
     *          positive values bias toward max, negative values toward min
     */
    public int nextSkewedInt(LootContext context) {
        float min = this.min.getInt(context);
        float max = this.max.getInt(context);
        float mean = this.mean.getInt(context);
        float deviation = this.deviation.getInt(context);
        float skew = this.skew.getInt(context);
        float bias = this.bias.getInt(context);

        float range = max - min;
        float mid = min + range / 2.0f;
        float unitGaussian = nextGaussian(context.getRandom(), mean, deviation);
        float biasFactor = (float) Math.exp(bias);

        return (int) Math.round(mid + (range * (biasFactor / (biasFactor + (float) Math.exp(-unitGaussian / skew)) - 0.5f)));
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return ImmutableSet.copyOf(Iterables.concat(
            this.min.getReferencedContextParams(),
            this.max.getReferencedContextParams(),
            this.mean.getReferencedContextParams(),
            this.deviation.getReferencedContextParams(),
            this.skew.getReferencedContextParams(),
            this.bias.getReferencedContextParams()
        ));
    }
}
