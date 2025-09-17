package com.afox.elementaryequipment.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ExperienceUtils {
    public static boolean take(World world, PlayerEntity player, int xpCost) {
        if (player.isCreative()) {
            return true;
        }

        int totalXP = getTotalPlayerXP(player);

        if (totalXP >= xpCost) {
            player.addExperience(-xpCost);
            return true;
        }

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_ANVIL_LAND,
                    SoundCategory.PLAYERS,
                    0.5F,
                    1.0F
            );
        }

        player.sendMessage(Text.literal("§4Недостаточно опыта! Нужно: " + (xpCost - totalXP) + " очков"), true);
        return false;
    }

    public static int getTotalPlayerXP(PlayerEntity player) {
        int playerLevel = player.experienceLevel;
        int totalXP = getXPForLevel(playerLevel);
        totalXP += Math.round(player.experienceProgress * getXpToNextLevel(playerLevel));
        return totalXP;
    }

    private static int getXPForLevel(int level) {
        if (level <= 0) return 0;
        if (level <= 16) {
            return (level * level) + (6 * level);
        } else if (level <= 31) {
            return (int) ((2.5 * level * level) - (40.5 * level) + 360);
        } else {
            return (int) ((4.5 * level * level) - (162.5 * level) + 2220);
        }
    }

    private static int getXpToNextLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }
}
