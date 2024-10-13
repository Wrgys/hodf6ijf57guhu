package com.wrgy.customportalmod.block.customhopper.entity;

//import de.markusbordihn.minecraft.framedhopper.block.framedhopper.entity.CustomHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

//import de.markusbordihn.minecraft.framedhopper.block.ModBlocks;

import com.wrgy.customportalmod.block.ModBlocks;
import com.wrgy.customportalmod.block.customhopper.entity.CustomHopperBlockEntity;

public class Name1CustomHopperEntity extends CustomHopperBlockEntity {

    public Name1CustomHopperEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.NAME1_HOPPER_ENTITY.get(), blockPos, blockState);
    }

}