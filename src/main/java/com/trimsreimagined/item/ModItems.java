package com.trimsreimagined.item;

import com.trimsreimagined.TrimsReimagined;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item TRIM_DECRYPTER = registerItem("trim_decrypter", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(TrimsReimagined.MOD_ID, name), item);
    }

    public static void registerModItems() {
        TrimsReimagined.LOGGER.info("Registering mod items for " + TrimsReimagined.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(TRIM_DECRYPTER);
        });
    }
}
