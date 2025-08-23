package com.afox.elementalweapons.utils;

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

        if (player.totalExperience >= xpCost) {
            player.addExperience(-xpCost);
        } else {
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

            player.sendMessage(Text.literal("§4Недостаточно опыта!"), true);
            return false;
        }
        return true;
    }
}
