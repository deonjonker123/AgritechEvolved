package com.misterd.agritechevolved.command;

import com.misterd.agritechevolved.Config;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ATECommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("agritechevolved")
                        .then(Commands.literal("reload")
                            .executes(ctx -> reloadMainConfig(ctx.getSource())))
        );
    }

    private static int reloadMainConfig(CommandSourceStack source) {
        try {
            Config.loadConfig();
            source.sendSuccess(() -> Component.literal("AgriTech: Evolved config reloaded successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload AgriTech: Evolved config: " + e.getMessage()));
            return 0;
        }
    }
}