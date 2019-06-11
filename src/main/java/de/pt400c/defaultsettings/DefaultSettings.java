package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

@Mod(modid = DefaultSettings.MODID, acceptedMinecraftVersions = "[1.8,1.12.2]", name = DefaultSettings.NAME, version = DefaultSettings.VERSION, guiFactory = DefaultSettings.modGuiFactory, dependencies = "before:*", certificateFingerprint = "@FINGERPRINT@", clientSideOnly = true, updateJSON = "https://gist.githubusercontent.com/PT400C/be22046792a7859688f655f1a5f83975/raw/")
public class DefaultSettings {

	public static final String MODID = "defaultsettings";
	public static final String NAME = "DefaultSettings";
	public static final String VERSION = "@VERSION@";
	public static final String modGuiFactory = "de.pt400c.defaultsettings.GuiConfigFactory";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static Map<String, Integer> keyRebinds_18 = new HashMap<String, Integer>();
	public static String mcVersion = FMLInjectionData.data()[4].toString();
	public static Map<String, KeyContainer> keyRebinds_19 = new HashMap<String, KeyContainer>();
	private static final UpdateContainer updateContainer = new UpdateContainer();

	@Instance
	public static DefaultSettings instance;
	
	public DefaultSettings() {
		instance = this;
	}

	@EventHandler
	public static void construction(FMLConstructionEvent event) {
		try {
			FileUtil.restoreContents();
		} catch (Exception e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game:", e);
		}
	}
	
	@EventHandler
    public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(event.isDirectory())
			return;

		DefaultSettings.log.log(Level.ERROR, "The mod's files have been manipulated! The game will be terminated.");
		FMLCommonHandler.instance().exitJava(0, true);
    }

	@SuppressWarnings("deprecation")
	//This is necessary for the MC 1.8.0 edition
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		
		if(DefaultSettings.mcVersion.startsWith("1.8")) {
			ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings_18());
		}else {
			ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings_19());
		}
		
		MinecraftForge.EVENT_BUS.register(DefaultSettings.class);
		EventHandlers handlers = new EventHandlers();
		FMLCommonHandler.instance().bus().register(handlers);
		MinecraftForge.EVENT_BUS.register(handlers);
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		try {
			FileUtil.restoreKeys();
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		} catch (NullPointerException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		}
	}
	
	public static UpdateContainer getUpdater() {
		return updateContainer;
	}
	
	public static DefaultSettings getInstance() {
		return instance;
	}

}
