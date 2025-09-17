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
import java.util.*;

public class MagicSwordActiveAbility {
    private static final Map<UUID, ActiveCircle> activeCircles = new HashMap<>();
    private static final int CIRCLE_RADIUS = 16;
    private static final int EFFECT_DURATION = 20 * 5;
    private static final int CIRCLE_DURATION = 40;
    private static final int MAX_TICKS_BEFORE_ACTION = 60;
    private static int tickCounter = 0;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            playSound(serverWorld, player);
            spawnParticles(serverWorld, player);
            activeCircles.put(player.getUuid(),
                    new ActiveCircle(player, player.getPos(), serverWorld, CIRCLE_DURATION));
        }
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,
                SoundCategory.AMBIENT,
                2.5f,
                0.8f
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.spawnParticles(
                ParticleTypes.WITCH,
                player.getX(), player.getY() + player.getHeight() / 2, player.getZ(),
                45,
                0.8, 0.8, 0.8,
                0.01
        );
    }

    public static void tickAll() {
        tickCounter++;

        if (tickCounter < MAX_TICKS_BEFORE_ACTION) {
            return;
        }

        tickCounter = 0;

        activeCircles.values().removeIf(circle -> !circle.tick());
    }

    private static class ActiveCircle {
        private final PlayerEntity player;
        private final Vec3d center;
        private final ServerWorld serverWorld;
        private int remainingTicks;

        public ActiveCircle(PlayerEntity player, Vec3d center, ServerWorld serverWorld, int durationTicks) {
            this.player = player;
            this.center = center;
            this.serverWorld = serverWorld;
            this.remainingTicks = durationTicks;
        }

        public boolean tick() {
            remainingTicks--;

            if (remainingTicks <= 0) return false;

            spawnCircleParticles();
            applyEffectsToEntities(serverWorld, player);
            return true;
        }

        private void spawnCircleParticles() {
            final double yPos = center.y + 0.3;

            for (int i = 0; i < 360; i += 3) {
                double rad = Math.toRadians(i);
                double x = center.x + CIRCLE_RADIUS * Math.cos(rad);
                double z = center.z + CIRCLE_RADIUS * Math.sin(rad);

                serverWorld.spawnParticles(
                        ParticleTypes.DRAGON_BREATH,
                        x, yPos, z,
                        1, 0, 0, 0, 0
                );
            }

            serverWorld.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    center.x, center.y + 1.5, center.z,
                    1, 0, 0, 0, 0
            );
        }

        private void applyEffectsToEntities(ServerWorld serverWorld, PlayerEntity player) {
            final Map<RegistryEntry<StatusEffect>, Integer> hostileEffects = Map.of(
                    StatusEffects.WEAKNESS, 0,
                    StatusEffects.SLOWNESS, 2
            );

            final Map<RegistryEntry<StatusEffect>, Integer> friendlyEffects = Map.of(
                    StatusEffects.REGENERATION, 0,
                    StatusEffects.ABSORPTION, 0,
                    StatusEffects.SPEED, 0
            );

            List<LivingEntity> livingEntities = EntityUtils.getEntitiesInArea(serverWorld, center, CIRCLE_RADIUS);
            EffectsUtils.applyEffectsInScopesToEntities(livingEntities, hostileEffects, friendlyEffects, player, EFFECT_DURATION);
        }
    }
}
