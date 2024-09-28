package com.hangbunny.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record TomeComponent(Integer experience) {
    public static final String EXPERIENCE = "experience";

    public static final TomeComponent DEFAULT = new TomeComponent(0);

    public static final Codec<TomeComponent> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.INT.fieldOf(EXPERIENCE).forGetter(TomeComponent::experience))
                    .apply(instance, TomeComponent::new));

    public static final PacketCodec<ByteBuf, TomeComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, TomeComponent::experience, TomeComponent::new);
}