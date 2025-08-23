package com.afox.elementalweapons.utils;

import net.minecraft.world.World;

public class CooldownUtils {
    private final long cooldownTime;
    private long lastUsedTime;

    public CooldownUtils(int cooldownTime) {
        this.cooldownTime = cooldownTime;
        this.lastUsedTime = -cooldownTime;
    }

    public boolean checkCooldown(World world) {
        long currentTime = world.getTime();

        return currentTime >= lastUsedTime + cooldownTime;
    }

    public void updateCooldown(World world) {
        this.lastUsedTime = world.getTime();
    }
}
