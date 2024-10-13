package com.wrgy.customportalmod.block.customhopper;

import com.wrgy.customportalmod.block.customhopper.entity.CustomHopperBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.wrapper.InvWrapper;

public class CustomHopperItemHandler extends InvWrapper {

    protected static final Logger log = LogManager.getLogger("Custom Hopper");

    private final CustomHopperBlockEntity hopper;

    public CustomHopperItemHandler(CustomHopperBlockEntity hopper) {
        super(hopper);
        this.hopper = hopper;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (simulate) {
            return super.insertItem(slot, stack, simulate);
        } else {
            boolean wasEmpty = getInv().isEmpty();
            int originalStackSize = stack.getCount();
            stack = super.insertItem(slot, stack, simulate);
            if (wasEmpty && originalStackSize > stack.getCount() && !hopper.isOnCustomCooldown()) {
                hopper.setCooldown(8);
            }
            return stack;
        }
    }

}
