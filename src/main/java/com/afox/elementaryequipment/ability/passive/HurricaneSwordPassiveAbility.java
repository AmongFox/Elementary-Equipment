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
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class HurricaneSwordPassiveAbility {
    private static final float KNOCKBACK_STRENGTH = 1.4F;
    private static final float KNOCKBACK_UP_STRENGTH = 0.8F;
    private static final int EFFECT_DURATION = 20 * 3;

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            playSound(serverWorld, attacker);
            spawnParticles(serverWorld, target);
            applyEffectsToEntities(target);
        }


        Vec3d knockbackDirection = target.getPos().subtract(attacker.getPos()).normalize();
        target.addVelocity(
                knockbackDirection.x * KNOCKBACK_STRENGTH,
                KNOCKBACK_UP_STRENGTH,
                knockbackDirection.z * KNOCKBACK_STRENGTH
        );
        target.velocityModified = true;
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.playSound(
                null,
                attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS,
                3.6F,
                1.6F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.spawnParticles(
                ParticleTypes.SWEEP_ATTACK,
                target.getX(), target.getY() + 0.5, target.getZ(),
                1,
                0, 0, 0,
                0.1
        );
    }

    private static void applyEffectsToEntities(LivingEntity target) {
        final Map<RegistryEntry<StatusEffect>, Integer> effects = Map.of(
                StatusEffects.SLOWNESS, 1,
                StatusEffects.WEAKNESS, 0
        );
        EffectsUtils.applyEffectsToEntity(target, effects, EFFECT_DURATION);
    }
}
