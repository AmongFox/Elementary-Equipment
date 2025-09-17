package com.afox.elementaryequipment.items;

import com.afox.elementaryequipment.ability.active.FireSwordActiveAbility;
import com.afox.elementaryequipment.ability.passive.FireSwordPassiveAbility;
import com.afox.elementaryequipment.config.ModConfig;
import com.afox.elementaryequipment.utils.CooldownUtils;
import com.afox.elementaryequipment.utils.ExperienceUtils;
import com.afox.elementaryequipment.utils.WorldBiomeUtils;
import net.minecraft.entity.EquipmentSlot;
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

public class FireSwordItem extends SwordItem {
    static ModConfig.ConfigData.FireSword config = ModConfig.getConfig().fireSword;
    CooldownUtils ABILITY_PASSIVE_COOLDOWN = new CooldownUtils(20 * config.passiveCooldown);
    private static final int ABILITY_ACTIVE_COOLDOWN = 20 * config.activeCooldown;
    private float ABILITY_PASSIVE_CHANCE = config.passiveChance;
    private static final int XP_COST = config.xpCost;
    private static final int DURABILITY_COST = config.durabilityCost;

    public FireSwordItem() {
        super(ToolMaterials.DIAMOND, new Item.Settings()
                .maxDamage(ToolMaterials.DIAMOND.getDurability())
                .attributeModifiers(SwordItem.createAttributeModifiers(
                        ToolMaterials.DIAMOND,
                        4,
                        -2.6F
                ))
        );
    }

    /**
     * ЛКМ (пассив):
     * С вероятностью в 60% поджигает врага.
     * В зимних биомах шанс понижается на 30%
     * Перезарядка: 30 секунд.
     * Износ: 2hp
     * ---
     * ПКМ (актив):
     * Запускает огненный шар, который создаёт огненную воронку при взрыве.
     * Перезарядка: 45 секунд.
     * Затрата опыта: 16xp
     */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld().isClient()) return super.postHit(stack, target, attacker);

        Random random = Random.create();

        if (WorldBiomeUtils.isInSnowyBiome(attacker)) {
            this.ABILITY_PASSIVE_CHANCE -= 0.3F;
        }

        if (ABILITY_PASSIVE_COOLDOWN.checkCooldown(attacker.getWorld()) && random.nextFloat() < this.ABILITY_PASSIVE_CHANCE) {
            FireSwordPassiveAbility.execute(target, attacker);
            ABILITY_PASSIVE_COOLDOWN.updateCooldown(attacker.getWorld());
            stack.damage(DURABILITY_COST, attacker, EquipmentSlot.MAINHAND);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

        if (ExperienceUtils.take(world, player, XP_COST)) {
            FireSwordActiveAbility.execute(world, player);
            player.getItemCooldownManager().set(this, ABILITY_ACTIVE_COOLDOWN);
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
