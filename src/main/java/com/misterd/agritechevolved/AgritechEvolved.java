package com.misterd.agritechevolved;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.blockentity.custom.AdvancedPlanterBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.BiomassBurnerBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.CapacitorBlockEntity;
import com.misterd.agritechevolved.blockentity.custom.ComposterBlockEntity;
import com.misterd.agritechevolved.client.ber.PlanterBlockEntityRenderer;
import com.misterd.agritechevolved.command.ATECommands;
import com.misterd.agritechevolved.component.ATEDataComponents;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.gui.custom.*;
import com.misterd.agritechevolved.item.ATECreativeTab;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.recipe.ATERecipe;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(AgritechEvolved.MODID)
public class AgritechEvolved {
    public static final String MODID = "agritechevolved";

    public AgritechEvolved(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ATEBlocks.register(modEventBus);
        ATEBlockEntities.register(modEventBus);
        ATEItems.register(modEventBus);
        ATECreativeTab.register(modEventBus);
        ATEMenuTypes.register(modEventBus);
        ATEDataComponents.register(modEventBus);

        modEventBus.addListener(AdvancedPlanterBlockEntity::registerCapabilities);
        modEventBus.addListener(ComposterBlockEntity::registerCapabilities);
        modEventBus.addListener(BiomassBurnerBlockEntity::registerCapabilities);
        modEventBus.addListener(CapacitorBlockEntity::registerCapabilities);

        ATERecipe.RECIPE_SERIALIZERS.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        Config.register(modContainer);
        modEventBus.register(Config.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    public void onRegisterCommands(RegisterCommandsEvent event) {
        ATECommands.register(event.getDispatcher());
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {}

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ATEBlockEntities.PLANTER_BLOCK_BE.get(), PlanterBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(), PlanterBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ATEMenuTypes.PLANTER_BLOCK_MENU.get(), BasicPlanterScreen::new);
            event.register(ATEMenuTypes.ADVANCED_PLANTER_BLOCK_MENU.get(), AdvancedPlanterScreen::new);
            event.register(ATEMenuTypes.COMPOSTER_MENU.get(), ComposterScreen::new);
            event.register(ATEMenuTypes.BURNER_MENU.get(), BiomassBurnerScreen::new);
            event.register(ATEMenuTypes.CAPACITOR_MENU.get(), CapacitorScreen::new);
        }

        @SubscribeEvent
        public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
            event.register(new ModelResourceLocation(
                    ResourceLocation.fromNamespaceAndPath(MODID, "block/cloche_dome"),
                    "standalone"
            ));
        }
    }
}
