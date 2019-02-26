package de.erdbeerbaerlp.creativefirework;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class UpdateCheck {
	private static boolean checkedUpdate = false;
	final static Logger LOGGER = Logger.getLogger(MainClass.MOD_ID);
	@SubscribeEvent
	public static void onTick(PlayerTickEvent event){
		if(checkedUpdate == false){
			final CheckResult result = VersionChecker.getResult(ModList.get().getModFileById(MainClass.MOD_ID).getMods().get(0));
			LOGGER.info(result.toString());
			if (result.status == Status.OUTDATED)
			{
				event.player.sendMessage(new TextComponentString("\u00A76[\u00A7cCreative Firework\u00A76]\u00A7c Update available!\n\u00A7cCurrent version: \u00A74"+MainClass.VERSION+"\u00A7c, Newest: \u00A7a"+result.target+"\n\u00A7cChangelog:\n\u00A76"+result.changes.get(result.target)).setStyle(new Style().setClickEvent(new ClickEvent(Action.OPEN_URL, "https://minecraft.curseforge.com/projects/discordrpc")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to get newer Version")))));
				LOGGER.log(Level.WARNING,"UpdateCheck: Update Available. Download it here: https://minecraft.curseforge.com/projects/discordrichpresence/files");
				checkedUpdate = true;
			}else if(result.status == Status.AHEAD){
				event.player.sendMessage(new TextComponentString("\u00A76[\u00A7cCreative Firework\u00A76]\u00A77 It looks like you are using an Development version... \n\u00A77Your version: \u00A76"+MainClass.VERSION).setStyle(new Style().setClickEvent(new ClickEvent(Action.OPEN_URL, "https://minecraft.curseforge.com/projects/discordrpc")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to get current stable Version")))));
				LOGGER.log(Level.WARNING,"UpdateCheck: Unreleased version... Assuming Development Version");
				checkedUpdate = true;
			}else if(result.status == Status.FAILED){
				event.player.sendMessage(new TextComponentString("\u00A76[\u00A7cCreative Firework\u00A76]\u00A7c FAILED TO CHECK FOR UPDATES"));
				LOGGER.log(Level.WARNING,"UpdateCheck: Unable to check for updates");
				checkedUpdate = true;
			}else if(result.status == Status.BETA){
				event.player.sendMessage(new TextComponentString("\u00A76[\u00A7cCreative Firework\u00A76]\u00A7a You are using an Beta Version. This may contain bugs which are being fixed."));
				LOGGER.log(Level.WARNING,"UpdateCheck: Beta");
				checkedUpdate = true;
			}else if(result.status == Status.BETA_OUTDATED){
				event.player.sendMessage(new TextComponentString("\u00A76[\u00A7cCreative Firework\u00A76]\u00A7c You are using an Outdated Beta Version. This may contain bugs which are being fixed or are already fixed\n\u00A76Changelog of newer Beta:"+result.changes.get(result.target)));
				LOGGER.log(Level.WARNING,"UpdateCheck: Bata_outdated");
				checkedUpdate = true;
			} else {
				LOGGER.log(Level.INFO,"UpdateCheck: "+result.toString());
				checkedUpdate = true;
			}
		}
	}
	@SubscribeEvent
	public static void onMenuOpened(GuiOpenEvent event){
		if(event.getGui() instanceof GuiMainMenu || event.getGui() instanceof GuiMultiplayer) checkedUpdate = false;
	}
}
