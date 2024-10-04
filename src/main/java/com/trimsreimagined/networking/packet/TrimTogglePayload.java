package com.trimsreimagined.networking.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TrimTogglePayload(boolean enabled) implements CustomPayload {
    public static final Id<TrimTogglePayload> ID = new Id<>(Identifier.of("trimsreimagined:trim_toggle_payload"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public boolean getEnabled() {
        return enabled;
    }

    //public static final Codec<TrimTogglePayload> CODEC = Codec.unit(TrimTogglePayload::new);
    public static final PacketCodec<RegistryByteBuf, TrimTogglePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, TrimTogglePayload::enabled,
            TrimTogglePayload::new
    );
    //public static final RecordCodecBuilder<RegistryByteBuf, TrimTogglePayload> CODEC = RecordCodecBuilder.of()

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
    }
}
