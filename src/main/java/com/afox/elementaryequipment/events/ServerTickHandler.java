package com.afox.elementaryequipment.events;

import com.afox.elementaryequipment.ability.active.MagicSwordActiveAbility;
import com.afox.elementaryequipment.ability.active.ElectricSwordActiveAbility;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class ServerTickHandler implements ServerTickEvents.StartTick {

    @Override
    public void onStartTick(MinecraftServer server) {
        MagicSwordActiveAbility.tickAll();
        ElectricSwordActiveAbility.tickAll();
    }
}
