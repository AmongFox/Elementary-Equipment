package com.afox.elementalweapons.ability.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class AncientSwordPassiveAbility {
    private static final float HEAL_AMOUNT = 1.5F;
    private static final float ADD_DAMAGE_AMOUNT = 0.3F;  // 30%

    public static void execute(LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient) return;

        if (attacker.getWorld() instanceof ServerWorld serverWorld) {
            playSound(serverWorld, attacker);
            spawnParticles(serverWorld, target);
        }

        if (attacker instanceof PlayerEntity player) {
            target.damage(attacker.getDamageSources().playerAttack(player), target.getMaxHealth() * ADD_DAMAGE_AMOUNT);
        } else {
            target.damage(attacker.getDamageSources().mobAttack(attacker), target.getMaxHealth() * ADD_DAMAGE_AMOUNT);
        }

        attacker.heal(HEAL_AMOUNT);
    }

    private static void playSound(ServerWorld serverWorld, LivingEntity attacker) {
        serverWorld.playSound(
                null,
                attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                SoundCategory.PLAYERS,
                1F,
                0.9F + serverWorld.getRandom().nextFloat() * 0.2F
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, LivingEntity target) {
        serverWorld.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                target.getX(), target.getY(), target.getZ(),
                25,
                0.5, 0.5, 0.5,
                0.1
        );
    }
}
