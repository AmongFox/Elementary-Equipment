package com.afox.elementalweapons.ability.active;

import com.afox.elementalweapons.utils.EffectsUtils;
import com.afox.elementalweapons.utils.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class AncientSwordActiveAbility {
    private static final int CIRCLE_RADIUS = 24;
    private static final int EFFECT_DURATION = 20 * 15;
    private static final float GRASS_SPAWN_CHANCE = 0.5f; // 40% шанс спавна травы
    private static final float TALL_GRASS_RATIO = 0.3f; // 30% от всей травы будет высокой
    private static final int VERTICAL_RANGE = 3;
    private static final int PARTICLE_COUNT = 60;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            BlockPos blockPos = player.getBlockPos();
            Random random = serverWorld.getRandom();

            List<LivingEntity> livingEntities = EntityUtils.getEntitiesInArea(serverWorld, player.getPos(), CIRCLE_RADIUS);

            playSound(serverWorld, player);
            applyEffectsToEntities(livingEntities, player);
            spawnParticles(serverWorld, blockPos, random);
            spawnGrassInCircle(serverWorld, blockPos, random);
            spawnEvokerFangs(livingEntities, serverWorld, player);
        }

    }

    private static void spawnGrassInCircle(ServerWorld serverWorld, BlockPos center, Random random) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int x = -CIRCLE_RADIUS; x <= CIRCLE_RADIUS; x++) {
            for (int z = -CIRCLE_RADIUS; z <= CIRCLE_RADIUS; z++) {
                for (int yOffset = -VERTICAL_RANGE; yOffset <= VERTICAL_RANGE; yOffset++) {
                    mutablePos.set(center.getX() + x, center.getY() + yOffset, center.getZ() + z);

                    ChunkPos chunkPos = new ChunkPos(mutablePos);
                    if (!serverWorld.isChunkLoaded(chunkPos.x, chunkPos.z)) continue;

                    if (random.nextFloat() <= GRASS_SPAWN_CHANCE &&
                            serverWorld.getBlockState(mutablePos.down()).isOf(Blocks.GRASS_BLOCK) &&
                            serverWorld.getBlockState(mutablePos).isAir()) {

                        if (random.nextFloat() <= TALL_GRASS_RATIO) {
                            placeTallGrass(serverWorld, mutablePos);
                        } else {
                            serverWorld.setBlockState(mutablePos, Blocks.GRASS.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    private static void placeTallGrass(ServerWorld serverWorld, BlockPos pos) {
        if (serverWorld.getBlockState(pos.up()).isAir()) {
            BlockState lowerState = Blocks.TALL_GRASS.getDefaultState()
                    .with(TallPlantBlock.HALF, DoubleBlockHalf.LOWER);
            BlockState upperState = Blocks.TALL_GRASS.getDefaultState()
                    .with(TallPlantBlock.HALF, DoubleBlockHalf.UPPER);

            serverWorld.setBlockState(pos, lowerState);
            serverWorld.setBlockState(pos.up(), upperState);
        }
    }

    private static void spawnEvokerFangs(List<LivingEntity> livingEntities, ServerWorld serverWorld, PlayerEntity player) {
        EntityUtils.FilterEntity.getEntitiesInScopes(livingEntities, player).forEach(target -> {
                EvokerFangsEntity evokerFangs = new EvokerFangsEntity(EntityType.EVOKER_FANGS, serverWorld);
                evokerFangs.setPosition(target.getX(), target.getY(), target.getZ());
                evokerFangs.setOwner(player);
                serverWorld.spawnEntity(evokerFangs);
            });
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        for (int i=0; i < 5; i++) {
            serverWorld.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_GRASS_PLACE,
                    SoundCategory.BLOCKS,
                    0.8f,
                    0.9f + serverWorld.getRandom().nextFloat() * 0.2f
            );
        }
    }

    private static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos, Random random) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double x = blockPos.getX() + (random.nextDouble() - 0.5) * CIRCLE_RADIUS * 2;
            double z = blockPos.getZ() + (random.nextDouble() - 0.5) * CIRCLE_RADIUS * 2;
            double y = blockPos.getY() + random.nextDouble();

            serverWorld.spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    5,
                    0, 0.1, 0,
                    0.1
            );
        }
    }

    private static void applyEffectsToEntities(List<LivingEntity> livingEntities, PlayerEntity player) {
        final Map<StatusEffect, Integer> hostileEffects = Map.of(
                StatusEffects.SLOWNESS, 3,
                StatusEffects.POISON, 1
        );

        final Map<StatusEffect, Integer> friendlyEffects = Map.of(
                StatusEffects.REGENERATION, 0,
                StatusEffects.HEALTH_BOOST, 2,
                StatusEffects.JUMP_BOOST, 1,
                StatusEffects.SPEED, 1
        );

        EffectsUtils.applyEffectsInScopesToEntities(livingEntities, hostileEffects, friendlyEffects, player, EFFECT_DURATION);
    }
}
