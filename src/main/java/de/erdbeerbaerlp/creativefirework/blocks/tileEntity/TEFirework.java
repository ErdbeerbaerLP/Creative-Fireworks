package de.erdbeerbaerlp.creativefirework.blocks.tileEntity;

import java.util.Random;

import javax.annotation.Nullable;

import de.erdbeerbaerlp.creativefirework.MainClass;
import de.erdbeerbaerlp.creativefirework.MainClass.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TEFirework extends TileEntity implements ITickable{

	
	private int flight,delay,mode,type;
	public TEFirework() {
		// TODO Auto-generated constructor stub
		super(MainClass.FWShooterTE);
	}
	@Override
	public NBTTagCompound write(NBTTagCompound compound) {
		compound.setInt("flight", flight);
		compound.setInt("delay", delay);
		compound.setInt("mode", mode);
		compound.setInt("type", type);
		return super.write(compound);
	}
	
	@Override
	public void read(NBTTagCompound compound) {
		flight = compound.getInt("flight");
		delay = compound.getInt("delay");
		mode = compound.getInt("mode");
		type = compound.getInt("type");
		super.read(compound);
	}
	
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		System.out.println("[DEBUG]:Server sent tile sync packet");
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		write(nbtTagCompound);
		int metadata = 0;
		return new SPacketUpdateTileEntity(this.pos, metadata, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		System.out.println("[DEBUG]:Client recived tile sync packet");
		read(pkt.getNbtCompound());
	}
	@Override
	public NBTTagCompound getUpdateTag()
	{
		System.out.println("GetUpdate");
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		write(nbtTagCompound);
		return nbtTagCompound;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag)
	{
		this.read(tag);
	}
	
	@Override
	public void markDirty() {
		// TODO Auto-generated method stub
//		System.out.println("DIRTY!!!!!!");
		super.markDirty();

	}
	public int getFlight() {
		return (flight == 0) ? 2:(flight >3)?3:flight;
	}
	public int getDelay() {
		return (delay == 0) ? 1:delay;
	}
	public void setDelay(int seconds) {
		this.delay = seconds;
		markDirty();
	}
	public void setFlight(int duration) {
		this.flight = duration;
		markDirty();
	}
	public int getMode() {
		return this.mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
		markDirty();
		
	}
	public int getFWType() {
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
		markDirty();
		
	}
	int i=0;
	Random r = new Random();
	@Override
	public void tick() {
		if(world.isRemote) {
			i=0;
			return;
		}
		switch(getMode()) {
		case 0:
			if(!world.isBlockPowered(pos)) {
				i=0;
				return;
			}
			break;
		case 1:
			if(world.isBlockPowered(pos)) {
				i=0;
				return;
			}
			break;
		case 2:
			i=0;
			return;
		case 3:
			break;
		case 4:
			if(checkUp() || !((world.getSkylightSubtracted()) > 5)) {
				i=0;
				return;
			}
			
			break;
			
		case 5:
//			System.out.println(world.isAirBlock(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ())));
			if(checkUp() || !((world.getSkylightSubtracted()) < 5)) {
				i=0;
				return;
			}
			break;
		default:
			return;
		}
		World worldIn = this.world;
		boolean canFire = world.isAirBlock(new BlockPos(pos.getX(), pos.getY()+1, pos.getZ()));
		
		// TODO Auto-generated method stub
		
		if(i > (getDelay()*20)) {
//			System.out.println("Fire!");
			final int[] colors = new int[r.nextInt(8)];
			for(int i = 0; i<colors.length;i++) {
				colors[i] = r.nextInt(99999999);
			}
			final int[] fade = new int[r.nextInt(8)];
			for(int i = 0; i<fade.length;i++) {
				fade[i] = r.nextInt(99999999);
			}
			int hole = r.nextInt(3);
			int hole2 = r.nextInt(3);
			double addx = 0;
			double addz = 0;
			if(hole == 0) addx = 0.2;
			if(hole == 1) addx = 0.5;
			if(hole == 2) addx = 0.75;
			
			if(hole2 == 0) addz = 0.2;
			if(hole2 == 1) addz = 0.5;
			if(hole2 == 2) addz = 0.75;
			if(canFire) {
				Entity e = new EntityFireworkRocket(worldIn, pos.getX()+addx, pos.getY(), pos.getZ()+addz, getFirework(getFlight(), (getFWType()==0) ? r.nextInt(5):(getFWType()-1), r.nextBoolean(), r.nextBoolean(), colors, fade));
				worldIn.spawnEntity(e);
			}else {
				Entity e = new EntityFireworkRocket(worldIn, pos.getX()+addx, pos.getY(), pos.getZ()+addz, getFirework(-99, (getFWType()==0) ? r.nextInt(5):(getFWType()-1), r.nextBoolean(), r.nextBoolean(), colors, fade));
				worldIn.spawnEntity(e);
//				world.destroyBlock(getPos(), false);
				world.createExplosion(e, pos.getX(), pos.getY(), pos.getZ(), 2f, true);
				i=0;
				return;
			}
			i=0;
		}
		i++;
	}
	private boolean checkUp() {
		for(int i=1;i<=50;i++) {
			if(!world.isAirBlock(new BlockPos(pos.getX(), pos.getY()+i, pos.getZ()))) return true;
		}
		return false;
	}
	private ItemStack getFirework(int flight, int shape, boolean trail, boolean flicker, int[] colors, int[] fadeColors) {
//		if(flight > 3) flight = 3;
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound fw = new NBTTagCompound();
		NBTTagList explosion = new NBTTagList();
		NBTTagCompound l = new NBTTagCompound();
		l.setInt("Flicker", flicker ? 1:0);
		l.setInt("Trail", trail ? 1:0);
		l.setInt("Type", shape);
		l.setTag("Colors", new NBTTagIntArray(colors));
		l.setTag("FadeColors", new NBTTagIntArray(fadeColors));
		explosion.add(l);
		fw.setTag("Explosions", explosion);
		fw.setInt("Flight", flight);
		nbt.setTag("Fireworks", fw);
		ItemStack i = new ItemStack(Items.FIREWORK_ROCKET);
		i.setTag(nbt);
		return i;
	}
}
