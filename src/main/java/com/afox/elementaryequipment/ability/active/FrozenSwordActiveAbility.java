package com.afox.elementaryequipment.ability.active;

import com.afox.elementaryequipment.utils.EffectsUtils;
import com.afox.elementaryequipment.utils.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class FrozenSwordActiveAbility {
    private static final int RADIUS = 16;
    private static final float AMOUNT_DAMAGE = 4.0F;
    private static final int EFFECT_DURATION = 20 * 10;
    private static final float FREEZE_BLOCK_CHANCE = 0.2F;
    private static final float PLACE_SNOWY_CHANCE = 0.4F;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            List<LivingEntity> livingEntities = EntityUtils.getEntitiesInArea(serverWorld, player.getPos(), RADIUS);
            List<LivingEntity> hostileEntities = EntityUtils.FilterEntity.getEntitiesInScopes(livingEntities, player);

            playSound(serverWorld, player);
            spawnParticles(serverWorld, player);
            freezeBlocks(serverWorld, player);

            hostileEntities.forEach(hostileEntity -> {
                applyEffectsToEntity(hostileEntity);
                hostileEntity.damage(serverWorld.getDamageSources().freeze(), AMOUNT_DAMAGE);
            });
        }
    }

    private static void freezeBlocks(ServerWorld serverWorld, PlayerEntity player) {
        Random random = serverWorld.getRandom();

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++)  {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    BlockPos blockPos = player.getBlockPos().add(x, y, z);
                    BlockState currentState = serverWorld.getBlockState(blockPos);

                    if (random.nextFloat() < FREEZE_BLOCK_CHANCE && currentState.isIn(BlockTags.BASE_STONE_OVERWORLD) ||
                            serverWorld.isWater(blockPos)) {
                        playSoundFreezeBlock(serverWorld, blockPos);
                        serverWorld.setBlockState(blockPos, Blocks.ICE.getDefaultState());
                    }

                    if (random.nextFloat() < PLACE_SNOWY_CHANCE) {
                        if (currentState.isFullCube(serverWorld, blockPos)) {
                            BlockPos abovePos = blockPos.up();
                            BlockState aboveState = serverWorld.getBlockState(abovePos);

                            if (aboveState.isReplaceable() && !aboveState.getFluidState().isStill()) {
                                serverWorld.setBlockState(abovePos, Blocks.SNOW.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void playSoundFreezeBlock(ServerWorld serverWorld, BlockPos blockPos) {
        serverWorld.playSound(
                null,
                blockPos,
                SoundEvents.BLOCK_GLASS_BREAK,
                SoundCategory.BLOCKS,
                0.3F,
                1.0F
        );
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_GLASS_BREAK,
                SoundCategory.HOSTILE,
                1.0f,
                0.5f
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, PlayerEntity player) {
        for (int i = 0; i < 100; i++) {
            Random random = serverWorld.getRandom();

            double x = player.getX() + (random.nextDouble() - 0.5) * 10;
            double y = player.getY() + (random.nextDouble() - 0.5) * 10;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 10;

            serverWorld.spawnParticles(
                    ParticleTypes.SNOWFLAKE,
                    x, y, z,
                    2,
                    0, 0, 0,
                    0.1
            );
        }
    }

    private static void applyEffectsToEntity(LivingEntity livingEntity) {
        final Map<RegistryEntry<StatusEffect>, Integer> effects = Map.of(
                StatusEffects.SLOWNESS, 100,
                StatusEffects.WEAKNESS, 100,
                StatusEffects.MINING_FATIGUE, 100
        );
        EffectsUtils.applyEffectsToEntity(livingEntity, effects, EFFECT_DURATION);
    }
}
