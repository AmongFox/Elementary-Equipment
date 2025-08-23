package com.afox.elementalweapons.config;

import static com.afox.elementalweapons.ModElementalWeapons.LOGGER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@SuppressWarnings("unused")
public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("elemental-weapons.json");
    private static ConfigData config;
    private static final int MIN_COOLDOWN = 0;
    private static final int MAX_COOLDOWN = 1000;
    private static final float MIN_CHANCE = 0.1F;
    private static final float MAX_CHANCE = 1.0F;
    private static final int MIN_XP = 0;
    private static final int MAX_XP = 1000;
    private static final int MIN_DURABILITY = 0;
    private static final int MAX_DURABILITY = 1562;

    public static void loadConfig() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String content = Files.readString(CONFIG_PATH);
                config = GSON.fromJson(content, ConfigData.class);

                validateAndCorrectConfig(config);
                saveConfig();

                LOGGER.info("Config loaded successfully");
            } else {
                config = new ConfigData();
                saveConfig();
                LOGGER.info("Created default config");
            }
        } catch (IOException error) {
            LOGGER.error("Failed to load config", error);
            config = new ConfigData();
        }
    }

    public static void saveConfig() throws IOException {
        String json = GSON.toJson(config);

        Path parentDir = CONFIG_PATH.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        Files.writeString(CONFIG_PATH, json);
    }

    public static ConfigData getConfig() {
        return config;
    }

    public static class ConfigData {
        public static class BaseSword {
            public int activeCooldown;
            public int passiveCooldown;
            public float passiveChance;
            public int xpCost;
            public int durabilityCost;

            public BaseSword(int activeCooldown, int passiveCooldown, float passiveChance, int xpCost, int durabilityCost) {
                this.activeCooldown = activeCooldown;
                this.passiveCooldown = passiveCooldown;
                this.passiveChance = passiveChance;
                this.xpCost = xpCost;
                this.durabilityCost = durabilityCost;
            }
        }

        public AncientSword ancientSword = new AncientSword();
        public ElectricSword electricSword = new ElectricSword();
        public FireSword fireSword = new FireSword();
        public FrozenSword frozenSword = new FrozenSword();
        public HurricaneSword hurricaneSword = new HurricaneSword();
        public MagicSword magicSword = new MagicSword();
        public MountainSword mountainSword = new MountainSword();
        public SeaSword seaSword = new SeaSword();
        public General general = new General();

        public static class AncientSword extends BaseSword {
            public AncientSword() {
                super(20, 40, 0.0F, 4, 1);
            }
        }
        public static class ElectricSword extends BaseSword {
            public ElectricSword() {
                super(90, 45, 0.2F, 22, 3);
            }
        }
        public static class FireSword extends BaseSword {
            public FireSword() {
                super(45, 30, 0.6F, 16, 2);
            }
        }
        public static class FrozenSword extends BaseSword {
            public FrozenSword() {
                super(45, 30, 0.3F, 16, 2);
            }
        }
        public static class HurricaneSword extends BaseSword {
            public HurricaneSword() {
                super(45, 10, 0.7F, 14, 1);
            }
        }
        public static class MagicSword extends BaseSword {
            public MagicSword() {
                super(120, 40, 0.0F, 14, 1);
            }
        }
        public static class MountainSword extends BaseSword {
            public MountainSword() {
                super(120, 40, 0.0F, 14, 1);
            }
        }
        public static class SeaSword extends BaseSword {
            public SeaSword() {
                super(35, 20, 0.0F, 5, 1);
            }
        }
        public static class General {
            private final static String README = "Select entities affected by ability: (all, players, animals, monsters)";
            public Set<String> entityScopes = Set.of("all");
        }

        public ConfigData() {}
    }

    private static void validateAndCorrectConfig(ConfigData config) {
        validateConfig(config.ancientSword);
        validateConfig(config.electricSword);
        validateConfig(config.fireSword);
        validateConfig(config.frozenSword);
        validateConfig(config.hurricaneSword);
        validateConfig(config.magicSword);
        validateConfig(config.mountainSword);
        validateConfig(config.seaSword);
    }

    private static void validateConfig(ConfigData.BaseSword sword) {
        sword.activeCooldown = Math.min(Math.max(sword.activeCooldown, MIN_COOLDOWN), MAX_COOLDOWN);
        sword.passiveCooldown = Math.min(Math.max(sword.passiveCooldown, MIN_COOLDOWN), MAX_COOLDOWN);
        sword.passiveChance = Math.min(Math.max(sword.passiveChance, MIN_CHANCE), MAX_CHANCE);
        sword.xpCost = Math.min(Math.max(sword.xpCost, MIN_XP), MAX_XP);
        sword.durabilityCost = Math.min(Math.max(sword.durabilityCost, MIN_DURABILITY), MAX_DURABILITY);
    }
}