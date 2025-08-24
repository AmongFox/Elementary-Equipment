package com.afox.elementalweapons.items;

import com.afox.elementalweapons.ability.active.HurricaneSwordActiveAbility;
import com.afox.elementalweapons.ability.passive.HurricaneSwordPassiveAbility;
import com.afox.elementalweapons.config.ModConfig;
import com.afox.elementalweapons.utils.CooldownUtils;
import com.afox.elementalweapons.utils.ExperienceUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HurricaneSwordItem extends SwordItem {
    static ModConfig.ConfigData.HurricaneSword config = ModConfig.getConfig().hurricaneSword;
    CooldownUtils ABILITY_PASSIVE_COOLDOWN = new CooldownUtils(20 * config.passiveCooldown);
    private static final int ABILITY_ACTIVE_COOLDOWN = 20 * config.activeCooldown;
    private static final float ABILITY_PASSIVE_CHANCE = config.passiveChance;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public HurricaneSwordItem() {
        super(ToolMaterials.DIAMOND, 2, -1.4F, new Item.Settings());
    }

    /**
     * ЛКМ (пассив):
     * С шансом 70% отталкивает противника и оглушает его.
     * Перезарядка: 10 секунд.
     * Износ: 1ph
     * ---
     * ПКМ (актив):
     * Отталкивает всех мобов вокруг себя в радиусе 20 блоков.
     * Перезарядка: 25 секунд.
     * Затрата опыта: 9xp
     */

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        Random random = Random.create();

        if (ABILITY_PASSIVE_COOLDOWN.checkCooldown(attacker.getWorld()) && random.nextFloat() < ABILITY_PASSIVE_CHANCE) {
            HurricaneSwordPassiveAbility.execute(target, attacker);
            ABILITY_PASSIVE_COOLDOWN.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, e -> e.sendToolBreakStatus(attacker.getActiveHand()));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (ExperienceUtils.take(world, player, XP_COST)) {
            HurricaneSwordActiveAbility.execute(world, player);
            player.getItemCooldownManager().set(this, ABILITY_ACTIVE_COOLDOWN);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
