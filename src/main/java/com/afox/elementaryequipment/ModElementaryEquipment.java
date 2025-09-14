package com.afox.elementaryequipment;

import com.afox.elementaryequipment.config.ModConfig;
import com.afox.elementaryequipment.events.DelayedTaskScheduler;
import com.afox.elementaryequipment.events.ServerTickHandler;
import com.afox.elementaryequipment.initializers.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModElementaryEquipment implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ElementaryEquipment");

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