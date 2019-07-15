package de.erdbeerbaerlp.creativefirework.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.erdbeerbaerlp.creativefirework.BlockUpdatePacket;
import de.erdbeerbaerlp.creativefirework.MainClass;
import de.erdbeerbaerlp.creativefirework.MainClass.Shape;
import de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TileEntityShooter;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;

import static de.erdbeerbaerlp.creativefirework.blocks.BlockFireworkShooter.CREATIVE;


public class GuiFirework extends Screen {
    boolean enabled = false;
    private BlockPos pos;
    private BlockState blockState;
    private GuiSlider durslider;
    private GuiSlider flighslider;
    private GuiSlider typeslider;
    private Button btnEnable;
    private int mode = 0;
    private Shape type;
    private World world;

    public GuiFirework(BlockPos pos, World world) {
        super(new StringTextComponent(""));
        this.pos = pos;
        this.world = world;
        this.blockState = world.getBlockState(pos);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        this.addButton(new GuiButtonExt(width / 2 - 100, 190, 200, 20, I18n.format("gui.done"), null) {
            /**
             * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
             */
            public void onClick(double mouseX, double mouseY) {
                GuiFirework.this.minecraft.displayGuiScreen(null);
            }
        });
        this.addButton(durslider = new GuiSlider(width / 2 - 100, 45, 200, 20, I18n.format("gui.delay") + " ", " " + I18n.format("gui.seconds"), 1, 10, blockState.get(BlockFireworkShooter.DELAY), false, true, null));
        this.addButton(flighslider = new GuiSlider(width / 2 - 100, 70, 200, 20, I18n.format("item.minecraft.firework_rocket.flight") + " ", "", 1, 3, blockState.get(BlockFireworkShooter.FLIGHT), false, true, null));
        this.addButton(btnEnable = new GuiButtonExt(width / 2 - 100, 160, 200, 20, I18n.format("gui.fwdisabled"), null) {

            public void onClick(double mouseX, double mouseY) {
                mode++;
                if (GuiFirework.this.mode > 5 || GuiFirework.this.mode < 0) GuiFirework.this.mode = 0;
            }

        });
        this.addButton(typeslider = new GuiSlider(width / 2 - 100, 100, 200, 20, I18n.format("item.fireworksCharge.type") + " ", "", 0, Shape.values().length - 1, blockState.get(BlockFireworkShooter.SHAPE).getID(), false, false, null));
        this.mode = blockState.get(BlockFireworkShooter.MODE).getID();
        this.type = blockState.get(BlockFireworkShooter.SHAPE);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.type = Shape.getShape((int) Math.round(typeslider.getValue()));
        String modestring = I18n.format("gui.fwmode") + " ";
        if (this.mode > 5) modestring = modestring + I18n.format("gui.fwmodes");
        else modestring = modestring + I18n.format("gui.fwmodes." + this.mode);
        String typestring = I18n.format("gui.fwtype") + " ";
        typestring = typestring + I18n.format("item.minecraft.firework_star.shape" + getTypeString(this.type));
        btnEnable.setMessage(modestring);
        typeslider.setMessage(typestring);
        renderBackground(1);
        drawCenteredString(font, I18n.format("gui.fireworks.title"), this.width / 2, 15, 16777215);
        if (!world.getBlockState(pos).get(CREATIVE)) {
            drawCenteredString(font, I18n.format("item.minecraft.paper") + ": " + ((TileEntityShooter) world.getTileEntity(pos)).getPaper(), this.width / 2, 125, 16777215);
            drawCenteredString(font, I18n.format("item.minecraft.gunpowder") + ": " + ((TileEntityShooter) world.getTileEntity(pos)).getGunpowder(), this.width / 2, 135, 16777215);
        }
        super.render(mouseX, mouseY, partialTicks);
    }


    private String getTypeString(Shape type) {
        switch (type) {
            case SMALL_BALL:
            case LARGE_BALL:
            case STAR:
            case CREEPER:
            case BURST:
            case RANDOM:
                return "." + type.getName();
            default:
                return "";
        }
    }

    @Override
    public void onClose() {
        int delay = (int) Math.round(durslider.getValue());
        int flight = (int) Math.round(flighslider.getValue()); //WorldClient
        MainClass.CHANNEL.sendToServer(new BlockUpdatePacket(pos, delay, flight, this.mode, this.type));
        super.onClose();
    }

    @Override
    public void renderBackground(int tint) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/block/bricks.png"));
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, (double) this.height, 0.0D).tex(0.0D, (double) ((float) this.height / 32.0F + (float) tint)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double) this.width, (double) this.height, 0.0D).tex((double) ((float) this.width / 32.0F), (double) ((float) this.height / 32.0F + (float) tint)).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos((double) this.width, 0.0D, 0.0D).tex((double) ((float) this.width / 32.0F), (double) tint).color(64, 64, 64, 255).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double) tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }
}
