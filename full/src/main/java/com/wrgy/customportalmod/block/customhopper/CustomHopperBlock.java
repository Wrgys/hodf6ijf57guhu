package com.wrgy.customportalmod.block.customhopper;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
//import de.markusbordihn.minecraft.customhopper.block.customhopper.entity.CustomHopperBlockEntity;
import com.wrgy.customportalmod.block.customhopper.entity.CustomHopperBlockEntity;

public class CustomHopperBlock extends HopperBlock {

    public static final String NAME = "custom_hopper";

    protected static final VoxelShape SHAPE_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public CustomHopperBlock(Properties properties) {
        super(properties);
    }

    public CustomHopperBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.STONE)
                .requiresCorrectToolForDrops().strength(3.0F, 4.8F).sound(SoundType.METAL).noOcclusion());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
                               CollisionContext collisionContext) {
        return SHAPE_AABB;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState,
                            LivingEntity livingEntity, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName() && level
                .getBlockEntity(blockPos) instanceof CustomHopperBlockEntity customHopperBlockEntity) {
            customHopperBlockEntity.setCustomName(itemStack.getHoverName());
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
                                 InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof CustomHopperBlockEntity customHopperBlockEntity) {
                player.openMenu(customHopperBlockEntity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (level.getBlockEntity(blockPos) instanceof CustomHopperBlockEntity customHopperBlockEntity) {
            CustomHopperBlockEntity.entityInside(level, blockPos, blockState, entity,
                    customHopperBlockEntity);
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos,
                         BlockState targetBlockState, boolean isMoving) {
        if (!blockState.is(targetBlockState.getBlock())) {
            if (level
                    .getBlockEntity(blockPos) instanceof CustomHopperBlockEntity customHopperBlockEntity) {
                Containers.dropContents(level, blockPos, customHopperBlockEntity);
                level.updateNeighbourForOutputSignal(blockPos, this);
            }
            super.onRemove(blockState, level, blockPos, targetBlockState, isMoving);
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
                                                                  BlockEntityType<T> blockEntityType) {
        return null;
    }

}
