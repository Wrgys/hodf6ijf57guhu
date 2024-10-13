package com.wrgy.customportalmod.mixin;

import com.wrgy.customportalmod.client.CustomCommandBlockScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.Commands;
import static com.wrgy.customportalmod.CustomPortalMod.WRGY_isAutoEnabled_39058;


@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    // Inject into the 'saveToItem' method
    @Inject(method = "saveToItem", at = @At("RETURN"))
    public void onSaveToItem(ItemStack itemStack, CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;

        if (blockEntity instanceof CommandBlockEntity && WRGY_isAutoEnabled_39058) {
            CompoundTag nbtData = itemStack.getTag();
            Minecraft client = Minecraft.getInstance();
            BlockPos blockPos = blockEntity.getBlockPos();
            int[] blockPosition = new int[] { blockPos.getX(), blockPos.getY(), blockPos.getZ() };

            if (client.player != null && nbtData != null && nbtData.contains("BlockEntityTag")) {
                CompoundTag blockEntityTag = nbtData.getCompound("BlockEntityTag");
                String command = blockEntityTag.getString("Command");
                command = CustomCommandBlockScreen.transformCommand(command, blockPosition, "", 0, true);
                blockEntityTag.putString("Command", command);
                nbtData.put("BlockEntityTag", blockEntityTag);
                blockEntity.setChanged();
            }
        }
    }
}
