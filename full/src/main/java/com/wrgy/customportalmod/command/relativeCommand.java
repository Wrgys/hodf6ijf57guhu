package com.wrgy.customportalmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wrgy.customportalmod.CustomPortalMod.WRGY_isAutoEnabled_39058;

@Mod.EventBusSubscriber
public class relativeCommand {

    public relativeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder) Commands.literal("r").requires((source) -> {
            return source.hasPermission(2);
        }).then(Commands.argument("target_block_position", BlockPosArgument.blockPos())
                .then(Commands.argument("anchor_block_position", BlockPosArgument.blockPos())
                        .executes((context) -> {
                            return calculateRelativePosition(
                                    context.getSource(),
                                    BlockPosArgument.getLoadedBlockPos(context, "target_block_position"),
                                    BlockPosArgument.getLoadedBlockPos(context, "anchor_block_position")
                            );
                        }))));

        dispatcher.register((LiteralArgumentBuilder) Commands.literal("rr").requires((source) -> {
            return source.hasPermission(2);
        })
          .executes((context) -> {
            return toggleAutoTransformCoordinates(
                    context.getSource()
            );
                        }));
    }

    private static int calculateRelativePosition(CommandSourceStack source, BlockPos blockPos, BlockPos commandBlockPos) throws CommandSyntaxException {
        // Calculate the relative coordinates
        int relativeX = blockPos.getX() - commandBlockPos.getX();
        int relativeY = blockPos.getY() - commandBlockPos.getY();
        int relativeZ = blockPos.getZ() - commandBlockPos.getZ();

        // Format the relative coordinates with the "~" symbol
        String relativePosition = String.format("~%d ~%d ~%d", relativeX, relativeY, relativeZ);

        // Send the message to the player
        source.sendSuccess(new TextComponent(relativePosition), true);

        return 1;
    }

    private static int toggleAutoTransformCoordinates(CommandSourceStack source) throws CommandSyntaxException {
        WRGY_isAutoEnabled_39058 = !WRGY_isAutoEnabled_39058;
        source.sendSuccess(new TextComponent(WRGY_isAutoEnabled_39058 ? "Auto ON" : "Auto OFF"),false);
        return 1;
    }


    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        relativeCommand.register(event.getDispatcher());
    }
}
