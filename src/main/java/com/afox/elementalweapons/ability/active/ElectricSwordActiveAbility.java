package com.afox.elementalweapons.ability.active;

import com.afox.elementalweapons.events.DelayedTaskScheduler;
import com.afox.elementalweapons.utils.EffectsUtils;
import com.afox.elementalweapons.utils.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ElectricSwordActiveAbility {
    private static final Map<UUID, ElectricSwordActiveAbility.ActiveMobParticle> activeMobParticles = new HashMap<>();
    private static final int RADIUS = 30;
    private static final int MOB_QUANTITY = 5;
    private static final int LIGHTNING_DAMAGE = 20;
    private static final int EFFECT_DURATION = 20 * 6;
    private static final int MIN_TICK_FOR_SOUNDS = 40;
    private static final int MAX_TICK_FOR_SOUNDS = 160;
    private static final int STEP_TICK_FOR_SOUNDS = 30;
    private static final int MAX_TICKS_BEFORE_ACTION = 40;
    private static int tickCounter = 0;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            spawnParticles(serverWorld, player);
            scheduleSounds(serverWorld, player);
            scheduleWeather(serverWorld);
            processMobs(serverWorld, player);
        }
    }

    private static void scheduleSounds(ServerWorld serverWorld, PlayerEntity player) {
        for (int i = MIN_TICK_FOR_SOUNDS; i < MAX_TICK_FOR_SOUNDS; i += STEP_TICK_FOR_SOUNDS) {
            int randomNumber = 20 + serverWorld.random.nextInt(6) * 16;
            DelayedTaskScheduler.schedule(100 + randomNumber, () -> playSound(serverWorld, player));
        }
    }

    private static void scheduleWeather(ServerWorld serverWorld) {
        DelayedTaskScheduler.schedule(100, () -> serverWorld.setWeather(0, 400, true, true));
    }

    private static void processMobs(ServerWorld serverWorld, PlayerEntity player) {
        List<LivingEntity> livingEntities = EntityUtils.getEntitiesInArea(serverWorld, player.getPos(), RADIUS);
        EntityUtils.FilterEntity.getEntitiesInScopes(livingEntities, player)
            .stream()
            .filter(mobEntity -> canSpawnLightning(serverWorld, mobEntity.getBlockPos()))
            .limit(MOB_QUANTITY)
            .forEach(livingEntity -> {
                activeMobParticles.put(livingEntity.getUuid(), new ActiveMobParticle(livingEntity, serverWorld, 26));
                DelayedTaskScheduler.schedule(300, () -> {
                    if (livingEntity.isAlive()) {
                        summonCustomLightningBolt(serverWorld, livingEntity);
                        applyEffectsToEntities(livingEntity);
                    }
                });
            });
    }

    public static boolean canSpawnLightning(World world, BlockPos pos) {
        return world.isSkyVisible(pos);
    }

    private static void summonCustomLightningBolt(ServerWorld serverWorld, LivingEntity livingEntity) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
        lightning.setPosition(livingEntity.getPos());
        lightning.setCosmetic(true);
        serverWorld.spawnEntity(lightning);

        livingEntity.damage(serverWorld.getDamageSources().lightningBolt(), LIGHTNING_DAMAGE);
        livingEntity.setOnFireFor(100);
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.AMBIENT,
                0.3f + serverWorld.getRandom().nextFloat() * 0.6f,
                0.3f + serverWorld.getRandom().nextFloat() * 0.8f
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.spawnParticles(
                ParticleTypes.FIREWORK,
                player.getX(), player.getY(), player.getZ(),
                20,
                0.3, 0.3, 0.3,
                0.1
        );
    }

    private static void applyEffectsToEntities(LivingEntity livingEntity) {
        final Map<StatusEffect, Integer> effects = Map.of(
                StatusEffects.SLOWNESS, 5,
                StatusEffects.WEAKNESS, 1
        );
        EffectsUtils.applyEffectsToEntity(livingEntity, effects, EFFECT_DURATION);
    }

    public static void tickAll() {
        tickCounter++;

        if (tickCounter < MAX_TICKS_BEFORE_ACTION) {
            return;
        }

        tickCounter = 0;

        activeMobParticles.values().removeIf(circle -> !circle.tick());
    }

    private static class ActiveMobParticle {
        private final Entity entity;
        private final ServerWorld serverWorld;
        private int remainingTicks;

        public ActiveMobParticle(Entity entity, ServerWorld serverWorld, int duration) {
            this.entity = entity;
            this.serverWorld = serverWorld;
            this.remainingTicks = duration;
        }

        public boolean tick() {
            remainingTicks--;

            if (remainingTicks <= 0) {
                return false;
            }

            spawnMobParticles();
            return true;
        }

        private void spawnMobParticles() {
            for (int i = 0; i < 360; i += 40) {
                double radians = Math.toRadians(i);
                double offsetX = Math.cos(radians) * -1;
                double offsetZ = Math.sin(radians) * -1;

                serverWorld.spawnParticles(
                        ParticleTypes.FIREWORK,
                        entity.getX() + offsetX,
                        entity.getY() + 1.0,
                        entity.getZ() + offsetZ,
                        3,
                        0, 0, 0,
                        0.1
                );
            }
        }
    }
}