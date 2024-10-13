package com.wrgy.customportalmod.mixin;

import com.wrgy.customportalmod.client.CustomCommandBlockScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.wrgy.customportalmod.CustomPortalMod.WRGY_isAutoEnabled_39058;

@Mixin(CommandBlock.class)
public abstract class CommandBlockMixin extends Block {

    public CommandBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void onCommandBlockPlaced(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        // Ensure we are on the server side and the placer is a player
        if (WRGY_isAutoEnabled_39058 && !world.isClientSide && placer instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) placer;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CommandBlockEntity) {
                CommandBlockEntity commandBlockEntity = (CommandBlockEntity) blockEntity;
                BlockPos blockPos = blockEntity.getBlockPos();
                int[] blockPosition = new int[] { blockPos.getX(), blockPos.getY(), blockPos.getZ() };
                String command = commandBlockEntity.getCommandBlock().getCommand();
                command = CustomCommandBlockScreen.transformCommand(command, blockPosition, "", 0, true);
                String transformedCommand = CustomCommandBlockScreen.transformCommand(command, blockPosition, "", 0, false);
                commandBlockEntity.getCommandBlock().setCommand(transformedCommand);
                commandBlockEntity.setChanged();
                if (transformedCommand.length()>0) {
                    player.sendMessage(new TextComponent("Command set: " + transformedCommand), player.getUUID());
                }
            }
        }
    }
}

