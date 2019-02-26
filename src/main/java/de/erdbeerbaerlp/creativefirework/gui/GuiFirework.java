package de.erdbeerbaerlp.creativefirework.gui;

import java.io.IOException;

import de.erdbeerbaerlp.creativefirework.MainClass;
import de.erdbeerbaerlp.creativefirework.blocks.tileEntity.TEFirework;
import de.erdbeerbaerlp.creativefirework.networking.UpdateTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;


public class GuiFirework extends GuiScreen{
	private TEFirework te;
	private GuiSlider durslider;
	private GuiSlider flighslider;
	private GuiSlider typeslider;
	private GuiButton btnEnable;
	boolean enabled = false;
	private int mode = 0;
	private int type;
	public GuiFirework(TEFirework t) {
		// TODO Auto-generated constructor stub
		this.te = t;
	}

	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void initGui() {
		// TODO Auto-generated method stub
		GuiButton xbtn;//GuiMainMenu
		this.addButton(xbtn = new GuiButtonExt(0, width/2-100, 190, I18n.format("gui.done")){
			/**
			 * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
			 */
			public void onClick(double mouseX, double mouseY) {
				GuiFirework.this.mc.displayGuiScreen(null);
			}
		});
		this.addButton(durslider = new GuiSlider(1, width/2-100, 45, 200, 20, I18n.format("gui.delay")+" ", " "+I18n.format("gui.seconds"), 1, 10, te.getDelay(), false, true));
		this.addButton(flighslider = new GuiSlider(2, width/2-100, 70, 200, 20, I18n.format("item.fireworks.flight")+" ", "", 1, 3, te.getFlight(), false, true));
		this.addButton(btnEnable = new GuiButtonExt(3, width/2-100, 160, I18n.format("gui.fwdisabled")) {

			public void onClick(double mouseX, double mouseY) {
				mode++;
				if(GuiFirework.this.mode >5 || GuiFirework.this.mode <0) GuiFirework.this.mode = 0;
			};

		});
		this.addButton(typeslider = new GuiSlider(4, width/2-100, 100, 200, 20, I18n.format("item.fireworksCharge.type")+" ", "", 0, 5, te.getFWType(), false, false));
		this.mode = te.getMode();
		this.type = te.getFWType();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
		this.type = (int) Math.round(typeslider.getValue());
		String modestring = I18n.format("gui.fwmode")+" ";
		if(this.mode > 5) modestring = modestring+I18n.format("gui.fwmodes");
		else modestring = modestring+I18n.format("gui.fwmodes."+this.mode);
		String typestring = I18n.format("gui.fwtype")+" ";
		switch(this.type){
		case 0:
			typestring = typestring+I18n.format("gui.fwtype0");
			break;
		default:
			typestring = typestring+I18n.format("item.fireworksCharge.type."+(this.type-1));
			break;
		}
		btnEnable.displayString = modestring;
		typeslider.displayString = typestring;
		drawBackground(1);
		drawCenteredString(fontRenderer, I18n.format("gui.fireworks.title"), this.width / 2, 15, 16777215);

		super.render(mouseX, mouseY, partialTicks);

	}


	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		int delay = (int) Math.round(durslider.getValue());
		int flight = (int) Math.round(flighslider.getValue());
		MainClass.TESHOOTERUPDATECHANNEL.sendToServer(new UpdateTE(te.getPos().getX()+";"+te.getPos().getY()+";"+te.getPos().getZ()+";"+te.getWorld().getDimension().getType().getId()+";delay;"+delay));
		MainClass.TESHOOTERUPDATECHANNEL.sendToServer(new UpdateTE(te.getPos().getX()+";"+te.getPos().getY()+";"+te.getPos().getZ()+";"+te.getWorld().getDimension().getType().getId()+";flight;"+flight));
		MainClass.TESHOOTERUPDATECHANNEL.sendToServer(new UpdateTE(te.getPos().getX()+";"+te.getPos().getY()+";"+te.getPos().getZ()+";"+te.getWorld().getDimension().getType().getId()+";mode;"+mode));
		MainClass.TESHOOTERUPDATECHANNEL.sendToServer(new UpdateTE(te.getPos().getX()+";"+te.getPos().getY()+";"+te.getPos().getZ()+";"+te.getWorld().getDimension().getType().getId()+";type;"+type));
		super.onGuiClosed();
	}

	@Override
	public void drawBackground(int tint) {
		// TODO Auto-generated method stub
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/blocks/brick.png"));
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0D, (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)tint)).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((double)((float)this.width / 32.0F), (double)tint).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)tint).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
	}
}
