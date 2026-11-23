package com.misterd.agritechevolved.gui;

import com.misterd.agritechevolved.AgritechEvolved;
import com.misterd.agritechevolved.gui.custom.BiomassBurnerMenu;
import com.misterd.agritechevolved.gui.custom.CapacitorMenu;
import com.misterd.agritechevolved.gui.custom.ComposterMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATEMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AgritechEvolved.MODID);

    public static final RegistryObject<MenuType<ComposterMenu>> COMPOSTER_MENU = registerMenuType("composter_menu", ComposterMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
    public static final RegistryObject<MenuType<CapacitorMenu>> CAPACITOR_MENU = registerMenuType("capacitor_menu", CapacitorMenu::new);
    public static final RegistryObject<MenuType<BiomassBurnerMenu>> BURNER_MENU = registerMenuType("burner_menu", BiomassBurnerMenu::new);

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}