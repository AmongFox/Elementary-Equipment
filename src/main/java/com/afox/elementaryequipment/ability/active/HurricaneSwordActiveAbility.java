package com.afox.elementaryequipment.ability.active;

import com.afox.elementaryequipment.utils.EffectsUtils;
import com.afox.elementaryequipment.utils.EntityUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class HurricaneSwordActiveAbility {
    private static final int RADIUS = 20;
    private static final float KNOCKBACK_STRENGTH = 2.8F;
    private static final float KNOCKBACK_UP_STRENGTH = 1.8F;
    private static final int EFFECT_DURATION = 20 * 10;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            List<LivingEntity> livingEntities = EntityUtils.getEntitiesInArea(serverWorld, player.getPos(), RADIUS);
            List<LivingEntity> hostileEntities = EntityUtils.FilterEntity.getEntitiesInScopes(livingEntities, player);

            playSound(serverWorld, player);

            hostileEntities.forEach(livingEntity -> {
                Vec3d knockbackDirection = livingEntity.getPos().subtract(player.getPos()).normalize();
                livingEntity.addVelocity(
                        knockbackDirection.x * KNOCKBACK_STRENGTH,
                        KNOCKBACK_UP_STRENGTH,
                        knockbackDirection.z * KNOCKBACK_STRENGTH
                );
                livingEntity.velocityModified = true;

                spawnParticles(serverWorld, livingEntity);
                applyEffectsToEntities(livingEntity);
            });
        }
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                SoundCategory.PLAYERS,
                3.6F,
                1F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity livingEntity) {
        serverWorld.spawnParticles(
                ParticleTypes.EXPLOSION,
                livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(),
                1,
                0, 0, 0,
                0.1
        );
    }

    private static void applyEffectsToEntities(LivingEntity livingEntity) {
        final Map<RegistryEntry<StatusEffect>, Integer> effects = Map.of(
                StatusEffects.SLOWNESS, 4,
                StatusEffects.WEAKNESS, 2
        );
        EffectsUtils.applyEffectsToEntity(livingEntity, effects, EFFECT_DURATION);
    }
}
