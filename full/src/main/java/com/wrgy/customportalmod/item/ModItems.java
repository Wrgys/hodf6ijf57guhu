package com.wrgy.customportalmod.item;

import com.wrgy.customportalmod.CustomPortalMod;
//import de.markusbordihn.minecraft.framedhopper.block.ModBlocks;
//import de.markusbordihn.minecraft.framedhopper.block.framedhopper.OakFramedHopper;
//import de.markusbordihn.minecraft.framedhopper.tabs.FramedHopperTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.wrgy.customportalmod.block.ModBlocks;
import com.wrgy.customportalmod.block.customhopper.Name1CustomHopper;
import com.wrgy.customportalmod.tabs.CustomHopperTab;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CustomPortalMod.MOD_ID);

    // Wooden framed Hoppers
    public static final RegistryObject<Item> NAME1_CUSTOM_HOPPER =
            ITEMS.register(Name1CustomHopper.NAME, () -> new BlockItem(ModBlocks.NAME1_CUSTOM_HOPPER.get(),
                    new Item.Properties().tab(CustomHopperTab.TAB_CUSTOM_HOPPERS)));

    // General items
   // public static final RegistryObject<Item> CHROMATIC_IRON_PROPELLER = ITEMS.register("chromatic_iron_propeller",
   //         () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    // Tooltip items
    public static final RegistryObject<Item> CHROMATIC_IRON_PROPELLER = ITEMS.register("chromatic_iron_propeller",
            () -> new TooltipItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), "item.customportalmod.chromatic_iron_propeller.description"));
    public static final RegistryObject<Item> POWER_ENGINE = ITEMS.register("power_engine",
            () -> new TooltipItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), "item.customportalmod.power_engine.description"));
    public static final RegistryObject<Item> TURBO_ENGINE = ITEMS.register("turbo_engine",
            () -> new TooltipItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), "item.customportalmod.turbo_engine.description"));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}