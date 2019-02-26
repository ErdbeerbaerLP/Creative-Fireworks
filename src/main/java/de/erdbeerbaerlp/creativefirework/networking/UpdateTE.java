package de.erdbeerbaerlp.creativefirework.networking;

import java.util.function.Supplier;

import de.erdbeerbaerlp.creativefirework.MainClass;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class UpdateTE {

	private String toSend;
	public UpdateTE(String toSend) {
		// TODO Auto-generated constructor stub
		this.toSend = toSend;
	}



	public Object encode(UpdateTE a, PacketBuffer b) {
		// TODO Auto-generated method stub
		b.writeInt(0);
		b.writeString(toSend);
		return b;
	}

	public Object onMessageReceived(UpdateTE a, Supplier<Context> b) {
		String[] values = a.toSend.split(";");
		System.out.println(values.length);
		if(values.length != 6) {
			System.out.println("Invalid or malformed packet data!");
			return null;
		} 
		try {
			WorldServer w = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(Integer.parseInt(values[3])));
			BlockPos pos = new BlockPos(Integer.parseInt(values[0]),Integer.parseInt(values[1]),Integer.parseInt(values[2]));
			TEFirework te = (TEFirework) w.getTileEntity(pos);
			if(te == null) {
				System.out.println("Invalid Tile Entity");
				return null;
			}
			switch(values[4]) {
			case "delay":
					    	System.out.println(values[5]);
				te.setDelay(Integer.parseInt(values[5]));
				w.notifyBlockUpdate(pos, MainClass.FireworkShooter.getDefaultState(), MainClass.FireworkShooter.getDefaultState(), 1);
				break;
			case "mode":
					    	System.out.println(values[5]);
				te.setMode(Integer.parseInt(values[5]));
				w.notifyBlockUpdate(pos, MainClass.FireworkShooter.getDefaultState(), MainClass.FireworkShooter.getDefaultState(), 1);
				break;
			case "type":
					    	System.out.println(values[5]);
				te.setType(Integer.parseInt(values[5]));
				w.notifyBlockUpdate(pos, MainClass.FireworkShooter.getDefaultState(), MainClass.FireworkShooter.getDefaultState(), 1);
				break;
			case "flight":
					    	System.out.println(values[5]);
				te.setFlight(Integer.parseInt(values[5]));
				w.notifyBlockUpdate(pos, MainClass.FireworkShooter.getDefaultState(), MainClass.FireworkShooter.getDefaultState(), 1);
				break;


			}
		}catch (NumberFormatException e) {
			System.out.println("Malformed packet data! java.lang.NumberFormatException");
			e.printStackTrace();
		}
		return null;

	}


}
