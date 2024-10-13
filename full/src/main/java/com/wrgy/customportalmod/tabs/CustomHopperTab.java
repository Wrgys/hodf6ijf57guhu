package com.wrgy.customportalmod.tabs;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

//import de.markusbordihn.minecraft.framedhopper.item.ModItems;
import com.wrgy.customportalmod.item.ModItems;

public class CustomHopperTab {

    protected CustomHopperTab() {
    }

    public static final CreativeModeTab TAB_CUSTOM_HOPPERS = new CreativeModeTab("custom_hoppers") {
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.NAME1_CUSTOM_HOPPER.get());
        }
    };

}
