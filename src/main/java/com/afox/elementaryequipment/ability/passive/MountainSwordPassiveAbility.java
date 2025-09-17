package com.afox.elementaryequipment.ability.passive;

import com.afox.elementaryequipment.utils.EffectsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

public class MountainSwordPassiveAbility {
    private static final int EFFECT_DURATION = 20 * 16;
    public static void execute(@SuppressWarnings("unused") LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            playSound(serverWorld, attacker);
            spawnParticles(serverWorld, attacker);
            applyEffectsToEntity(attacker);
        }
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.playSound(
                null,
                attacker.getBlockPos(),
                SoundEvents.BLOCK_ANVIL_LAND,
                SoundCategory.PLAYERS,
                1.0F,
                0.2F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.spawnParticles(
                ParticleTypes.SMOKE,
                attacker.getX(), attacker.getY() + attacker.getHeight() / 2, attacker.getZ(),
                25,
                0.5, 0.5, 0.5,
                0.1
        );
    }

    private static void applyEffectsToEntity(LivingEntity attacker) {
        final Map<RegistryEntry<StatusEffect>, Integer> effects = Map.of(
                StatusEffects.RESISTANCE, 1,
                StatusEffects.FIRE_RESISTANCE, 0,
                StatusEffects.ABSORPTION, 1
        );
        EffectsUtils.applyEffectsToEntity(attacker, effects, EFFECT_DURATION);
    }
}
