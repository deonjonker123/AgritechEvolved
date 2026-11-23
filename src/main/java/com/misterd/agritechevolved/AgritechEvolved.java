package com.misterd.agritechevolved;

import com.misterd.agritechevolved.block.ATEBlocks;
import com.misterd.agritechevolved.blockentity.ATEBlockEntities;
import com.misterd.agritechevolved.gui.ATEMenuTypes;
import com.misterd.agritechevolved.gui.custom.BiomassBurnerScreen;
import com.misterd.agritechevolved.gui.custom.CapacitorScreen;
import com.misterd.agritechevolved.gui.custom.ComposterScreen;
import com.misterd.agritechevolved.item.ATECreativeTab;
import com.misterd.agritechevolved.item.ATEItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
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

        Config.register(context);
        modEventBus.register(Config.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

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
                MenuScreens.register(ATEMenuTypes.COMPOSTER_MENU.get(), ComposterScreen::new);
                MenuScreens.register(ATEMenuTypes.BURNER_MENU.get(), BiomassBurnerScreen::new);
                MenuScreens.register(ATEMenuTypes.CAPACITOR_MENU.get(), CapacitorScreen::new);
            });
        }
    }
}