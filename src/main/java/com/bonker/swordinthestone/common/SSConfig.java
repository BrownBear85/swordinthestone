package com.bonker.swordinthestone.common;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSConfig {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    // sword stats
    public static final ForgeConfigSpec.IntValue BASE_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue MAX_DAMAGE_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue BASE_SPEED;
    public static final ForgeConfigSpec.DoubleValue MAX_SPEED_MODIFIER;
    public static final ForgeConfigSpec.IntValue DURABILITY;

    // sword stone
    public static final ForgeConfigSpec.BooleanValue SWORD_BEACON_ENABLED;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLED_ABILITIES;
    public static final List<ResourceLocation> disabledAbilities = new ArrayList<>();
    public static final ForgeConfigSpec.IntValue SWORD_STONE_SPACING_OVERWORLD;
    public static final ForgeConfigSpec.IntValue SWORD_STONE_SEPARATION_OVERWORLD;
    public static final ForgeConfigSpec.IntValue SWORD_STONE_SPACING_END;
    public static final ForgeConfigSpec.IntValue SWORD_STONE_SEPARATION_END;
    public static final ForgeConfigSpec.IntValue SWORD_STONE_SPACING_NETHER;
    public static final ForgeConfigSpec.IntValue SWORD_STONE_SEPARATION_NETHER;

    // abilities
    public static final ForgeConfigSpec.IntValue THUNDER_SMITE_CHARGES;
    public static final ForgeConfigSpec.DoubleValue VAMPIRIC_HEALTH_PERCENT;
    public static final ForgeConfigSpec.IntValue VAMPIRIC_HEALTH_CAP;
    public static final ForgeConfigSpec.IntValue TOXIC_DASH_COOLDOWN;
    public static final ForgeConfigSpec.IntValue ENDER_RIFT_COOLDOWN;
    public static final ForgeConfigSpec.IntValue ENDER_RIFT_DURATION;
    public static final ForgeConfigSpec.IntValue FIREBALL_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue FIREBALL_DESTROY_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue FIREBALL_SET_FIRE;
    public static final ForgeConfigSpec.DoubleValue FIREBALL_MAX_POWER;
    public static final ForgeConfigSpec.DoubleValue FIREBALL_CHARGE_RATE;
    public static final ForgeConfigSpec.BooleanValue DOUBLE_JUMP_VEHICLE;
    public static final ForgeConfigSpec.DoubleValue ALCHEMIST_SELF_CHANCE;
    public static final ForgeConfigSpec.DoubleValue ALCHEMIST_VICTIM_CHANCE;
    public static final ForgeConfigSpec.IntValue BAT_SWARM_COOLDOWN;
    public static final ForgeConfigSpec.IntValue BAT_SWARM_DURATION;
    public static final ForgeConfigSpec.DoubleValue BAT_SWARM_DAMAGE;
    public static final ForgeConfigSpec.IntValue VORTEX_CHARGE_CAPACITY;
    public static final ForgeConfigSpec.IntValue VORTEX_CHARGE_PER_HIT;
    public static final ForgeConfigSpec.DoubleValue VORTEX_CHARGE_DAMAGE;

    static {
        // sword stats
        COMMON_BUILDER.comment("Sword Stats").push("stats");

        BASE_DAMAGE = COMMON_BUILDER
                .comment("The base damage of a sword from this mod." +
                        "\nThe weakest sword possible will have this for its attack damage.")
                .translation("swordinthestone.configgui.baseDamage")
                .defineInRange("baseDamage", 6, 1, 100);

        MAX_DAMAGE_MODIFIER = COMMON_BUILDER
                .comment("The amount that swords' attack damages can vary randomly." +
                        "\nThe strongest sword possible will have this added to baseDamage for its attack damage.")
                .translation("swordinthestone.configgui.maxDamageModifier")
                .defineInRange("maxDamageModifier", 2.5, 0.0, 50.0);

        BASE_SPEED = COMMON_BUILDER
                .comment("The base attack speed of a sword from this mod." +
                        "\nThe slowest sword possible will have this for its attack damage.")
                .translation("swordinthestone.configgui.baseAttackSpeed")
                .defineInRange("baseAttackSpeed", 1.2, 0.0, 2.0);

        MAX_SPEED_MODIFIER = COMMON_BUILDER
                .comment("The amount that swords' attack speeds can vary randomly." +
                        "\nThe fastest sword possible will have this added to baseAttackSpeed for its attack speed.")
                .translation("swordinthestone.configgui.maxAttackSpeedModifier")
                .defineInRange("maxAttackSpeedModifier", 0.6, 0.0, 1.0);

        DURABILITY = COMMON_BUILDER
                .comment("""
                        The durability of swords from this mod.\
                        A value of 0 will cause swords from this mod to be unbreakable.\
                        Client and server restart required.""")
                .translation("swordinthestone.configgui.durability")
                .defineInRange("durability", 2000, 0, 10000);

        // sword stone
        COMMON_BUILDER.pop().comment("Sword Stone Settings").push("sword_stone");

        SWORD_BEACON_ENABLED = COMMON_BUILDER
                .comment("Whether sword stones should show a beacon beam every so often to alert players to their location.")
                .translation("swordinthestone.configgui.swordBeaconEnabled")
                .define("swordBeaconEnabled", true);

        DISABLED_ABILITIES = COMMON_BUILDER
                .comment("Add the ids of sword abilities here to disabled them. Ex. \"swordinthestone:thunder_smite\"")
                .translation("swordinthestone.configgui.disabledAbilities")
                .defineListAllowEmpty("disabledAbilities", List.of(), obj ->
                        obj instanceof String str &&
                                ResourceLocation.isValidResourceLocation(str) &&
                                SwordAbilities.SWORD_ABILITY_REGISTRY.get().containsKey(new ResourceLocation(str)));

        SWORD_STONE_SPACING_OVERWORLD = COMMON_BUILDER
                .comment("The average distance (in chunks) between sword stones in the Overworld.")
                .translation("swordinthestone.configgui.swordStoneSpacingOverworld")
                .defineInRange("swordStoneSpacingOverworld", 40, 0, 4096);

        SWORD_STONE_SEPARATION_OVERWORLD = COMMON_BUILDER
                .comment("The minimum distance (in chunks) between sword stones in the Overworld." +
                        "\nMust be smaller than swordStoneSpacingOverworld.")
                .translation("swordinthestone.configgui.swordStoneSeparationOverworld")
                .defineInRange("swordStoneSeparationOverworld", 15, 0, 4096);

        SWORD_STONE_SPACING_END = COMMON_BUILDER
                .comment("The average distance (in chunks) between sword stones in the End.")
                .translation("swordinthestone.configgui.swordStoneSpacingEnd")
                .defineInRange("swordStoneSpacingEnd", 40, 0, 4096);

        SWORD_STONE_SEPARATION_END = COMMON_BUILDER
                .comment("The minimum distance (in chunks) between sword stones in the End." +
                        "\nMust be smaller than swordStoneSpacingEnd.")
                .translation("swordinthestone.configgui.swordStoneSeparationEnd")
                .defineInRange("swordStoneSeparationEnd", 15, 0, 4096);

        SWORD_STONE_SPACING_NETHER = COMMON_BUILDER
                .comment("The average distance (in chunks) between sword stones in the Nether.")
                .translation("swordinthestone.configgui.swordStoneSpacingNether")
                .defineInRange("swordStoneSpacingNether", 35, 0, 4096);

        SWORD_STONE_SEPARATION_NETHER = COMMON_BUILDER
                .comment("The minimum distance (in chunks) between sword stones in the Nether." +
                        "\nMust be smaller than swordStoneSpacingNether.")
                .translation("swordinthestone.configgui.swordStoneSeparationNether")
                .defineInRange("swordStoneSeparationNether", 10, 0, 4096);

        // abilities
        COMMON_BUILDER.pop().comment("Sword Ability Settings").push("abilities");

        THUNDER_SMITE_CHARGES = COMMON_BUILDER
                .comment("The number of hits with a Thunder Smite sword before the sword becomes electrically charged.")
                .translation("swordinthestone.configgui.thunderSmiteCharges")
                .defineInRange("thunderSmiteCharges", 3, 1, 10);

        VAMPIRIC_HEALTH_PERCENT = COMMON_BUILDER
                .comment("The percentage of a killed entity's max health that the Vampiric ability will heal its user.")
                .translation("swordinthestone.configgui.vampiricHealthPercent")
                .defineInRange("vampiricHealthPercent", 0.15, 0.01, 2);

        VAMPIRIC_HEALTH_CAP = COMMON_BUILDER
                .comment("The maximum amount of health that the Vampiric ability can heal.")
                .translation("swordinthestone.configgui.vampiricHealthCap")
                .defineInRange("vampiricHealthCap", 10, 1, 1000);

        TOXIC_DASH_COOLDOWN = COMMON_BUILDER
                .comment("The cooldown (in ticks) of the Toxic Dash ability.")
                .translation("swordinthestone.configgui.toxicDashCooldown")
                .defineInRange("toxicDashCooldown", 200, 0, 10000);

        ENDER_RIFT_COOLDOWN = COMMON_BUILDER
                .comment("The cooldown (in ticks) of the Ender Rift ability." +
                        "\nMust be longer than enderRiftDuration.")
                .translation("swordinthestone.configgui.enderRiftCooldown")
                .defineInRange("enderRiftCooldown", 200, 0, 10000);

        ENDER_RIFT_DURATION = COMMON_BUILDER
                .comment("The duration (in ticks) that the Ender Rift entity lasts for after creation." +
                        "\nClient and server restart required.")
                .translation("swordinthestone.configgui.enderRiftDuration")
                .defineInRange("enderRiftDuration", 60, 10, 200);

        FIREBALL_COOLDOWN = COMMON_BUILDER
                .comment("The cooldown (in ticks) of the Fireball ability.")
                .translation("swordinthestone.configgui.fireballCooldown")
                .defineInRange("fireballCooldown", 200, 0, 10000);

        FIREBALL_DESTROY_BLOCKS = COMMON_BUILDER
                .comment("Whether the Fireball ability will destroy blocks with it explodes.")
                .translation("swordinthestone.configgui.fireballDestroyBlocks")
                .define("fireballDestroyBlocks", true);

        FIREBALL_SET_FIRE = COMMON_BUILDER
                .comment("Whether the Fireball ability will set fire when it explodes.")
                .translation("swordinthestone.configgui.fireballSetFire")
                .define("fireballSetFire", true);

        FIREBALL_MAX_POWER = COMMON_BUILDER
                .comment("The maximum power of the Fireball ability." +
                        "\nThis value must be evenly divisible by fireballChargeRate or else the actual max power could be slightly off.")
                .translation("swordinthestone.configgui.fireballMaxPower")
                .defineInRange("fireballMaxPower", 4.0, 1.0, 100.0);

        FIREBALL_CHARGE_RATE = COMMON_BUILDER
                .comment("The increase in power each tick that you use the Fireball ability." +
                        "\nNote that the fireball will grow at twice this rate for the first half of the max power.")
                .translation("swordinthestone.configgui.fireballChargeRate")
                .defineInRange("fireballChargeRate", 0.04, 0.01, 1.0);

        DOUBLE_JUMP_VEHICLE = COMMON_BUILDER
                .comment("Whether you can use the Double Jump ability while riding a vehicle." +
                        "\nThis feature is pretty buggy, but also pretty fun.")
                .translation("swordinthestone.configgui.doubleJumpVehicle")
                .define("doubleJumpVehicle", false);

        ALCHEMIST_SELF_CHANCE = COMMON_BUILDER
                .comment("The chance that you will receive a status effect upon killing a mob with the Alchemist abiliy.")
                .translation("swordinthestone.configgui.alchemistSelfChance")
                .defineInRange("alchemistSelfChance", 0.5, 0.0, 1.0);

        ALCHEMIST_VICTIM_CHANCE = COMMON_BUILDER
                .comment("The chance that you will inflict a status effect on a mob upon hitting it with the Alchemist abiliy.")
                .translation("swordinthestone.configgui.alchemistVictimChance")
                .defineInRange("alchemistVictimChance", 0.5, 0.0, 1.0);

        BAT_SWARM_COOLDOWN = COMMON_BUILDER
                .comment("The cooldown (in ticks) of the Bat Swarm ability.")
                .translation("swordinthestone.configgui.batSwarmCooldown")
                .defineInRange("batSwarmCooldown", 200, 0, 10000);

        BAT_SWARM_DURATION = COMMON_BUILDER
                .comment("The average length of time (in ticks) that the Bat Swarm ability will last.")
                .translation("swordinthestone.configgui.batSwarmDuration")
                .defineInRange("batSwarmDuration", 60, 10, 10000);

        BAT_SWARM_DAMAGE = COMMON_BUILDER
                .comment("The average length of time (in ticks) that the Bat Swarm ability will last.")
                .translation("swordinthestone.configgui.batSwarmDamage")
                .defineInRange("batSwarmDamage", 2.0, 0.0, 100.0);

        VORTEX_CHARGE_CAPACITY = COMMON_BUILDER
                .comment("The maximum amount of charge that a sword can hold." +
                        "\n1 unit of charge is used each tick when holding right click.")
                .translation("swordinthestone.configgui.vortexChargeCapacity")
                .defineInRange("vortexChargeCapacity", 400, 1, 10000);

        VORTEX_CHARGE_PER_HIT = COMMON_BUILDER
                .comment("The amount of charge gained per each fully charged hit on a mob.")
                .translation("swordinthestone.configgui.vortexChargePerHit")
                .defineInRange("vortexChargePerHit", 50, 1, 10000);

        VORTEX_CHARGE_DAMAGE = COMMON_BUILDER
                .comment("The amount of damage dealt to mobs by Vortex Charge's shift-right-click ability.")
                .translation("swordinthestone.configgui.vortexChargeDamage")
                .defineInRange("vortexChargeDamage", 12.0, 0.0, 100.0);

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void load(Path path) {
        CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        COMMON_CONFIG.setConfig(configData);
    }

    public static void updateConfig(final ModConfigEvent event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            disabledAbilities.clear();
            DISABLED_ABILITIES.get().stream().map(ResourceLocation::new).forEach(disabledAbilities::add);
            UniqueSwordItem.reloadAbilities();
        }
    }

    @SubscribeEvent
    public static void onConfigLoaded(final ModConfigEvent.Loading event) {
        updateConfig(event);
    }

    @SubscribeEvent
    public static void onConfigReloaded(final ModConfigEvent.Reloading event) {
        updateConfig(event);
    }
}
