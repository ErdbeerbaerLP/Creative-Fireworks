package de.erdbeerbaerlp.creativefirework.blocks;

import java.util.ArrayList;
import java.util.List;

import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.BlockTileEntity;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import de.erdbeerbaerlp.creativefirework.gui.GuiFirework;
import de.erdbeerbaerlp.creativefirework.util.IHasModel;
import de.erdbeerbaerlp.creativefirework.util.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.DistExecutor;

public class BlockFireworkShooter extends BlockTileEntity<TEFirework> implements IHasModel {

	public BlockFireworkShooter(Properties properties) {
		super(properties);
		setRegistryName("fireworkshooter");
		Lists.BLOCKS.add(this);
		Lists.ITEMS.add(new ItemBlock(this, new Item.Properties().addToolType(ToolType.PICKAXE, 1)).setRegistryName(this.getRegistryName()));

	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		// TODO Auto-generated method stub
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TEFirework te = (TEFirework) worldIn.getTileEntity(pos);
		if(te == null) {
			System.out.println("TileEntity = null");
			return;
		}
		if(stack.hasTag()) {
			NBTTagCompound data = stack.getTag().getCompound("blockdata");
			if(data.size() != 0) {
				data.setInt("x", pos.getX());
				data.setInt("y", pos.getY());
				data.setInt("z", pos.getZ());
				te.read(data);
				te.markDirty();
			}}
		//		worldIn.notifyBlockUpdate(pos, state, state, 2); BlockPlanks
	}
	public static ItemStack addNBT(ItemStack is, TEFirework te) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound blockdata = new NBTTagCompound();

		blockdata.setInt("flight", te.getFlight());
		blockdata.setInt("delay", te.getDelay());
		blockdata.setInt("mode", te.getMode());
		blockdata.setInt("type", te.getFWType());
		nbt.setTag("blockdata", blockdata);
		is.setTag(nbt);
		return is;
	}
	@Override
	public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
		List<ItemStack> dropz = new ArrayList<ItemStack>();
		dropz.add(addNBT(new ItemStack(this), (TEFirework) world.getTileEntity(pos)));
	}
	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		// TODO Auto-generated method stub
		if(stack.hasTag()) {
			NBTTagCompound c = stack.getTag().getCompound("blockdata");
			//		System.out.println(tooltip);
			if(c != null && c.size() != 0) {
				tooltip.add(new TextComponentString(getLores()[0]));
				tooltip.add(new TextComponentString(getLores()[1]));
				tooltip.add(new TextComponentString(getLores()[2]));
				tooltip.add(new TextComponentString(""));
				tooltip.add(new TextComponentString("§e§n"+I18n.format("lore.storedData")));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.flight")+" "+c.getInt("flight")));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.delay")+" "+c.getInt("delay")+" "+I18n.format("gui.seconds")));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.fwmode")+" "+I18n.format(c.getInt("mode") > 5 ?"gui.fwmodes":("gui.fwmodes."+c.getInt("mode")))));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.fwtype")+" "+I18n.format(c.getInt("type") == 0?"gui.fwtype0":("item.fireworksCharge.type."+(c.getInt("type")-1)))));
			}else {
				tooltip.add(new TextComponentString(getLores()[0]));
				tooltip.add(new TextComponentString(getLores()[1]));
				tooltip.add(new TextComponentString(getLores()[2]));
			}
		}else {
			tooltip.add(new TextComponentString(getLores()[0]));
			tooltip.add(new TextComponentString(getLores()[1]));
			tooltip.add(new TextComponentString(getLores()[2]));
		}
		//	System.out.println(tooltip);
	}
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
			boolean willHarvest, IFluidState fluid) {
		if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool)
	{
		super.harvestBlock(world, player, pos, state, te, tool);
//		world.set(pos);
	}
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {

	TEFirework t = getTileEntity(worldIn, pos);
	if(hand == EnumHand.MAIN_HAND) DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
		openGui(t);
	});

	return true;
	}
	@OnlyIn(Dist.CLIENT)
	private void openGui(TEFirework t) {
		Minecraft.getInstance().displayGuiScreen(new GuiFirework(t));
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing side) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			EntityPlayer player) {
		// TODO Auto-generated method stub
		return addNBT(new ItemStack(this), (TEFirework) world.getTileEntity(pos));
	}
	public String[] getLores() {
		return new String[] {I18n.format("lore.shooter.1"), "", I18n.format("lore.shooter.2")};
	}

//	@Override
//	public Class<TEFirework> getTileEntityClass() {
//		// TODO Auto-generated method stub
//		return TEFirework.class;
//	}
	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
		// TODO Auto-generated method stub
		return new TEFirework();
	}
	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Class<TEFirework> getTileEntityClass() {
		// TODO Auto-generated method stub
		return TEFirework.class;
	}
	@Override
	public void registerModels() {
		// TODO Auto-generated method stub
		System.out.println("Test");
	}

}
