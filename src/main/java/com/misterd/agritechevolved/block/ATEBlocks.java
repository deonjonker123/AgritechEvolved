package com.misterd.agritechevolved.block;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.component.ATEDataComponents;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.block.custom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ATEBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AgritechEvolved.MODID);

    public static final DeferredBlock<Block> ACACIA_PLANTER = registerBlock("acacia_planter",
            () -> new AcaciaPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BAMBOO_PLANTER = registerBlock("bamboo_planter",
            () -> new BambooPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BIRCH_PLANTER = registerBlock("birch_planter",
            () -> new BirchPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> CHERRY_PLANTER = registerBlock("cherry_planter",
            () ->  new CherryPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD).noOcclusion()));

    public static final DeferredBlock<Block> CRIMSON_PLANTER = registerBlock("crimson_planter",
            () -> new CrimsonPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> DARK_OAK_PLANTER = registerBlock("dark_oak_planter",
            () -> new DarkOakPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> JUNGLE_PLANTER = registerBlock("jungle_planter",
            () -> new JunglePlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> MANGROVE_PLANTER = registerBlock("mangrove_planter",
            () -> new MangrovePlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> OAK_PLANTER = registerBlock("oak_planter",
            () -> new OakPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> SPRUCE_PLANTER = registerBlock("spruce_planter",
            () -> new SprucePlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> WARPED_PLANTER = registerBlock("warped_planter",
            () -> new WarpedPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final DeferredBlock<Block> ADVANCED_PLANTER = registerBlock("advanced_planter",
            () -> new AdvancedPlanterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> BIOMASS_BURNER = registerBlock("biomass_burner",
            () -> new BiomassBurnerBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> COMPOSTER = registerBlock("composter",
            () -> new ComposterBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> CAPACITOR_TIER_1 = registerBlock("capacitor_tier1",
            () -> new CapacitorTier1Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);
                    Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
                    if (storedEnergy != null && storedEnergy > 0) {
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy", storedEnergy).withStyle(ChatFormatting.GOLD));
                    }

                }
            });

    public static final DeferredBlock<Block> CAPACITOR_TIER_2 = registerBlock("capacitor_tier2",
            () -> new CapacitorTier2Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);
                    Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
                    if (storedEnergy != null && storedEnergy > 0) {
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy", storedEnergy).withStyle(ChatFormatting.GOLD));
                    }

                }
            });

    public static final DeferredBlock<Block> CAPACITOR_TIER_3 = registerBlock("capacitor_tier3",
            () -> new CapacitorTier3Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            {
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, context, tooltip, flag);
                    Integer storedEnergy = stack.get(ATEDataComponents.STORED_ENERGY.get());
                    if (storedEnergy != null && storedEnergy > 0) {
                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                        tooltip.add(Component.translatable("tooltip.agritechevolved.capacitor.stored_energy", storedEnergy).withStyle(ChatFormatting.GOLD));
                    }

                }
            });

    public static final DeferredBlock<Block> COMPACTED_BIOMASS_BLOCK = registerBlock("compacted_biomass_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> INFUSED_FARMLAND = registerBlock("infused_farmland",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.MOSS)
                    .noOcclusion()
                    .randomTicks())
            {
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.infused_farmland"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredBlock<Block> MULCH = registerBlock("mulch",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.MOSS)
                    .noOcclusion())
            {
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.agritechevolved.mulch"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ATEItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
