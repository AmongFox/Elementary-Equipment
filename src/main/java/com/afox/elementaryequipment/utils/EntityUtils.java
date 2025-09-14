package com.afox.elementaryequipment.utils;

import com.afox.elementaryequipment.config.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityUtils {
    private static final Set<String> ENTITY_SCOPES = ModConfig.getConfig().general.entityScopes;

    public static List<LivingEntity> getEntitiesInArea(World world, Vec3d center, double radius) {
        return world.getEntitiesByClass(
                LivingEntity.class,
                getBox(center, radius),
                entity -> entity.isAlive() && entity.squaredDistanceTo(center) <= radius * radius
        );
    }

    public static class FilterEntity {
//        public static List<LivingEntity> getPlayerEntities(List<LivingEntity> livingEntities) {
//            return livingEntities.stream()
//                .filter(entity -> entity instanceof PlayerEntity)
//                .collect(Collectors.toList());
//        }
//
//        public static List<LivingEntity> getTamedEntities(List<LivingEntity> livingEntities) {
//            return livingEntities.stream()
//                    .filter(entity -> entity instanceof TameableEntity tameableEntity && tameableEntity.isTamed())
//                    .collect(Collectors.toList());
//        }

        public static List<LivingEntity> getEntitiesInScopes(List<LivingEntity> livingEntities, PlayerEntity player) {
            if (ENTITY_SCOPES.contains("all")) {
                return livingEntities
                    .stream()
                    .filter(entity -> entity != player && !(entity instanceof TameableEntity tameable && tameable.isTamed()))
                    .collect(Collectors.toList());
            }

            return livingEntities.stream()
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
                }).collect(Collectors.toList());
        }
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

    private static Box getBox(Vec3d vec3d, double radius) {
        return new Box(
                vec3d.getX() - radius, vec3d.getY() - radius, vec3d.getZ() - radius,
                vec3d.getX() + radius, vec3d.getY() + radius, vec3d.getZ() + radius
        );
    }
}
