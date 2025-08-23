package com.afox.elementalweapons;

import com.afox.elementalweapons.config.ModConfig;
import com.afox.elementalweapons.events.DelayedTaskScheduler;
import com.afox.elementalweapons.events.ServerTickHandler;
import com.afox.elementalweapons.initializers.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModElementalWeapons implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ElementalWeapons");

	@Override
	public void onInitialize() {
		LOGGER.info("LOAD CONFIG...");
		// Конфиг
		ModConfig.loadConfig();
		LOGGER.info("DONE");

		LOGGER.info("SWORDS REGISTERING...");
		// Мечи
		Items.registerItems();
		Items.registerInRegister();
		Items.addToItemGroup();
		LOGGER.info("DONE");

		LOGGER.info("SERVER TICK REGISTERING...");
		// Серверные тики
		ServerTickEvents.START_SERVER_TICK.register(new ServerTickHandler());
		DelayedTaskScheduler.init();
		LOGGER.info("DONE");
	}
}