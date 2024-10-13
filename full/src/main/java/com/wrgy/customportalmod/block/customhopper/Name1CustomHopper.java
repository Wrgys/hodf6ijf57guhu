package com.wrgy.customportalmod.block.customhopper;

import javax.annotation.Nullable;

//import de.markusbordihn.minecraft.framedhopper.block.framedhopper.FramedHopperBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

//import de.markusbordihn.minecraft.framedhopper.block.ModBlocks;
//import de.markusbordihn.minecraft.framedhopper.block.framedhopper.entity.OakFramedHopperEntity;
import com.wrgy.customportalmod.block.ModBlocks;
import com.wrgy.customportalmod.block.customhopper.entity.Name1CustomHopperEntity;
import com.wrgy.customportalmod.block.customhopper.CustomHopperBlock;

public class    Name1CustomHopper extends CustomHopperBlock {

    public static final String NAME = "name1_custom_hopper";

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new Name1CustomHopperEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
                                                                  BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null
                : createTickerHelper(blockEntityType, ModBlocks.NAME1_HOPPER_ENTITY.get(),
                Name1CustomHopperEntity::pushItemsTick);
    }

}
