package com.afox.elementaryequipment.ability.passive;

import com.afox.elementaryequipment.utils.EffectsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Map;

public class MagicSwordPassiveAbility {
    private static final float DAMAGE_AMOUNT = 8.0F;
    private static final int EFFECT_DURATION = 20 * 6;

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            target.damage(attacker.getDamageSources().magic(), DAMAGE_AMOUNT);

            playSound(serverWorld, attacker);
            spawnParticles(serverWorld, target);
            applyEffectsToEntities(target);
        }
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.playSound(
                null,
                attacker.getBlockPos(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.spawnParticles(
                ParticleTypes.WITCH,
                target.getX(), target.getY() + target.getHeight() / 2, target.getZ(),
                25,
                0.5, 0.5, 0.5,
                0.01
        );
    }

    private static void applyEffectsToEntities(LivingEntity target) {
        final Map<StatusEffect, Integer> effects = Map.of(
                StatusEffects.BLINDNESS, 5,
                StatusEffects.DARKNESS, 5,
                StatusEffects.UNLUCK, 5
        );
        EffectsUtils.applyEffectsToEntity(target, effects, EFFECT_DURATION);
    }
}
