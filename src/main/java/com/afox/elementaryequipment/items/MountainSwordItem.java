package com.afox.elementaryequipment.items;

import com.afox.elementaryequipment.ability.active.MountainSwordActiveAbility;
import com.afox.elementaryequipment.ability.passive.MountainSwordPassiveAbility;
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
import net.minecraft.world.World;

public class MountainSwordItem extends SwordItem {
    static ModConfig.ConfigData.MountainSword config = ModConfig.getConfig().mountainSword;
    CooldownUtils ABILITY_PASSIVE_COOLDOWN = new CooldownUtils(20 * config.passiveCooldown);
    private static final int ABILITY_ACTIVE_COOLDOWN = 20 * config.activeCooldown;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public MountainSwordItem() {
        super(ToolMaterials.DIAMOND, 6, -3.2f, new Settings());
    }

    /**
     * ЛКМ (пассив):
     * Накладывает эффект сопротивления и силы.
     * Перезарядка: 35 секунд.
     * Износ: 1hp
     * ---
     * ПКМ (актив):
     * Подбрасывает мобов вокруг вверх и создаёт под ними капельники.
     * Перезарядка: 60 секунд.
     * Затрата опыта: 14xp
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        if (ABILITY_PASSIVE_COOLDOWN.checkCooldown(attacker.getWorld())) {
            MountainSwordPassiveAbility.execute(target, attacker);
            ABILITY_PASSIVE_COOLDOWN.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, e -> e.sendToolBreakStatus(attacker.getActiveHand()));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (ExperienceUtils.take(world, player, XP_COST)) {
            MountainSwordActiveAbility.execute(world, player);
            player.getItemCooldownManager().set(this, ABILITY_ACTIVE_COOLDOWN);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
