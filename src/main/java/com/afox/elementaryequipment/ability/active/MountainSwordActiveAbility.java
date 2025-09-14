package com.afox.elementaryequipment.ability.active;

import com.afox.elementaryequipment.events.DelayedTaskScheduler;
import com.afox.elementaryequipment.utils.EffectsUtils;
import com.afox.elementaryequipment.utils.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.enums.Thickness;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class MountainSwordActiveAbility {
    private static final int RADIUS = 16;
    private static final float UPWARD_VELOCITY = 1.6f;
    private static final int EFFECT_DURATION = 20 * 8;

    private static final List<Thickness> THICKNESS_LIST = List.of(
            Thickness.TIP,
            Thickness.FRUSTUM,
            Thickness.MIDDLE
    );

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            List<LivingEntity> livingEntities = EntityUtils.getEntitiesInArea(serverWorld, player.getPos(), RADIUS);
            List<LivingEntity> hostileEntities = EntityUtils.FilterEntity.getEntitiesInScopes(livingEntities, player);

            playSound(serverWorld, player);

            hostileEntities.forEach(entity -> {
                BlockPos blockPos = entity.getBlockPos();
                spawnPointedDripstone(serverWorld, blockPos, player);
                applyEffectsToEntities(entity);

                entity.setVelocity(entity.getVelocity().add(0, UPWARD_VELOCITY, 0));
                entity.velocityModified = true;
            });
        }
    }

    private static void spawnPointedDripstone(ServerWorld serverWorld, BlockPos blockPos, PlayerEntity player) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (dx == 0 && dz == 0) continue;

                    BlockPos spawnPos = blockPos.add(dx, dy, dz);
                    if (isValidPosition(serverWorld, spawnPos.down())) {
                        Random random = serverWorld.getRandom();
                        int height = random.nextBoolean() ? 1 : 2;
                        int delay = 5 + random.nextInt(4) * 3;

                        DelayedTaskScheduler.schedule(delay, () ->
                                placeDripstones(serverWorld, player, spawnPos, height)
                        );
                    }
                }
            }
        }

        if (isValidPosition(serverWorld, blockPos.down())) {
            DelayedTaskScheduler.schedule(5, () ->
                    placeDripstones(serverWorld, player, blockPos, THICKNESS_LIST.size())
            );
        }
    }

    private static boolean isValidPosition(ServerWorld serverWorld, BlockPos pos) {
        BlockState state = serverWorld.getBlockState(pos);
        return state.isFullCube(serverWorld, pos) && !(state.getBlock() instanceof FluidBlock);
    }

    private static void placeDripstones(ServerWorld serverWorld, PlayerEntity player, BlockPos basePos, int height) {
        for (int i = 0; i < height; i++) {

            BlockPos pos = basePos.up(i);
            BlockState state = serverWorld.getBlockState(pos);

            if (state.isReplaceable() || serverWorld.isWater(pos)) {
                Thickness thickness = THICKNESS_LIST.get(i);
                serverWorld.setBlockState(pos, createDripstoneState(serverWorld, pos, thickness));
            } else return;
        }
        placeDristoneSound(serverWorld, player);
        spawnParticles(serverWorld, basePos);
    }

    private static BlockState createDripstoneState(ServerWorld serverWorld, BlockPos blockPos, Thickness thickness) {
        return Blocks.POINTED_DRIPSTONE.getDefaultState()
                .with(PointedDripstoneBlock.VERTICAL_DIRECTION, Direction.UP)
                .with(PointedDripstoneBlock.THICKNESS, thickness)
                .with(PointedDripstoneBlock.WATERLOGGED, serverWorld.isWater(blockPos));
    }

    private static void placeDristoneSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLOCK_STONE_BREAK,
                SoundCategory.BLOCKS,
                1.5F,
                0.3F
        );
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMBIENT_NETHER_WASTES_MOOD.value(),
                SoundCategory.AMBIENT,
                1.5F,
                1.0F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos) {
        serverWorld.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.POINTED_DRIPSTONE.getDefaultState()),
                blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5,
                25,
                0.3, 0.3, 0.3,
                0.1
        );
    }

    private static void applyEffectsToEntities(LivingEntity livingEntity) {
        final Map<StatusEffect, Integer> effects = Map.of(
                StatusEffects.POISON, 1,
                StatusEffects.SLOWNESS, 4,
                StatusEffects.WEAKNESS, 2
        );
        EffectsUtils.applyEffectsToEntity(livingEntity, effects, EFFECT_DURATION);
    }
}
