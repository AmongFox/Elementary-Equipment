package com.afox.elementaryequipment.ability.active;

import com.afox.elementaryequipment.utils.EffectsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class SeaSwordActiveAbility {
    private static final List<SoundEvent> RIPTIDE_SOUNDS = List.of(
            SoundEvents.ITEM_TRIDENT_RIPTIDE_1,
            SoundEvents.ITEM_TRIDENT_RIPTIDE_2,
            SoundEvents.ITEM_TRIDENT_RIPTIDE_3
    );
    private static final int EFFECT_DURATION = 20 * 3;

    public static void execute(World world, PlayerEntity player) {
        if (world.isClient()) return;

        if (world instanceof ServerWorld serverWorld) {
            playSound(serverWorld, player);
            spawnParticles(serverWorld, player);
            applyEffectsToEntities(player);
            RiptideEffect(player);
        }
    }

    private static void RiptideEffect(PlayerEntity player) {
        player.setVelocity(player.getRotationVector().multiply(2.0));
        player.velocityModified = true;
    }

    private static void playSound(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                RIPTIDE_SOUNDS.get(serverWorld.getRandom().nextInt(RIPTIDE_SOUNDS.size())),
                SoundCategory.AMBIENT,
                1.5f,
                0.3f
        );
    }

    private static void spawnParticles(ServerWorld serverWorld, PlayerEntity player) {
        serverWorld.spawnParticles(
                ParticleTypes.BUBBLE,
                player.getX() + 0.5, player.getY(), player.getZ() + 0.5,
                45,
                0.3, 0.3, 0.3,
                0.1
        );
    }

    private static void applyEffectsToEntities(LivingEntity livingEntity) {
        final Map<StatusEffect, Integer> effects = Map.of(
                StatusEffects.WATER_BREATHING, 4,
                StatusEffects.REGENERATION, 3
        );
        EffectsUtils.applyEffectsToEntity(livingEntity, effects, EFFECT_DURATION);
    }
}
