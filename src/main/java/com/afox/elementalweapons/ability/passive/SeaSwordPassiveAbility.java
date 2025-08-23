package com.afox.elementalweapons.ability.passive;

import com.afox.elementalweapons.utils.EffectsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

public class SeaSwordPassiveAbility {
    private static final int EFFECT_DURATION = 20 * 10;

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            playSound(serverWorld, attacker);
            spawnParticles(serverWorld, attacker);
            applyEffectsToEntity(target, attacker);
        }
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.playSound(
                null,
                attacker.getBlockPos(),
                SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,
                SoundCategory.PLAYERS,
                1.0F,
                0.2F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.spawnParticles(
                ParticleTypes.BUBBLE,
                attacker.getX(), attacker.getY() + attacker.getHeight() / 2, attacker.getZ(),
                45,
                0.5, 0.5, 0.5,
                0.1
        );
    }

    private static void applyEffectsToEntity(LivingEntity target, LivingEntity attacker) {
        final Map<StatusEffect, Integer> hostileEffects = Map.of(
                StatusEffects.POISON, 0
        );
        final Map<StatusEffect, Integer> friendlyEffects = Map.of(
                StatusEffects.DOLPHINS_GRACE, 2,
                StatusEffects.CONDUIT_POWER, 0
        );
        EffectsUtils.applyEffectsToEntity(target, hostileEffects, EFFECT_DURATION / 2);
        EffectsUtils.applyEffectsToEntity(attacker, friendlyEffects, EFFECT_DURATION);
    }
}
