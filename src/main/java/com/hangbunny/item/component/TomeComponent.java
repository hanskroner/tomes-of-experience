package com.hangbunny.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

public record TomeComponent(Integer experience) {
    public static final String EXPERIENCE = "experience";

    public static final TomeComponent DEFAULT = new TomeComponent(0);

    public static final Codec<TomeComponent> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf(EXPERIENCE).forGetter(TomeComponent::experience))
                    .apply(instance, TomeComponent::new));

    public static final StreamCodec<ByteBuf, TomeComponent> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.INT, TomeComponent::experience, TomeComponent::new);
}