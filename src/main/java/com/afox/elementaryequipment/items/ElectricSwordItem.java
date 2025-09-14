package com.afox.elementaryequipment.items;

import com.afox.elementaryequipment.ability.active.ElectricSwordActiveAbility;
import com.afox.elementaryequipment.ability.passive.ElectricSwordPassiveAbility;
import com.afox.elementaryequipment.config.ModConfig;
import com.afox.elementaryequipment.utils.CooldownUtils;
import com.afox.elementaryequipment.utils.ExperienceUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ElectricSwordItem extends SwordItem {
    static ModConfig.ConfigData.ElectricSword config = ModConfig.getConfig().electricSword;
    CooldownUtils ABILITY_PASSIVE_COOLDOWN = new CooldownUtils(20 * config.passiveCooldown);
    private static final int ABILITY_ACTIVE_COOLDOWN = 20 * config.activeCooldown;
    private float ABILITY_PASSIVE_CHANCE = config.passiveChance;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public ElectricSwordItem() {
        super(ToolMaterials.DIAMOND, 5, -3.2F, new Settings());
    }

    /**
     * ЛКМ (пассив):
     * С шансом 20% вызывает молнию на врага.
     * Если в мире гроза, то шанс повышается на 40%
     * Перезарядка: 45 секунд.
     * Износ: 3hp
     * ---
     * ПКМ (актив):
     * Вызывает шторм в мире, который вызывает на 3 ближайших врагов молнию.
     * На врагов в радиусе 20 блоков наносится мощный удар молнией, нанося негативные эффекты.
     * Перезарядка: 90 секунд.
     * Затрата опыта: 22xp
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        Random random = Random.create();

        if (attacker.getWorld().isThundering()) {
            this.ABILITY_PASSIVE_CHANCE += 0.4F;
        }

        if (ABILITY_PASSIVE_COOLDOWN.checkCooldown(attacker.getWorld()) &&
                attacker.getWorld().getRegistryKey().equals(World.OVERWORLD) &&
                random.nextFloat() < this.ABILITY_PASSIVE_CHANCE) {
            ElectricSwordPassiveAbility.execute(target, attacker);
            ABILITY_PASSIVE_COOLDOWN.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, e -> e.sendToolBreakStatus(attacker.getActiveHand()));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (ExperienceUtils.take(world, player, XP_COST)) {
            ElectricSwordActiveAbility.execute(world, player);
            player.getItemCooldownManager().set(this, ABILITY_ACTIVE_COOLDOWN);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
