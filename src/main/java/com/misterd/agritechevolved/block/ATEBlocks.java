package com.misterd.agritechevolved.block;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.block.custom.*;
import com.misterd.agritechevolved.component.ATEItemData;
import com.misterd.agritechevolved.item.ATEItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ATEBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AgritechEvolved.MODID);

    public static final RegistryObject<Block> ACACIA_PLANTER = registerBlock("basic_acacia_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> BAMBOO_PLANTER = registerBlock("basic_bamboo_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> BIRCH_PLANTER = registerBlock("basic_birch_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> CHERRY_PLANTER = registerBlock("basic_cherry_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> CRIMSON_PLANTER = registerBlock("basic_crimson_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> DARK_OAK_PLANTER = registerBlock("basic_dark_oak_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> JUNGLE_PLANTER = registerBlock("basic_jungle_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> MANGROVE_PLANTER = registerBlock("basic_mangrove_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> OAK_PLANTER = registerBlock("basic_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> SPRUCE_PLANTER = registerBlock("basic_spruce_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> WARPED_PLANTER = registerBlock("basic_warped_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> TERRACOTTA_PLANTER = registerBlock("terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> BLACK_TERRACOTTA_PLANTER = registerBlock("black_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> BLUE_TERRACOTTA_PLANTER = registerBlock("blue_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> BROWN_TERRACOTTA_PLANTER = registerBlock("brown_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CYAN_TERRACOTTA_PLANTER = registerBlock("cyan_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> GRAY_TERRACOTTA_PLANTER = registerBlock("gray_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> GREEN_TERRACOTTA_PLANTER = registerBlock("green_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> LIGHT_BLUE_TERRACOTTA_PLANTER = registerBlock("light_blue_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> LIGHT_GRAY_TERRACOTTA_PLANTER = registerBlock("light_gray_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> LIME_TERRACOTTA_PLANTER = registerBlock("lime_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> MAGENTA_TERRACOTTA_PLANTER = registerBlock("magenta_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ORANGE_TERRACOTTA_PLANTER = registerBlock("orange_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> PINK_TERRACOTTA_PLANTER = registerBlock("pink_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> PURPLE_TERRACOTTA_PLANTER = registerBlock("purple_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> RED_TERRACOTTA_PLANTER = registerBlock("red_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> WHITE_TERRACOTTA_PLANTER = registerBlock("white_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> YELLOW_TERRACOTTA_PLANTER = registerBlock("yellow_terracotta_planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> ADVANCED_PLANTER = registerBlock("advanced_planter",
            () -> new AdvancedPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> COMPOSTER = registerBlock("composter",
            () -> new ComposterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> BIOMASS_BURNER = registerBlock("biomass_burner",
            () -> new BiomassBurnerBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CAPACITOR_TIER_1 = registerBlock("capacitor_tier1",
            () -> new CapacitorTier1Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, level, tooltip, flag);
                    int storedEnergy = ATEItemData.getStoredEnergy(stack);
                    if (storedEnergy > 0) {
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy", storedEnergy).withStyle(ChatFormatting.GOLD));
                    }
                }
            });

    public static final RegistryObject<Block> CAPACITOR_TIER_2 = registerBlock("capacitor_tier2",
            () -> new CapacitorTier2Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, level, tooltip, flag);
                    int storedEnergy = ATEItemData.getStoredEnergy(stack);
                    if (storedEnergy > 0) {
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy", storedEnergy).withStyle(ChatFormatting.GOLD));
                    }
                }
            });

    public static final RegistryObject<Block> CAPACITOR_TIER_3 = registerBlock("capacitor_tier3",
            () -> new CapacitorTier3Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, level, tooltip, flag);
                    int storedEnergy = ATEItemData.getStoredEnergy(stack);
                    if (storedEnergy > 0) {
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy", storedEnergy).withStyle(ChatFormatting.GOLD));
                    }
                }
            });

    public static final RegistryObject<Block> COMPACTED_BIOMASS_BLOCK = registerBlock("compacted_biomass_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
                    int totalRF = Config.getBurnerCompactedBiomassBlockRfValue();
                    int burnDuration = Config.getBurnerCompactedBiomassBlockBurnDuration();
                    if (Screen.hasShiftDown()) {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.compacted_biomass.rf_generation", fmt.format(totalRF)).withStyle(ChatFormatting.GREEN));
                        double burnSeconds = burnDuration / 20.0D;
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.burn_duration", String.format("%.1f", burnSeconds)).withStyle(ChatFormatting.AQUA));
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.fuel.rf_per_second", fmt.format((int) Math.round(totalRF / burnSeconds))).withStyle(ChatFormatting.YELLOW));
                    } else {
                        tooltipComponents.add(Component.translatable("tooltip.agritechevolved.crude_fuel.shift_info"));
                    }
                }
            });

    public static final RegistryObject<Block> INFUSED_FARMLAND = registerBlock("infused_farmland",
            () -> new InfusedFarmlandBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.MOSS)
                    .noOcclusion()
                    .randomTicks())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.infused_farmland"));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    public static final RegistryObject<Block> MULCH = registerBlock("mulch",
            () -> new MulchBlock(BlockBehaviour.Properties.of()
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.MOSS)
                    .noOcclusion())
            {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.mulch"));
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                }
            });

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ATEItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}