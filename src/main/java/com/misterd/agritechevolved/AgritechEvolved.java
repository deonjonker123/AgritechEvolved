package com.misterd.agritechevolved;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.client.ber.PlanterBlockEntityRenderer;
import com.misterd.agritechevolved.datamap.ATEDataMaps;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.gui.custom.*;
import com.misterd.agritechevolved.item.ATECreativeTab;
import com.misterd.agritechevolved.item.ATEItems;
import com.misterd.agritechevolved.network.ATENetwork;
import com.misterd.agritechevolved.recipe.ATERecipe;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AgritechEvolved.MODID)
public class AgritechEvolved {
    public static final String MODID = "agritechevolved";
    public static int RECIPE_REVISION = 0;

    public AgritechEvolved(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ATEBlocks.register(modEventBus);
        ATEBlockEntities.register(modEventBus);
        ATEItems.register(modEventBus);
        ATECreativeTab.register(modEventBus);
        ATEMenuTypes.register(modEventBus);
        ATERecipe.register(modEventBus);
        ATEDataMaps.register();

        Config.register(context);
        modEventBus.register(Config.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ATENetwork::register);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void object, ResourceManager manager, ProfilerFiller profiler) {
                RECIPE_REVISION++;
            }
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ATEMenuTypes.PLANTER_BLOCK_MENU.get(), BasicPlanterScreen::new);
                MenuScreens.register(ATEMenuTypes.ADVANCED_PLANTER_BLOCK_MENU.get(), AdvancedPlanterScreen::new);
                MenuScreens.register(ATEMenuTypes.COMPOSTER_MENU.get(), ComposterScreen::new);
                MenuScreens.register(ATEMenuTypes.BURNER_MENU.get(), BiomassBurnerScreen::new);
                MenuScreens.register(ATEMenuTypes.CAPACITOR_MENU.get(), CapacitorScreen::new);
            });
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ATEBlockEntities.PLANTER_BLOCK_BE.get(), PlanterBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ATEBlockEntities.ADVANCED_PLANTER_BLOCK_BE.get(), PlanterBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
            event.register(PlanterBlockEntityRenderer.CLOCHE_DOME_MODEL);
        }
    }
}