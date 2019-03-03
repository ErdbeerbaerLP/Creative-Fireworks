package de.erdbeerbaerlp.creativefirework;

import java.util.logging.Logger;

import com.google.common.base.Predicate;

import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import de.erdbeerbaerlp.creativefirework.entity.EntityCustomRocket;
import de.erdbeerbaerlp.creativefirework.itemGroups.GroupFirework;
import de.erdbeerbaerlp.creativefirework.items.ItemCustomRocket;
import de.erdbeerbaerlp.creativefirework.networking.UpdateTE;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
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
	public static final ItemGroup groupFirework = new GroupFirework("firework");
	private static final String protVersion = "1.0.0";
	private static final Predicate<String> pred = (ver) -> {return ver.equals(protVersion);};
	public static final SimpleChannel TESHOOTERUPDATECHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MainClass.MOD_ID, "te-fwshooter-channel"), ()->{return protVersion;}, pred, pred);
	
	@ObjectHolder(MOD_ID + ":fireworkshooter")
	public static final Block FireworkShooter = null;
	//BlockChest
	@ObjectHolder("minecraft:firework_rocket")
	public static final ItemCustomRocket CustomRocket = null;
	public static final Logger LOGGER = Logger.getLogger(MOD_ID);
	
	public MainClass() {
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.register(UpdateCheck.class);
		TESHOOTERUPDATECHANNEL.<UpdateTE>registerMessage(0, UpdateTE.class, (a, b) -> a.encode(a,b), (a) -> {a.readInt();return new UpdateTE(a.readString(300));}, (a, b) -> a.onMessageReceived(a,b));
		
	}
	public void commonSetup(FMLCommonSetupEvent evt) {
	}
	public void serverStarting(FMLServerStartingEvent evt)
	{
	}

	public static TileEntityType<TEFirework> FWShooterTE;
	public static EntityType<EntityCustomRocket> CUSTOM_ROCKET_TYPE;





}

