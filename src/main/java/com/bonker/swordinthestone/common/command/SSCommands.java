package com.bonker.swordinthestone.common.command;

import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SSCommands {
    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(Commands.literal("sword")
                .requires(stack -> stack.hasPermission(2))
                .then(Commands.argument("num", IntegerArgumentType.integer(1, 100))
                        .executes(SSCommands::makeSword))
                .executes(SSCommands::makeSword));
    }

    private static int makeSword(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        ServerPlayer player = context.getSource().getPlayerOrException();

        int num;
        try {
            num = context.getArgument("num", Integer.class);
        } catch (Exception e) {
            num = 1;
        }

        for (int i = 0; i < num; i++) {
            ItemStack stack = UniqueSwordItem.getRandom("random", level.getRandom());
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        return 1;
    }
}
