package de.erdbeerbaerlp.creativefirework;

import de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockUpdatePacket {
    private final BlockPos pos;
    private final int delay, flight, mode, type;

    public BlockUpdatePacket(BlockPos pos, int delay, int flight, int mode, MainClass.Shape type) {
        this.pos = pos;
        this.delay = delay;
        this.flight = flight;
        this.mode = mode;
        this.type = type.getID();
    }

    public static void encode(BlockUpdatePacket msg, PacketBuffer buf) {
        System.out.println("ENCODING");
        final CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("posX", msg.pos.getX());
        nbt.putInt("posY", msg.pos.getY());
        nbt.putInt("posZ", msg.pos.getZ());
        nbt.putInt("flight", msg.flight);
        nbt.putInt("delay", msg.delay);
        nbt.putInt("mode", msg.mode);
        nbt.putInt("type", msg.type);
        buf.writeCompoundTag(nbt);
    }

    public static BlockUpdatePacket decode(PacketBuffer buf) {
        System.out.println("DECODING");
        final CompoundNBT nbt = buf.readCompoundTag();
        final BlockPos bpos = new BlockPos(nbt.getInt("posX"), nbt.getInt("posY"), nbt.getInt("posZ"));
        return new BlockUpdatePacket(bpos, nbt.getInt("delay"), nbt.getInt("flight"), nbt.getInt("mode"), MainClass.Shape.getShape(nbt.getInt("type")));
    }

    public static class Handler {
        public static void handle(BlockUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                // Work that needs to be threadsafe (most work)
                ServerPlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
                // do stuff
                sender.world.setBlockState(msg.pos, sender.world.getBlockState(msg.pos).with(BlockFireworkShooter.DELAY, msg.delay).with(BlockFireworkShooter.FLIGHT, msg.flight).with(BlockFireworkShooter.MODE, BlockFireworkShooter.Mode.getMode(msg.mode)).with(BlockFireworkShooter.SHAPE, MainClass.Shape.getShape(msg.type)));
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
