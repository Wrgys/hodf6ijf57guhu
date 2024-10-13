package com.wrgy.customportalmod.block;

import java.util.function.Supplier;

import com.wrgy.customportalmod.CustomPortalMod;
import com.wrgy.customportalmod.item.ModItems;
import com.wrgy.customportalmod.block.customhopper.Name1CustomHopper;
import com.wrgy.customportalmod.block.customhopper.entity.Name1CustomHopperEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;


public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CustomPortalMod.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CustomPortalMod.MOD_ID);

    // Custom portals
    public static final RegistryObject<Block> wPortalBlock = registerBlockWithoutItem("wportalblock",
            ()-> new wgPortalBlock(BlockBehaviour.Properties.of(Material.GLASS)));

    // Custom hoppers
    public static final RegistryObject<Block> NAME1_CUSTOM_HOPPER =
            BLOCKS.register(Name1CustomHopper.NAME, Name1CustomHopper::new);

    // Custom hopper entities
    public static final RegistryObject<BlockEntityType<Name1CustomHopperEntity>> NAME1_HOPPER_ENTITY =
            ENTITIES.register(Name1CustomHopper.NAME, () -> BlockEntityType.Builder
                    .of(Name1CustomHopperEntity::new, NAME1_CUSTOM_HOPPER.get()).build(null));

    //Other Blocks
    public static final RegistryObject<Block> upgradeBlock_Completed = registerBlock("upgradeblock_completed",
            ()-> new Block(BlockBehaviour.Properties.of(Material.GLASS)), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> upgradeBlock_Initial = registerBlock("upgradeblock_initial",
            ()-> new Block(BlockBehaviour.Properties.of(Material.GLASS)), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> seaLantern_Off = registerBlock("sea_lantern_off",
            ()-> new Block(BlockBehaviour.Properties.of(Material.GLASS)), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> seaLantern10_Off = registerBlock("chipped_sea_lantern_10_off",
            ()-> new Block(BlockBehaviour.Properties.of(Material.GLASS)), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> randomTickBlock_1 = registerBlock("random_tick_block_1",
            () -> new RandomTickBlock(1), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> randomTickBlock_2 = registerBlock("random_tick_block_2",
            () -> new RandomTickBlock(2), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> randomTickBlock_3 = registerBlock("random_tick_block_3",
            () -> new RandomTickBlock(3), CreativeModeTab.TAB_MISC);


    private static <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
                                                                     CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name,toReturn,tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}