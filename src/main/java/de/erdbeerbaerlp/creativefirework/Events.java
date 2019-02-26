package de.erdbeerbaerlp.creativefirework;

import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import de.erdbeerbaerlp.creativefirework.util.IHasModel;
import de.erdbeerbaerlp.creativefirework.util.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		System.out.println("Registering blocks");
		event.getRegistry().registerAll(Lists.BLOCKS.toArray(new Block[0]));
//		GameRegistry.registerTileEntity(TEFirework.class, MainClass.MOD_ID+":fireworkshooter");
	}
	public void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> ev) {
		MainClass.FWShooterTE = TileEntityType.register("FireworkShooter", TileEntityType.Builder.create(TEFirework::new)); 
	}
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		System.out.println("Registering items");
		event.getRegistry().registerAll(Lists.ITEMS.toArray(new Item[0]));
	}
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		for(Item item : Lists.ITEMS) {
			if(item instanceof IHasModel) {
				((IHasModel)item).registerModels();
			}
		}
		
		for(Block block : Lists.BLOCKS) {
			if(block instanceof IHasModel) {
				((IHasModel)block).registerModels();
			}
		}
	}
	
}
