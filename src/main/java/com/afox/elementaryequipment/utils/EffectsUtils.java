package com.afox.elementaryequipment.utils;

import com.afox.elementaryequipment.config.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectsUtils {
    private static final Set<String> ENTITY_SCOPES = ModConfig.getConfig().general.entityScopes;

    public static void applyEffectsToEntity(
            LivingEntity livingEntity,
            Map<StatusEffect, Integer> effects,
            int effectDuration
    ) {
        effects.forEach((effect, amplifier) -> livingEntity.addStatusEffect(
                new StatusEffectInstance(effect, effectDuration, amplifier, false, false)
        ));
    }

    public static void applyEffectsToEntities(
            List<LivingEntity> livingEntities,
            Map<StatusEffect, Integer> effects,
            int effectDuration
    ) {
        for (LivingEntity livingEntity : livingEntities) {
            effects.forEach((effect, amplifier) ->
                    livingEntity.addStatusEffect(new StatusEffectInstance(
                            effect,
                            effectDuration,
                            amplifier,
                            false,
                            false
                    ))
            );
        }
    }

    public static void applyEffectsInScopesToEntities(
            List<LivingEntity> livingEntities,
            Map<StatusEffect, Integer> badEffects,
            Map<StatusEffect, Integer> goodEffects,
            PlayerEntity player,
            int effectDuration
    ) {
        final List<LivingEntity> hostileEntities;
        final List<LivingEntity> friendlyEntities;

        if (ENTITY_SCOPES.contains("all")) {
            hostileEntities = livingEntities.stream()
                    .filter(entity -> entity != player && !(entity instanceof TameableEntity && ((TameableEntity) entity).isTamed()))
                    .toList();

            friendlyEntities = livingEntities.stream()
                    .filter(entity -> entity == player || isTamedByPlayer(entity, player))
                    .toList();

        }
        else {
            hostileEntities = livingEntities.stream()
                    .filter(entity -> {
                        if (entity == player) {
                            return false;
                        }

                        for (String scope : ENTITY_SCOPES) {
                            if (matchesScope(entity, scope)) {
                                return true;
                            }
                        }
                        return false;
                    }).toList();

            friendlyEntities = livingEntities.stream()
                    .filter(entity -> !ENTITY_SCOPES.contains("players") && entity.isPlayer() || entity == player || isTamedByPlayer(entity, player))
                    .toList();

        }

        applyEffectsToEntities(hostileEntities, badEffects, effectDuration);
        applyEffectsToEntities(friendlyEntities, goodEffects, effectDuration);
    }

    private static boolean matchesScope(LivingEntity entity, String scope) {
        return switch (scope) {
            case "players" -> entity instanceof PlayerEntity;
            case "monsters" -> entity instanceof Monster;
            case "animals" -> entity instanceof AnimalEntity &&
                    !(entity instanceof TameableEntity && ((TameableEntity) entity).isTamed());
            default -> false;
        };
    }

    private static boolean isTamedByPlayer(LivingEntity entity, PlayerEntity player) {
        if (entity instanceof TameableEntity tameable) {
            return tameable.isTamed() && tameable.getOwner() == player;
        }

        return false;
    }
}
