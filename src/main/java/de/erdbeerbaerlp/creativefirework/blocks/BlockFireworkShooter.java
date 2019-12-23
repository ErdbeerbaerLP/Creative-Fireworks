package de.erdbeerbaerlp.creativefirework.blocks;

import de.erdbeerbaerlp.creativefirework.MainClass.Shape;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TileEntityShooter;
import de.erdbeerbaerlp.creativefirework.gui.GuiFirework;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"deprecation", "NoTranslation"})
public class BlockFireworkShooter extends Block {
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
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityShooter();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(SHAPE, FLIGHT, DELAY, MODE, CREATIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Shape shape = Shape.RANDOM;
        int flight = 3;
        int delay = 3;
        Mode mode = Mode.ALWAYS_OFF;
        if (context.getItem().hasTag()) {
            final CompoundNBT nbt = context.getItem().getTag();
            if (nbt != null && nbt.contains("blockdata")) {
                final CompoundNBT bd = nbt.getCompound("blockdata");
                if (bd.contains("flight")) flight = bd.getInt("flight");
                if (bd.contains("delay")) delay = bd.getInt("delay");
                if (bd.contains("mode")) mode = Mode.getMode(bd.getInt("mode"));
                if (bd.contains("shape")) shape = Shape.getShape(bd.getInt("shape"));
            }
        }
        return this.stateContainer.getBaseState()
                .with(SHAPE, shape)
                .with(FLIGHT, flight)
                .with(DELAY, delay)
                .with(MODE, mode)
                .with(CREATIVE, context.getPlayer().isCreative());
    }

    private ItemStack addNBT(ItemStack is, BlockState state) {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT blockdata = new CompoundNBT();
        blockdata.putInt("flight", state.get(FLIGHT));
        blockdata.putInt("delay", state.get(DELAY));
        blockdata.putInt("mode", state.get(MODE).getID());
        blockdata.putInt("type", state.get(SHAPE).getID());
        nbt.put("blockdata", blockdata);
        is.setTag(nbt);
        return is;
    }

    private String getTypeString(int type) {
        switch (Shape.getShape(type)) {
            case SMALL_BALL:
            case LARGE_BALL:
            case STAR:
            case CREEPER:
            case BURST:
            case RANDOM:
                return "." + Shape.getShape(type).getName();
            default:
                return "";
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
        return new ArrayList<>();
    }

    @Override
    public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
                               ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT c = stack.getTag().getCompound("blockdata");
            if (c.size() != 0) {
                tooltip.add(new StringTextComponent(getLores()[0]));
                tooltip.add(new StringTextComponent(getLores()[1]));
                tooltip.add(new StringTextComponent(getLores()[2]));
                tooltip.add(new StringTextComponent(""));
                tooltip.add(new StringTextComponent("§e§n" + I18n.format("lore.storedData")));
                tooltip.add(new StringTextComponent("§e" + I18n.format("gui.flight") + " " + c.getInt("flight")));
                tooltip.add(new StringTextComponent("§e" + I18n.format("gui.delay") + " " + c.getInt("delay") + " " + I18n.format("gui.seconds")));
                tooltip.add(new StringTextComponent("§e" + I18n.format("gui.fwmode") + " " + I18n.format(c.getInt("mode") > 5 ? "gui.fwmodes" : ("gui.fwmodes." + c.getInt("mode")))));
                tooltip.add(new StringTextComponent("§e" + I18n.format("gui.fwtype") + " " + I18n.format(c.getInt("type") == 0 ? "gui.fwtype0" : ("item.minecraft.firework_star.shape" + getTypeString(c.getInt("type"))))));
            } else {
                tooltip.add(new StringTextComponent(getLores()[0]));
                tooltip.add(new StringTextComponent(getLores()[1]));
                tooltip.add(new StringTextComponent(getLores()[2]));
            }
        } else {
            tooltip.add(new StringTextComponent(getLores()[0]));
            tooltip.add(new StringTextComponent(getLores()[1]));
            tooltip.add(new StringTextComponent(getLores()[2]));
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                   boolean willHarvest, IFluidState fluid) {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer,
                                ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        CompoundNBT tag = stack.getTag();
        if (te instanceof TileEntityShooter && tag != null) {
            ((TileEntityShooter) te).setPaper(tag.contains("paper") ? tag.getInt("paper") : 5);
            ((TileEntityShooter) te).setGunpowder(tag.contains("gunpowder") ? tag.getInt("gunpowder") : 5);
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof BlockFireworkShooter) return;
        super.onReplaced(state, worldIn, pos, newState, isMoving);

    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        if (!world.isRemote) {
            CompoundNBT tag = new CompoundNBT();
            if (te instanceof TileEntityShooter) {
                tag.putInt("paper", ((TileEntityShooter) te).getPaper());
                tag.putInt("gunpowder", ((TileEntityShooter) te).getGunpowder());
            }
            ItemStack item = addNBT(new ItemStack(this), state);
            item.setTag(tag.merge(item.getTag()));
            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), item));
            te.remove();
            world.removeBlock(pos, false);
        }

        super.harvestBlock(world, player, pos, state, te, stack);
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (hand == Hand.MAIN_HAND)
            DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().displayGuiScreen(new GuiFirework(pos, worldIn)));
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return (state.get(MODE).equals(Mode.REDSTONE) || state.get(MODE).equals(Mode.REDSTONE_INVERTED));
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return addNBT(new ItemStack(this), state);
    }

    public String[] getLores() {
        return new String[]{I18n.format("lore.shooter.1"), "", I18n.format("lore.shooter.2")};
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    public enum Mode implements IStringSerializable {
        REDSTONE(0, "redstone"),
        REDSTONE_INVERTED(1, "redstone_inverted"),
        ALWAYS_OFF(2, "always_off"),
        ALWAYS_ON(3, "always_on"),
        AUTOMATIC_NIGHT(4, "auto_night"),
        AUTOMATIC_DAY(5, "auto_day");

        private static final Mode[] modes = Arrays.stream(values()).sorted(Comparator.comparingInt((mode) -> mode.ID)).toArray(Mode[]::new);
        private final int ID;
        private final String name;

        Mode(int ID, String name) {
            this.ID = ID;
            this.name = name;
        }

        public static Mode getMode(int index) {
            return index >= 0 && index < modes.length ? modes[index] : ALWAYS_OFF;
        }

        public int getID() {
            return this.ID;
        }

        @Override
        public String getName() {
            return this.name;
        }

    }


}
