package com.afox.elementalweapons.events;

import com.afox.elementalweapons.ability.active.MagicSwordActiveAbility;
import com.afox.elementalweapons.ability.active.ElectricSwordActiveAbility;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerTickHandler implements ServerTickEvents.StartTick {

    @Override
    public void onStartTick(MinecraftServer server) {
        MagicSwordActiveAbility.tickAll();
        ElectricSwordActiveAbility.tickAll();
    }
}
