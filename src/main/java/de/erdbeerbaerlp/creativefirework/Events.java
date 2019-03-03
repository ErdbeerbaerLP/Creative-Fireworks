package de.erdbeerbaerlp.creativefirework;

import de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import de.erdbeerbaerlp.creativefirework.entity.EntityCustomRocket;
import de.erdbeerbaerlp.creativefirework.items.ItemCustomFireworkStar;
import de.erdbeerbaerlp.creativefirework.items.ItemCustomRocket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
@EventBusSubscriber(bus=Bus.MOD)
public class Events {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		System.out.println("Registering blocks");
		
		event.getRegistry().register(new BlockFireworkShooter(Block.Properties.create(Material.GROUND).hardnessAndResistance(0.5f, 0f)));
	
	}
	@SubscribeEvent
	public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> ev) {
		MainClass.FWShooterTE = TileEntityType.register(MainClass.MOD_ID+":tefireworkshooter", TileEntityType.Builder.create(TEFirework::new)); 
	}
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		System.out.println("Registering items"); //ItemDebugStick
		event.getRegistry().register(new ItemBlock(MainClass.FireworkShooter, new Item.Properties()).setRegistryName(MainClass.MOD_ID, "fireworkshooter"));
		event.getRegistry().register(new ItemCustomRocket(new Item.Properties().group(MainClass.groupFirework)).setRegistryName(new ResourceLocation("minecraft", "firework_rocket")));
		event.getRegistry().register(new ItemCustomFireworkStar(new Item.Properties().group(MainClass.groupFirework)).setRegistryName(new ResourceLocation("minecraft", "firework_star")));
	}
	@SubscribeEvent
	public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
		MainClass.CUSTOM_ROCKET_TYPE = EntityType.register(EntityType.FIREWORK_ROCKET.getRegistryName().toString(), EntityType.Builder.create(EntityCustomRocket.class, EntityCustomRocket::new));
	
	}
	

}
