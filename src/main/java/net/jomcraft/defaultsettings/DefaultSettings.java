package net.jomcraft.defaultsettings;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

@Mod(modid = DefaultSettings.MODID, acceptedMinecraftVersions = "[1.8,1.12.2]", name = DefaultSettings.NAME, version = DefaultSettings.VERSION, guiFactory = DefaultSettings.modGuiFactory, dependencies = "required-after:neptunefx@[1.0.0,)", certificateFingerprint = "@FINGERPRINT@", clientSideOnly = true, updateJSON = "https://gist.githubusercontent.com/PT400C/be22046792a7859688f655f1a5f83975/raw/")
public class DefaultSettings {

	//-Dfml.coreMods.load=net.jomcraft.defaultsettings.core.DefaultSettingsPlugin
	public static final String MODID = "defaultsettings";
	public static final String NAME = "DefaultSettings";
	public static final String VERSION = "DS-Version";
	public static final String modGuiFactory = "net.jomcraft.defaultsettings.GuiConfigFactory";
	public static final Logger log = LogManager.getLogger(DefaultSettings.MODID);
	public static Map<String, Integer> keyRebinds_18 = new HashMap<String, Integer>();
	public static String mcVersion = FMLInjectionData.data()[4].toString();
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
	public static Map<String, KeyContainer> keyRebinds_19 = new HashMap<String, KeyContainer>();
	private static final UpdateContainer updateContainer = new UpdateContainer();
	public static String BUILD_ID = "Unknown";
	public static String BUILD_TIME = "Unknown";
	public static final boolean is180 = DefaultSettings.mcVersion.equals("1.8");
	public static final boolean debug = false;
	public static int targetMS = 9;
	public static boolean compatibilityMode = false;
	
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
		(new Thread() {

			@Override
			public void run() {
				try {
					sendCount();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}).start();
	}
	
	@EventHandler
    public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		if(event.isDirectory() || FileUtil.isDev)
			return;

		DefaultSettings.log.log(Level.ERROR, "The mod's files have been manipulated! The game will be terminated.");
		FMLCommonHandler.instance().exitJava(0, true);
    }

	@SuppressWarnings("deprecation")
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		
		if(DefaultSettings.mcVersion.startsWith("1.8")) {
			ClientCommandHandler.instance.registerCommand(new CommandSwitchProfile_18());
			ClientCommandHandler.instance.registerCommand(new CommandDefaultSettings_18());
		}else {
			ClientCommandHandler.instance.registerCommand(new CommandSwitchProfile_19());
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
			getBuildID();
			getBuildTime();
		} catch(NullPointerException | IOException e) {
			
		}
	}
	
	@EventHandler
	public static void keysEvent(FMLLoadCompleteEvent event) {
		try {
			FileUtil.restoreKeys();
		} catch (IOException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		} catch (NullPointerException e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while starting up the game (Post):", e);
		}
	}
	
	private static void getBuildID() throws FileNotFoundException, IOException {
		ModContainer mc = FMLCommonHandler.instance().findContainerFor(DefaultSettings.getInstance());
		try (JarInputStream jarStream = new JarInputStream(new FileInputStream(mc.getSource()))) {
			BUILD_ID = jarStream.getManifest().getMainAttributes().getValue("Build-ID");
		}
	}
	
	private static void getBuildTime() throws FileNotFoundException, IOException {
		ModContainer mc = FMLCommonHandler.instance().findContainerFor(DefaultSettings.getInstance());
		try (JarInputStream jarStream = new JarInputStream(new FileInputStream(mc.getSource()))) {
			BUILD_TIME = jarStream.getManifest().getMainAttributes().getValue("Build-Date");
		}
	}

	public static UpdateContainer getUpdater() {
		return updateContainer;
	}
	
	public static DefaultSettings getInstance() {
		return instance;
	}

	public static void sendCount() throws Exception {
		String url = "https://apiv1.jomcraft.net/count";
		String jsonString = "{\"id\":\"Defaultsettings\", \"code\":" + RandomStringUtils.random(32, true, true) + "}"; 
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(jsonString);

		wr.flush();
		wr.close();
		con.getResponseCode();
		//if (resCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
		//	result = con.getInputStream();
		//} else {
		//	result = con.getErrorStream();
		//}
		/*
		BufferedReader in = new BufferedReader(new InputStreamReader(result));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		//String JSON = response.toString();
		in.close();*/
		con.disconnect();
		

	}
}