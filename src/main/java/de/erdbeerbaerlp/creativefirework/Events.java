package de.erdbeerbaerlp.creativefirework;

import de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntityType;
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
//		GameRegistry.registerTileEntity(TEFirework.class, MainClass.MOD_ID+":fireworkshooter");
	}
	@SubscribeEvent
	public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> ev) {
		MainClass.FWShooterTE = TileEntityType.register(MainClass.MOD_ID+":tefireworkshooter", TileEntityType.Builder.create(TEFirework::new)); 
	}
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		System.out.println("Registering items");
		event.getRegistry().register(new ItemBlock(MainClass.FireworkShooter, new Item.Properties()).setRegistryName(MainClass.MOD_ID, "fireworkshooter"));
	}
//	@SubscribeEvent
//	public void registerModels(ModelRegistryEvent event) {
//		for(Item item : Lists.ITEMS) {
//			if(item instanceof IHasModel) {
//				((IHasModel)item).registerModels();
//			}
//		}
//		
//		for(Block block : Lists.BLOCKS) {
//			if(block instanceof IHasModel) {
//				((IHasModel)block).registerModels();
//			}
//		}
//	}
	
}
