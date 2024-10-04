package com.trimsreimagined;

import com.trimsreimagined.effect.ModEffects;
import com.trimsreimagined.item.ModItems;
import com.trimsreimagined.networking.TrimMessages;
import com.trimsreimagined.networking.packet.TrimTogglePayload;
import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrimsReimagined implements ModInitializer {
	public static final String MOD_ID = "trimsreimagined";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModEffects.registerEffects();
		ModItems.registerModItems();
		TrimMessages.registerS2CPackets();
		initNetworking();
	}

	private void initNetworking() {
		ServerPlayNetworking.registerGlobalReceiver(TrimTogglePayload.ID, ((payload, context) -> {
			ServerPlayerEntity player = context.player();
			((PlayerMixinMethodAccess)player).trimsreimagined$toggleTrimEffects();
			boolean trimStatus = ((PlayerMixinMethodAccess)player).trimsreimagined$getTrimEffectStatus();
			String trimEffectMessage = String.format("message.trimsreimagined.trimEffects.%s", trimStatus);
			player.sendMessage(Text.translatable(trimEffectMessage));
		}));
	}

}