package com.trimsreimagined.networking;

import com.trimsreimagined.TrimsReimagined;
import com.trimsreimagined.networking.packet.TrimTogglePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public class TrimMessages {
    public static final Identifier EFFECT_TOGGLE_ID = Identifier.of(TrimsReimagined.MOD_ID, "toggle_effects");

    public static void registerC2SPackets() {

    }

    public static void registerS2CPackets() {
        TrimTogglePayload.register();
    }

}
