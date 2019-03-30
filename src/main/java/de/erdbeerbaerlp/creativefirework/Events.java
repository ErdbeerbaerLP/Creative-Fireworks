package de.erdbeerbaerlp.creativefirework;

import de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TileEntityShooter;
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
	}
	@SubscribeEvent
	public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> ev) {
		MainClass.TE_FWSHOOTER = TileEntityType.register(MainClass.MOD_ID+":tefireworkshooter", TileEntityType.Builder.create(TileEntityShooter::new)); 
	}
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		System.out.println("Registering items");
		event.getRegistry().register(new ItemBlock(MainClass.FireworkShooter, new Item.Properties()).setRegistryName(MainClass.MOD_ID, "fireworkshooter"));
	}
	

}
