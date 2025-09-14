package com.afox.elementaryequipment.items;

import com.afox.elementaryequipment.ability.active.FrozenSwordActiveAbility;
import com.afox.elementaryequipment.ability.passive.FrozenSwordPassiveAbility;
import com.afox.elementaryequipment.config.ModConfig;
import com.afox.elementaryequipment.utils.CooldownUtils;
import com.afox.elementaryequipment.utils.ExperienceUtils;
import com.afox.elementaryequipment.utils.WorldBiomeUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FrozenSwordItem extends SwordItem {
    static ModConfig.ConfigData.FrozenSword config = ModConfig.getConfig().frozenSword;
    CooldownUtils ABILITY_PASSIVE_COOLDOWN = new CooldownUtils(20 * config.passiveCooldown);
    private static final int ABILITY_ACTIVE_COOLDOWN = 20 * config.activeCooldown;
    private float ABILITY_PASSIVE_CHANCE = config.passiveChance;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public FrozenSwordItem() {
        super(ToolMaterials.DIAMOND, 4, -2.6F, new Settings());
    }

    /**
     * ЛКМ (пассив):
     * С вероятностью в 30% замораживает врага.
     * В зимнем биоме шанс повышается на 30%
     * Перезарядка: 25 секунд.
     * Износ: 2hp
     * ---
     * ПКМ (актив):
     * Замораживает всё в области 10 блоков.
     * Перезарядка: 45 секунд.
     * Затрата опыта: 16xp
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        Random random = Random.create();

        if (WorldBiomeUtils.isInSnowyBiome(attacker)) {
            this.ABILITY_PASSIVE_CHANCE += 0.3F;
        }

        if (ABILITY_PASSIVE_COOLDOWN.checkCooldown(attacker.getWorld()) && random.nextFloat() < this.ABILITY_PASSIVE_CHANCE) {
            FrozenSwordPassiveAbility.execute(target, attacker);
            ABILITY_PASSIVE_COOLDOWN.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, e -> e.sendToolBreakStatus(attacker.getActiveHand()));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (ExperienceUtils.take(world, player, XP_COST)) {
            FrozenSwordActiveAbility.execute(world, player);
            player.getItemCooldownManager().set(this, ABILITY_ACTIVE_COOLDOWN);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}