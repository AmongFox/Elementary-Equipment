package com.afox.elementalweapons.initializers;

import com.afox.elementalweapons.items.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Items {
    private static final String MOD_ID = "elemental-weapons";

    private static FireSwordItem FIRE_SWORD;
    private static FrozenSwordItem FROZEN_SWORD;
    private static ElectricSwordItem ELECTRIC_SWORD;
    private static MagicSwordItem MAGIC_SWORD;
    private static AncientSwordItem ANCIENT_SWORD;
    private static MountainSwordItem MOUNTAIN_SWORD;
    private static HurricaneSwordItem HURRICANE_SWORD;
    private static SeaSwordItem SEA_SWORD;

    public static void registerItems() {
        FIRE_SWORD = new FireSwordItem();
        FROZEN_SWORD = new FrozenSwordItem();
        ELECTRIC_SWORD = new ElectricSwordItem();
        MAGIC_SWORD = new MagicSwordItem();
        ANCIENT_SWORD = new AncientSwordItem();
        MOUNTAIN_SWORD = new MountainSwordItem();
        HURRICANE_SWORD = new HurricaneSwordItem();
        SEA_SWORD = new SeaSwordItem();
    }

    public static void registerInRegister() {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "fire_sword"), FIRE_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "frozen_sword"), FROZEN_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "electric_sword"), ELECTRIC_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "magic_sword"), MAGIC_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "ancient_sword"), ANCIENT_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "mountain_sword"), MOUNTAIN_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "hurricane_sword"), HURRICANE_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "sea_sword"), SEA_SWORD);
    }

    public static void addToItemGroup() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries ->
                entries.addAfter(
                        net.minecraft.item.Items.DIAMOND_SWORD,
                        FIRE_SWORD,
                        FROZEN_SWORD,
                        ELECTRIC_SWORD,
                        MAGIC_SWORD,
                        ANCIENT_SWORD,
                        MOUNTAIN_SWORD,
                        HURRICANE_SWORD,
                        SEA_SWORD
                )
        );
    }
}
