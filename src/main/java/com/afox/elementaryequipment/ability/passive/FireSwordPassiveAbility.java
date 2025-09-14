package com.afox.elementaryequipment.ability.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

public class FireSwordPassiveAbility {
    private static final int FIRE_DURATION = 3;

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            playSound(serverWorld, target);
            spawnParticles(serverWorld, target);
        }

        target.setOnFireFor(FIRE_DURATION);
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.playSound(
                null,
                target.getBlockPos(),
                SoundEvents.ITEM_FIRECHARGE_USE,
                target.getSoundCategory(),
                0.8f,
                1.2f
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.spawnParticles(
                ParticleTypes.FLAME,
                target.getX(), target.getY() + target.getHeight() / 2, target.getZ(),
                25,
                0.5, 0.5, 0.5,
                0.1
        );
    }
}
