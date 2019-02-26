package de.erdbeerbaerlp.creativefirework.blocks.tileEntity;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockTileEntity<TE extends TileEntity> extends Block {

	public BlockTileEntity(Properties properties) {
		super(properties);
	}
	public abstract Class<TE> getTileEntityClass();
	public TE getTileEntity(World world, BlockPos pos) {
		return (TE)world.getTileEntity(pos);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
		return null;
	}

}