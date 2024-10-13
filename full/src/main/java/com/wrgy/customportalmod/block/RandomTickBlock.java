package com.wrgy.customportalmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import java.util.Random;

public class RandomTickBlock extends Block {

    private final int tickInterval; // Tick interval in ticks

    // Constructor to accept custom tick interval
    public RandomTickBlock(int tickInterval) {
        super(BlockBehaviour.Properties.of(Material.STONE).strength(3.5f));
        this.tickInterval = tickInterval;  // Initialize tick interval
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.ENABLED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) {
            ((ServerLevel) world).scheduleTick(pos, this, tickInterval); // Schedule the first tick
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        // Randomly toggle the enabled state every tick
        boolean powered = random.nextBoolean();
        world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.ENABLED, powered));

        // Schedule the next tick using the specified tick interval
        world.scheduleTick(pos, this, tickInterval);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ENABLED, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(BlockStateProperties.ENABLED);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return state.getValue(BlockStateProperties.ENABLED) && side.getOpposite() == state.getValue(BlockStateProperties.HORIZONTAL_FACING) ? 15 : 0;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placedState = super.getStateForPlacement(context);
        if (placedState != null) {
            for (final Direction facing : context.getNearestLookingDirections()) {
                if (facing.getAxis().isHorizontal()) {
                    return placedState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
                }
            }
        }
        return placedState;
    }
}
