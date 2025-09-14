package com.afox.elementaryequipment.ability.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ElectricSwordPassiveAbility {
    private static final int BURN_TIME = 2;

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(target.getWorld());

        if ((attacker.getWorld() instanceof ServerWorld serverWorld) && lightning != null) {
            lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
            serverWorld.spawnEntity(lightning);
            target.setOnFireFor(BURN_TIME);

            playSound(serverWorld, target);
            spawnParticles(serverWorld, target);
        }
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.playSound(
                null,
                target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.AMBIENT,
                2.0f,
                1.0f
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.spawnParticles(
                ParticleTypes.FIREWORK,
                target.getX(), target.getY() + target.getHeight(), target.getZ(),
                20,
                0, 0, 0,
                0.1
        );
    }
}
