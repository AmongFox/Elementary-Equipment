package com.afox.elementalweapons.ability.active;

import com.afox.elementalweapons.entity.CustomFireballEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FireSwordActiveAbility {
    private static final int PARTICLE_COUNT = 35;
    private static final double FIRE_CIRCLE_RADIUS = 0.6;
    private static final double FIREBALL_SPAWN_OFFSET = 2.0;
    private static final double FIREBALL_VELOCITY_FACTOR = 0.1;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            playSound(serverWorld, player);
            spawnParticles(serverWorld, player);
            spawnFireball(serverWorld, player);
        }
    }

    private static void spawnFireball(ServerWorld world, PlayerEntity player) {
        CustomFireballEntity fireball = createFireballEntity(world, player);
        world.spawnEntity(fireball);
    }

    private static CustomFireballEntity createFireballEntity(ServerWorld serverWorld, PlayerEntity player) {
        Vec3d look = player.getRotationVec(1.0F);

        Vec3d velocity = look.multiply(FIREBALL_VELOCITY_FACTOR);
        Vec3d position = player.getPos()
                .add(0, player.getEyeHeight(player.getPose()), 0)
                .add(look.multiply(FIREBALL_SPAWN_OFFSET));

        CustomFireballEntity fireball = new CustomFireballEntity(serverWorld, player, velocity.x, velocity.y, velocity.z);
        fireball.setPosition(position);

        return fireball;
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_GHAST_SHOOT,
                SoundCategory.PLAYERS,
                2.0f,
                1.0f
        );
    }

    public static void spawnParticles(ServerWorld serverWorld, PlayerEntity player) {
        Random random = serverWorld.getRandom();
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d centerPos = eyePos.add(lookVec.multiply(1.5));

        Vec3d up = Math.abs(lookVec.y) > 0.9 ? new Vec3d(0, 0, 1) : new Vec3d(0, 1, 0);
        Vec3d right = lookVec.crossProduct(up).normalize();
        Vec3d realUp = lookVec.crossProduct(right).normalize();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = 2 * Math.PI * i / PARTICLE_COUNT;
            Vec3d offset = right.multiply(Math.cos(angle) * FIRE_CIRCLE_RADIUS)
                    .add(realUp.multiply(Math.sin(angle) * FIRE_CIRCLE_RADIUS));
            Vec3d particlePos = centerPos.add(offset);
            Vec3d velocity = offset.normalize().multiply(0.15);

            serverWorld.spawnParticles(
                    ParticleTypes.FLAME,
                    particlePos.x, particlePos.y, particlePos.z,
                    1,
                    velocity.x, velocity.y, velocity.z,
                    0.02
            );
        }

        for (int i = 0; i < 8; i++) {
            serverWorld.spawnParticles(
                    ParticleTypes.LAVA,
                    centerPos.x, centerPos.y, centerPos.z,
                    1,
                    (random.nextDouble() - 0.5) * 0.5,
                    random.nextDouble() * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    0.01
            );
        }
    }
}
