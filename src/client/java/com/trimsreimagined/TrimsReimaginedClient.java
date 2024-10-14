package com.trimsreimagined;

import com.trimsreimagined.networking.packet.TrimTogglePayload;
import com.trimsreimagined.utils.PlayerMixinMethodAccess;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class TrimsReimaginedClient implements ClientModInitializer {

	private static KeyBinding toggleTrimEffectsKey;
	public static final String KEY_CATEGORY_TRIMS = "key.category.trimsreimagined.category";
	public static final String KEY_TOGGLE_EFFECTS = "key.trimsreimagined.toggle_effects";

	public static void registerKeyInputs() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(toggleTrimEffectsKey.wasPressed()) {
				ClientPlayNetworking.send(new TrimTogglePayload(true));
				ClientPlayerEntity playerEntity = client.player;
                if(playerEntity != null) {
					((PlayerMixinMethodAccess) playerEntity).trimsreimagined$toggleTrimEffects();
				}
			}
		});
	}

	public static void register() {
		toggleTrimEffectsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				KEY_TOGGLE_EFFECTS,
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				KEY_CATEGORY_TRIMS));
		registerKeyInputs();
	}

	@Override
	public void onInitializeClient() {
		register();
	}
}