package com.afox.elementalweapons.items;

import com.afox.elementalweapons.ability.active.MagicSwordActiveAbility;
import com.afox.elementalweapons.ability.passive.MagicSwordPassiveAbility;
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
import net.minecraft.world.World;

public class MagicSwordItem extends SwordItem {
    static ModConfig.ConfigData.MagicSword config = ModConfig.getConfig().magicSword;
    CooldownUtils ABILITY_PASSIVE_COOLDOWN = new CooldownUtils(20 * config.passiveCooldown);
    private static final int ABILITY_ACTIVE_COOLDOWN = 20 * config.activeCooldown;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public MagicSwordItem() {
        super(ToolMaterials.DIAMOND, 3, -2.0f, new Item.Settings());
    }

    /**
     * ЛКМ (пассив):
     * Наносит 5 ед. магического урона (игнорирует броню и любые защитные эффекты)
     * Перезарядка: 40 секунд.
     * Износ: 1hp
     * ---
     * ПКМ (актив):
     * При активации, вызывает магический круг и накладывает эффекты.
     * Перезарядка: 120 секунд.
     * Затрата опыта: 18xp
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        if (ABILITY_PASSIVE_COOLDOWN.checkCooldown(attacker.getWorld())) {
            MagicSwordPassiveAbility.execute(target, attacker);
            ABILITY_PASSIVE_COOLDOWN.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, e -> e.sendToolBreakStatus(attacker.getActiveHand()));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (ExperienceUtils.take(world, player, XP_COST)) {
            MagicSwordActiveAbility.execute(world, player);
            player.getItemCooldownManager().set(this, ABILITY_ACTIVE_COOLDOWN);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
