package com.afox.elementaryequipment.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class WorldBiomeUtils {
    public static final TagKey<Biome> SNOWY_BIOMES = TagKey.of(
            RegistryKeys.BIOME,
            new Identifier("new-weapons", "snowy_biomes")
    );

    public static final TagKey<Biome> JUNGLE_BIOMES = TagKey.of(
            RegistryKeys.BIOME,
            new Identifier("new-weapons", "jungle_biomes")
    );

    public static boolean isInSnowyBiome(LivingEntity livingEntity) {
        RegistryEntry<Biome> biome = livingEntity.getWorld().getBiome(livingEntity.getBlockPos());
        return biome.isIn(SNOWY_BIOMES);
    }

    public static boolean isInJungleBiome(LivingEntity livingEntity) {
        RegistryEntry<Biome> biome = livingEntity.getWorld().getBiome(livingEntity.getBlockPos());
        return biome.isIn(JUNGLE_BIOMES);
    }
}
