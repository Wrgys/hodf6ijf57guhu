package com.wrgy.customportalmod;

import com.mojang.logging.LogUtils;
import com.wrgy.customportalmod.block.ModBlocks;
import com.wrgy.customportalmod.client.CustomCommandBlockScreen;
import com.wrgy.customportalmod.item.ModItems;
import net.kyrptonaught.customportalapi.CustomPortalBlock;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CustomPortalMod.MOD_ID)
public class CustomPortalMod {
    public static final String MOD_ID = "customportalmod";
    public static boolean WRGY_isAutoEnabled_39058 = false;
    public static final Logger LOGGER = LogUtils.getLogger();

    public CustomPortalMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(eventBus);
        ModBlocks.ENTITIES.register(eventBus);
        ModItems.register(eventBus);
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void clientSetup(final FMLClientSetupEvent event) {
        //MAKE SEE-THROUGH
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.wPortalBlock.get(), RenderType.translucent());
        CustomCommandBlockScreen.clientSetup(event);
    }

    private void setup(final FMLCommonSetupEvent event) {
        //PORTALS
        CustomPortalBuilder.beginPortal()
                .frameBlock(Blocks.IRON_BLOCK)
                .lightWithItem(Items.ENDER_EYE)
                .destDimID(new ResourceLocation("the_test"))
                .tintColor(254,254,254)
                .customPortalBlock((CustomPortalBlock) ModBlocks.wPortalBlock.get())
                .registerPortal();
    }

}
