package com.misterd.agritechevolved.gui;

import com.misterd.agritechevolved.gui.custom.AdvancedPlanterMenu;
import com.misterd.agritechevolved.gui.custom.BasicPlanterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ATEMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "agritechevolved");

    public static final DeferredHolder<MenuType<?>, MenuType<BasicPlanterMenu>> PLANTER_BLOCK_MENU = registerMenuType("planter_block_menu", BasicPlanterMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<AdvancedPlanterMenu>> ADVANCED_PLANTER_BLOCK_MENU = registerMenuType("advanced_planter_block_menu", AdvancedPlanterMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
