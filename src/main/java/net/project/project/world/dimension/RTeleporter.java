package net.project.project.world.dimension;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.project.project.block.ModBlocks;
import net.project.project.block.custom.RDimensionBlock;

import java.util.function.Function;

public class RTeleporter implements ITeleporter {
    public static BlockPos thisPos = BlockPos.ZERO;
    public static boolean insideDimension = true;

    public RTeleporter(BlockPos pos, boolean insideDim) {
        thisPos = pos;
        insideDimension = insideDim;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destinationWorld,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
        entity = repositionEntity.apply(false);
        double y = 61;

        if (!insideDimension) {
            y = thisPos.getY();
        }

        BlockPos destinationPos = new BlockPos(thisPos.getX(), y, thisPos.getZ());

        int tries = 0;
        while ((destinationWorld.getBlockState(destinationPos).getMaterial() != Material.AIR) &&
                !destinationWorld.getBlockState(destinationPos).isReplaceable(Fluids.WATER) &&
                destinationWorld.getBlockState(destinationPos.up()).getMaterial() != Material.AIR &&
                !destinationWorld.getBlockState(destinationPos.up()).isReplaceable(Fluids.WATER) && tries < 25) {
            destinationPos = destinationPos.up(2);
            tries++;
        }

        entity.setPositionAndUpdate(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ());

        if (insideDimension) {
            boolean doSetBlock = true;
            for (BlockPos checkPos : BlockPos.getAllInBoxMutable(destinationPos.down(10).west(10), destinationPos.up(10).east(10))) {
                if (destinationWorld.getBlockState(checkPos).getBlock() instanceof RDimensionBlock) {
                    doSetBlock = false;
                    break;
                }
            }
            if (doSetBlock) {
                destinationWorld.setBlockState(destinationPos, ModBlocks.REDIUM.get().getDefaultState());
            }
        }

        return entity;
    }
}