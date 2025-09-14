package com.afox.elementaryequipment.items;

import com.afox.elementaryequipment.ability.active.AncientSwordActiveAbility;
import com.afox.elementaryequipment.ability.passive.AncientSwordPassiveAbility;
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
import net.minecraft.world.World;

public class AncientSwordItem extends SwordItem {
    static ModConfig.ConfigData.AncientSword config = ModConfig.getConfig().ancientSword;
    CooldownUtils AbilityPassiveCooldown = new CooldownUtils(20 * config.passiveCooldown);
    private static final int AbilityActiveCooldown = 20 * config.activeCooldown;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public AncientSwordItem() {
        super(ToolMaterials.DIAMOND, 3, -2.2F, new Settings());
    }

    /**
     * ЛКМ (пассив):
     * В биомах джунглей наносится на 30% больше урона.
     * Восстанавливается здоровье на 1.5хп при ударе
     * Перезарядка: 40 секунд
     * Износ: 1hp
     * ---
     * ПКМ (актив):
     * Создаёт вокруг игрока цветение, накладывает негативные / положительные эффекты
     * Перезарядка: 20 секунд
     * Затрата опыта: 4xp
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        if (AbilityPassiveCooldown.checkCooldown(attacker.getWorld()) && WorldBiomeUtils.isInJungleBiome(attacker)) {
            AncientSwordPassiveAbility.execute(target, attacker);
            AbilityPassiveCooldown.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, e -> e.sendToolBreakStatus(attacker.getActiveHand()));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (player.isOnGround()) {
            if (ExperienceUtils.take(world, player, XP_COST)) {
                AncientSwordActiveAbility.execute(world, player);
                player.getItemCooldownManager().set(this, AbilityActiveCooldown);
            }
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
