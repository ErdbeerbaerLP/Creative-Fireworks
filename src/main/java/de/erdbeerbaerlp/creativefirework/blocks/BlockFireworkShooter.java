package de.erdbeerbaerlp.creativefirework.blocks;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import de.erdbeerbaerlp.creativefirework.MainClass.Shape;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TileEntityShooter;
import de.erdbeerbaerlp.creativefirework.gui.GuiFirework;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class BlockFireworkShooter extends Block{
	public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
	public static final IntegerProperty FLIGHT = IntegerProperty.create("flight_duration", 0, 3);
	public static final IntegerProperty DELAY = IntegerProperty.create("delay", 1, 10);
	public static final EnumProperty<Mode> MODE = EnumProperty.create("mode", Mode.class);
	public static final BooleanProperty CREATIVE = BooleanProperty.create("creative");


	public BlockFireworkShooter(Properties properties) {
		super(properties);
		setRegistryName("fireworkshooter");
		setDefaultState(this.stateContainer.getBaseState().with(SHAPE, Shape.RANDOM).with(FLIGHT, 3).with(DELAY, 3).with(MODE, Mode.ALWAYS_OFF).with(CREATIVE, false));
	}
	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
		return new TileEntityShooter();
	}
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	protected void fillStateContainer(Builder<Block, IBlockState> builder) {
		builder.add(SHAPE,FLIGHT,DELAY,MODE,CREATIVE);
	}
	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return this.stateContainer.getBaseState().with(SHAPE, Shape.RANDOM).with(FLIGHT, 3).with(DELAY, 3).with(MODE, Mode.ALWAYS_OFF).with(CREATIVE, context.getPlayer().isCreative());
	}
	private ItemStack addNBT(ItemStack is, IBlockState state) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound blockdata = new NBTTagCompound();
		blockdata.setInt("flight", state.get(FLIGHT));
		blockdata.setInt("delay", state.get(DELAY));
		blockdata.setInt("mode", state.get(MODE).getID());
		blockdata.setInt("type", state.get(SHAPE).getID());
		nbt.setTag("blockdata", blockdata);
		is.setTag(nbt);
		return is;
	}
	private String getTypeString(int type) {
		switch(Shape.getShape(type)) {
		case SMALL_BALL:
		case LARGE_BALL:
		case STAR:
		case CREEPER:
		case BURST:
		case RANDOM:
			return "."+Shape.getShape(type).getName();
		default:
			return "";
		}
	}
	@Override
	public void getDrops(IBlockState state, NonNullList<ItemStack> drops, World world, BlockPos pos, int fortune) {
		drops.clear();
	}
	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		// TODO Auto-generated method stub
		if(stack.hasTag()) {
			NBTTagCompound c = stack.getTag().getCompound("blockdata");
			if(c != null && c.size() != 0) {
				tooltip.add(new TextComponentString(getLores()[0]));
				tooltip.add(new TextComponentString(getLores()[1]));
				tooltip.add(new TextComponentString(getLores()[2]));
				tooltip.add(new TextComponentString(""));
				tooltip.add(new TextComponentString("§e§n"+I18n.format("lore.storedData")));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.flight")+" "+c.getInt("flight")));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.delay")+" "+c.getInt("delay")+" "+I18n.format("gui.seconds")));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.fwmode")+" "+I18n.format(c.getInt("mode") > 5 ?"gui.fwmodes":("gui.fwmodes."+c.getInt("mode")))));
				tooltip.add(new TextComponentString("§e"+I18n.format("gui.fwtype")+" "+I18n.format(c.getInt("type") == 0?"gui.fwtype0":("item.minecraft.firework_star.shape"+getTypeString(c.getInt("type"))))));
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
	}
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
			boolean willHarvest, IFluidState fluid) {
		if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		TileEntity te = worldIn.getTileEntity(pos);
		NBTTagCompound tag = stack.getTag();
		if(te != null && te instanceof TileEntityShooter && tag != null) {
			((TileEntityShooter)te).setPaper(tag.hasKey("paper")?tag.getInt("paper"):5);
			((TileEntityShooter)te).setGunpowder(tag.hasKey("gunpowder")?tag.getInt("gunpowder"):5);
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		if(newState.getBlock() instanceof BlockFireworkShooter) return;
		super.onReplaced(state, worldIn, pos, newState, isMoving);
		
	}
	@Override
	public void harvestBlock(World world, net.minecraft.entity.player.EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
		if (!world.isRemote) {
			NBTTagCompound tag = new NBTTagCompound();
			if(te != null && te instanceof TileEntityShooter) {
				tag.setInt("paper", ((TileEntityShooter)te).getPaper());
				tag.setInt("gunpowder", ((TileEntityShooter)te).getGunpowder());
			}
			ItemStack item = addNBT(new ItemStack(this), state);
			item.setTag(tag.merge(item.getTag()));
			world.spawnEntity(new EntityItem(world, pos.getX(),pos.getY(),pos.getZ(), item));
			te.remove();
		}
		
		super.harvestBlock(world, player, pos, state, te, stack);
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.MAIN_HAND) DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
			Minecraft.getInstance().displayGuiScreen(new GuiFirework(pos, worldIn));
		});

		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockReader world, BlockPos pos, EnumFacing side) {
		// TODO Auto-generated method stub
		return (state.get(MODE).equals(Mode.REDSTONE) || state.get(MODE).equals(Mode.REDSTONE_INVERTED));
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player) {
		return addNBT(new ItemStack(this), state);
	}
	public String[] getLores() {
		return new String[] {I18n.format("lore.shooter.1"), "", I18n.format("lore.shooter.2")};
	}
	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static enum Mode implements IStringSerializable{
		REDSTONE(0, "redstone"),
		REDSTONE_INVERTED(1, "redstone_inverted"),
		ALWAYS_OFF(2, "always_off"),
		ALWAYS_ON(3, "always_on"),
		AUTOMATIC_NIGHT(4, "auto_night"),
		AUTOMATIC_DAY(5, "auto_day");

		private static final Mode[] modes = Arrays.stream(values()).sorted(Comparator.comparingInt((mode) -> {
			return mode.ID;
		})).toArray((modeID) -> {
			return new Mode[modeID];
		});
		private final int ID;
		private final String name;

		private Mode(int ID, String name) {
			this.ID = ID;
			this.name = name;
		}

		public int getID() {
			return this.ID;
		}
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return this.name;
		}

		public static Mode getMode(int index) {
			return index >= 0 && index < modes.length ? modes[index] : ALWAYS_OFF;
		}

	}


}
