package de.erdbeerbaerlp.creativefirework;

import com.google.common.base.Predicate;

import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import de.erdbeerbaerlp.creativefirework.creativeTabs.tabFirework;
import de.erdbeerbaerlp.creativefirework.networking.UpdateTE;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;
@Mod(MainClass.MOD_ID)
public class MainClass {


	public static final String MOD_ID = "creativefirework";
	public static final String VERSION = "2.0.1";
	public static final ItemGroup tabFirework = new tabFirework("firework");
	private static final String protVersion = "1.0.0";
	private static final Predicate<String> pred = (ver) -> {return ver.equals(protVersion);};
	public static final SimpleChannel TESHOOTERUPDATECHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MainClass.MOD_ID, "te-fwshooter-channel"), ()->{return protVersion;}, pred, pred);
	
	@ObjectHolder(MOD_ID + ":fireworkshooter")
	public static final Block FireworkShooter = null;
	
	public MainClass() {
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.addListener(this::postInit);
		MinecraftForge.EVENT_BUS.register(UpdateCheck.class);
//		MinecraftForge.EVENT_BUS.register(Events.class);
		TESHOOTERUPDATECHANNEL.<UpdateTE>registerMessage(0, UpdateTE.class, (a, b) -> a.encode(a,b), (a) -> {a.readInt();return new UpdateTE(a.readString(300));}, (a, b) -> a.onMessageReceived(a,b));
		
	}
	public void commonSetup(FMLCommonSetupEvent evt) {
	}
	public void postInit(InterModProcessEvent evt) {
	}
	public void serverStarting(FMLServerStartingEvent evt)
	{
	}

	public static TileEntityType<TEFirework> FWShooterTE;
}
