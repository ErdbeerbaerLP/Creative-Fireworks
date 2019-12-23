package de.erdbeerbaerlp.creativefirework;

import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TileEntityShooter;
import de.erdbeerbaerlp.creativefirework.itemGroups.GroupFirework;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Mod(MainClass.MOD_ID)
public class MainClass {


    public static final String MOD_ID = "creativefirework";
    public static final String VERSION = "2.0.1";
    public static final ItemGroup groupFirework = new GroupFirework("firework");
    @ObjectHolder(MOD_ID + ":fireworkshooter")
    public static final Block FireworkShooter = null;
    @ObjectHolder(MOD_ID + ":tefireworkshooter")
    public static final TileEntityType<TileEntityShooter> TE_FWSHOOTER = null;
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    private static final String protVersion = "1.0.0";
    private static final Predicate<String> pred = (ver) -> ver.equals(protVersion);
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MainClass.MOD_ID, "te-fwshooter-channel"), () -> protVersion, pred, pred);

    public MainClass() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent evt) {
        System.out.println("common");
        CHANNEL.registerMessage(0, BlockUpdatePacket.class, BlockUpdatePacket::encode, BlockUpdatePacket::decode, BlockUpdatePacket.Handler::handle);
    }

    public void serverStarting(FMLServerStartingEvent evt) {
    }


    public enum Shape implements IStringSerializable {
        RANDOM(0, "random"),
        SMALL_BALL(1, "small_ball"),
        LARGE_BALL(2, "large_ball"),
        STAR(3, "star"),
        CREEPER(4, "creeper"),
        BURST(5, "burst");

        private static final Shape[] shapes = Arrays.stream(values()).sorted(Comparator.comparingInt((shape) -> shape.ID)).toArray(Shape[]::new);
        private final int ID;
        private final String name;

        Shape(int ID, String name) {
            this.ID = ID;
            this.name = name;
        }

        @OnlyIn(Dist.CLIENT)
        public static Shape getShape(int index) {
            return index >= 0 && index < shapes.length ? shapes[index] : SMALL_BALL;
        }

        public int getID() {
            return this.ID;
        }

        @OnlyIn(Dist.CLIENT)
        public String getName() {
            return this.name;
        }
    }


}

