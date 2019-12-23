package de.erdbeerbaerlp.creativefirework.blocks.tileEntity;

import de.erdbeerbaerlp.creativefirework.MainClass;
import de.erdbeerbaerlp.creativefirework.MainClass.Shape;
import de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter.*;

public class TileEntityShooter extends TileEntity implements ITickableTileEntity {
    final VoxelShape COLLECTION_AREA_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);
    int i = 0;
    final Random r = new Random();
    public int paper = 5;
    public int gunpowder = 5;

    public TileEntityShooter() {
        super(MainClass.TE_FWSHOOTER);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("paper", paper);
        compound.putInt("gunpowder", gunpowder);
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        paper = compound.getInt("paper");
        gunpowder = compound.getInt("gunpowder");
        super.read(compound);
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        System.out.println("[DEBUG]:Server sent tile sync packet");
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);

        return new SUpdateTileEntityPacket(this.pos, 0, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        System.out.println("[DEBUG]:Client recived tile sync packet");
        read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        System.out.println("GetUpdate");
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Override
    public void tick() {

        final BlockPos pos = this.getPos();
        final BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockFireworkShooter)) return;
        if (world.isRemote) {
            i = 0;
            return;
        }
        if (!state.get(CREATIVE))
            for (ItemEntity item : getCaptureItems()) {
                int itemCount = item.getItem().getCount();
                if (item.getItem().getItem() == Items.PAPER) {
                    setPaper(5 * itemCount + getPaper());
                    item.remove();
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() == Items.GUNPOWDER) {
                    setGunpowder(5 * itemCount + getGunpowder());
                    item.remove();
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() instanceof FireworkRocketItem) {
                    setPaper(5 * itemCount + getPaper());
                    setGunpowder(5 * itemCount + getGunpowder());
                    item.remove();
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() instanceof FireworkStarItem) {
                    setGunpowder(5 * itemCount + getGunpowder());
                    item.remove();
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() == Blocks.SUGAR_CANE.asItem()) {
                    setPaper(5 * itemCount + getPaper());
                    item.remove();
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() == Items.BOOK || item.getItem().getItem() == Items.WRITTEN_BOOK || item.getItem().getItem() == Items.WRITABLE_BOOK) {
                    setPaper(15 * itemCount + getPaper());
                    item.setItem(new ItemStack(Items.LEATHER, itemCount));
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() instanceof EnchantedBookItem) {
                    setPaper(30 * itemCount + getPaper());
                    item.remove();
                    world.notifyBlockUpdate(pos, state, state, 2);
                } else if (item.getItem().getItem() == Blocks.TNT.asItem()) {
                    setGunpowder(25 * itemCount + getGunpowder());
                    item.setItem(new ItemStack(Blocks.SAND, 4 * itemCount));
                    world.notifyBlockUpdate(pos, state, state, 2);
                }
            }
        if ((getGunpowder() <= 0 || getPaper() <= 0) && !state.get(CREATIVE)) return;
        switch (state.get(MODE)) {
            case REDSTONE:
                if (!world.isBlockPowered(pos)) {
                    i = 0;
                    return;
                }
                break;
            case REDSTONE_INVERTED:
                if (world.isBlockPowered(pos)) {
                    i = 0;
                    return;
                }
                break;
            case ALWAYS_OFF:
                i = 0;
                return;
            case ALWAYS_ON:
                break;
            case AUTOMATIC_NIGHT:
                if (checkUp(world, pos) || !((world.getSkylightSubtracted()) > 5)) {
                    i = 0;
                    return;
                }

                break;

            case AUTOMATIC_DAY:
                if (checkUp(world, pos) || !((world.getSkylightSubtracted()) < 5)) {
                    i = 0;
                    return;
                }
                break;
            default:
                return;
        }
        final boolean canFire = world.isAirBlock(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));

        if (i > (state.get(DELAY) * 20)) {
            final int[] colors = new int[r.nextInt(8)];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = r.nextInt(99999999);
            }
            final int[] fade = new int[r.nextInt(8)];
            for (int i = 0; i < fade.length; i++) {
                fade[i] = r.nextInt(99999999);
            }
            int hole = r.nextInt(3);
            int hole2 = r.nextInt(3);
            double addx = 0;
            double addz = 0;
            if (hole == 0) addx = 0.2;
            if (hole == 1) addx = 0.5;
            if (hole == 2) addx = 0.75;

            if (hole2 == 0) addz = 0.2;
            if (hole2 == 1) addz = 0.5;
            if (hole2 == 2) addz = 0.75;
            if (canFire) {
                if (!state.get(CREATIVE)) {
                    setGunpowder(getGunpowder() - 1);
                    setPaper(getPaper() - 1);
                    world.notifyBlockUpdate(pos, state, state, 2);
                }
                Entity e = new FireworkRocketEntity(world, pos.getX() + addx, pos.getY(), pos.getZ() + addz, getFirework(state.get(FLIGHT), state.get(SHAPE), r.nextBoolean(), r.nextBoolean(), colors, fade));
                world.addEntity(e);
            } else {
                Entity e = new FireworkRocketEntity(world, pos.getX() + addx, pos.getY(), pos.getZ() + addz, getFirework(-99, state.get(SHAPE), r.nextBoolean(), r.nextBoolean(), colors, fade));
                world.addEntity(e);
                world.createExplosion(e, pos.getX(), pos.getY(), pos.getZ(), 2f, Explosion.Mode.BREAK);
                i = 0;
                return;
            }
            i = 0;
        }
        i++;
    }

    private boolean checkUp(World world, BlockPos pos) {
        for (int i = 1; i <= 50; i++) {
            if (!world.isAirBlock(new BlockPos(pos.getX(), pos.getY() + i, pos.getZ()))) return true;
        }
        return false;
    }

    private ItemStack getFirework(int flight, Shape shape, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT fw = new CompoundNBT();
        ListNBT explosion = new ListNBT();
        CompoundNBT l = new CompoundNBT();
        l.putInt("Flicker", flicker ? 1 : 0);
        l.putInt("Trail", trail ? 1 : 0);
        l.putInt("Type", (shape == Shape.RANDOM) ? new Random().nextInt(Shape.values().length - 2) + 1 : shape.getID() + 1);
        l.put("Colors", new IntArrayNBT(colors));
        l.put("FadeColors", new IntArrayNBT(fadeColors));
        explosion.add(l);
        fw.put("Explosions", explosion);
        fw.putInt("Flight", flight);
        nbt.put("Fireworks", fw);
        ItemStack i = new ItemStack(Items.FIREWORK_ROCKET);
        i.setTag(nbt);
        return i;
    }

    /**
     * Copied from TileEntityHopper and modified to work here
     */
    public List<ItemEntity> getCaptureItems() {
        return COLLECTION_AREA_SHAPE.toBoundingBoxList().stream().flatMap((p_200110_1_) -> this.getWorld().getEntitiesWithinAABB(ItemEntity.class, p_200110_1_.offset(this.getPos().getX() - 0.5D, this.getPos().getY() - 0.5D, this.getPos().getZ() - 0.5D), EntityPredicates.IS_ALIVE).stream()).collect(Collectors.toList());
    }

    public int getGunpowder() {
        return this.gunpowder;
    }

    public void setGunpowder(int gunpowder) {
        this.gunpowder = gunpowder;
        markDirty();
    }

    public int getPaper() {
        return this.paper;
    }

    public void setPaper(int paper) {
        this.paper = paper;
        markDirty();
    }

}
