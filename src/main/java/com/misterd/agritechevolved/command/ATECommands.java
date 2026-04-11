package com.misterd.agritechevolved.command;

import com.misterd.agritechevolved.Config;
import com.misterd.agritechevolved.config.CompostableConfig;
import com.misterd.agritechevolved.config.CompostableOverrideConfig;
import com.misterd.agritechevolved.config.PlantablesConfig;
import com.misterd.agritechevolved.config.PlantablesOverrideConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ATECommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("agritechevolved")
                        .then(Commands.literal("reload")
                                .then(Commands.literal("plantables")
                                        .executes(ctx -> reloadPlantables(ctx.getSource())))
                                .then(Commands.literal("compostable")
                                        .executes(ctx -> reloadCompostable(ctx.getSource())))
                                .then(Commands.literal("config")
                                        .executes(ctx -> reloadMainConfig(ctx.getSource())))
                                .executes(ctx -> reloadAll(ctx.getSource())))
        );
    }

    // -------------------------------------------------------------------------
    // Reload handlers
    // -------------------------------------------------------------------------

    private static int reloadPlantables(CommandSourceStack source) {
        try {
            PlantablesOverrideConfig.resetErrorFlag();
            PlantablesConfig.loadConfig();
            source.sendSuccess(() -> Component.literal("AgriTech: Evolved plantables config reloaded successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload AgriTech: Evolved plantables config: " + e.getMessage()));
            return 0;
        }
    }

    private static int reloadCompostable(CommandSourceStack source) {
        try {
            CompostableOverrideConfig.resetErrorFlag();
            CompostableConfig.loadConfig();
            source.sendSuccess(() -> Component.literal("AgriTech: Evolved compostable config reloaded successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload AgriTech: Evolved compostable config: " + e.getMessage()));
            return 0;
        }
    }

    private static int reloadMainConfig(CommandSourceStack source) {
        try {
            Config.loadConfig();
            source.sendSuccess(() -> Component.literal("AgriTech: Evolved main config reloaded successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload AgriTech: Evolved main config: " + e.getMessage()));
            return 0;
        }
    }

    private static int reloadAll(CommandSourceStack source) {
        try {
            PlantablesOverrideConfig.resetErrorFlag();
            CompostableOverrideConfig.resetErrorFlag();
            Config.loadConfig();
            PlantablesConfig.loadConfig();
            CompostableConfig.loadConfig();
            source.sendSuccess(() -> Component.literal("All AgriTech: Evolved configs reloaded successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload AgriTech: Evolved configs: " + e.getMessage()));
            return 0;
        }
    }
}