package com.wrgy.customportalmod.utils;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerUtils {

    protected ItemHandlerUtils() {}

    public static boolean isItemHandler(BlockEntity blockEntity, Direction direction) {
        Capability<IItemHandler> itemHandlerCapability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        return blockEntity != null && direction != null && itemHandlerCapability != null
                && blockEntity.getCapability(itemHandlerCapability, direction).isPresent();
    }

    public static IItemHandler getItemHandler(BlockEntity blockEntity, Direction direction) {
        if (!isItemHandler(blockEntity, direction)) {
            return null;
        }
        Capability<IItemHandler> itemHandlerCapability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        if (itemHandlerCapability != null) {
            Optional<IItemHandler> capability =
                    blockEntity.getCapability(itemHandlerCapability, direction).resolve();
            if (capability.isPresent()) {
                return capability.get();
            }
        }
        return null;
    }

}