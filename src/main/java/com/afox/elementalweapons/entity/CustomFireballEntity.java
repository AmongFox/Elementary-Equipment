package com.afox.elementalweapons.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.util.math.random.Random;

public class CustomFireballEntity extends SmallFireballEntity {
    private static final float FIRE_CHANCE = 0.6F;
    private static final float EXPLOSION_POWER = 3.0F;

    public CustomFireballEntity(World world, PlayerEntity player, double directionX, double directionY, double directionZ) {
        super(world, player, directionX, directionY, directionZ);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        this.getWorld().playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.BLOCKS,
                5.0F,
                0.3F
        );

        super.onCollision(hitResult);

            this.getWorld().createExplosion(
                    this,
                    this.getX(), this.getY(), this.getZ(),
                    EXPLOSION_POWER,
                    true,
                    World.ExplosionSourceType.MOB
            );

            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    Random random = Random.create();

                    if (random.nextFloat() < FIRE_CHANCE) {
                        BlockPos pos = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());
                        BlockPos firePos = pos.add(x, 0, z);

                        if (this.getWorld().getBlockState(firePos).isAir() &&
                                this.getWorld().getBlockState(firePos.down()).isSolidBlock(this.getWorld(), firePos.down())) {

                            this.getWorld().setBlockState(firePos, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            }
            this.discard();
        }
    }
