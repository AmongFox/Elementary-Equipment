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

public class FrozenSwordPassiveAbility {
    private static final int EFFECT_DURATION = 20 * 5;

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            playSound(serverWorld, target);
            spawnParticles(serverWorld, target);
            applyEffectsToEntity(target);
            target.setFrozenTicks(EFFECT_DURATION);
        }
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.playSound(
                null,
                target.getBlockPos(),
                SoundEvents.BLOCK_GLASS_BREAK,
                SoundCategory.HOSTILE,
                1.0F,
                1.2F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.spawnParticles(
                ParticleTypes.SNOWFLAKE,
                target.getX(), target.getY() + target.getHeight() / 2, target.getZ(),
                25,
                0.5, 0.5, 0.5,
                0.1
        );
    }

    private static void applyEffectsToEntity(LivingEntity target) {
        final Map<StatusEffect, Integer> effects = Map.of(
                StatusEffects.SLOWNESS, 3,
                StatusEffects.WEAKNESS, 100,
                StatusEffects.MINING_FATIGUE, 2
        );
        EffectsUtils.applyEffectsToEntity(target, effects, EFFECT_DURATION);
    }
}
