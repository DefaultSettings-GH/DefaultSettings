package de.pt400c.defaultsettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.FMLClientHandler;

public class FileUtil {
	
	public static final Minecraft MC = Minecraft.getMinecraft();
	public static final File mcDataDir = MC.mcDataDir;
	public static final boolean isDev = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"); 
	
	public static File getMainFolder() {
		final File storeFolder = new File(mcDataDir, "config/defaultsettings");
		storeFolder.mkdir();
		return storeFolder;
	}

	public static void restoreContents() throws NullPointerException, IOException {
		
		final File options = new File(mcDataDir, "options.txt");
		boolean firstBoot = !options.exists();
		if (firstBoot) {
			restoreOptions();
			restoreConfigs();
		}
		final File optionsOF = new File(mcDataDir, "optionsof.txt");
		if (!optionsOF.exists()) 
			restoreOptionsOF();
		
		final File serversFile = new File(mcDataDir, "servers.dat");
		if (!serversFile.exists()) 
			restoreServers();
		
	}
	
	public static boolean optionsFilesExist() {
		final File optionsFile = new File(getMainFolder(), "options.txt");
		final File optionsofFile = new File(getMainFolder(), "optionsof.txt");
		return optionsFile.exists() || optionsofFile.exists();
	}
	
	public static boolean keysFileExist() {
		final File keysFile = new File(getMainFolder(), "keys.txt");
		return keysFile.exists();
	}
	
	public static void restoreOptions() throws NullPointerException, IOException {
		final File optionsFile = new File(getMainFolder(), "options.txt");
		if (optionsFile.exists()) {
			BufferedReader reader = null;
			PrintWriter writer = null;
			try {
				reader = new BufferedReader(new FileReader(optionsFile));	
				writer = new PrintWriter(new FileWriter(new File(mcDataDir, "options.txt")));
				String line;
				while ((line = reader.readLine()) != null) {
					writer.print(line + "\n");
				}
			} catch (IOException e) {
				throw e;
			} finally {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					throw e;
				} catch (NullPointerException e) {
					throw e;
				}
			}
		}
	}
	
	public static void restoreKeys() throws NullPointerException, IOException, NumberFormatException {
		if(DefaultSettings.mcVersion.startsWith("1.8"))
			DefaultSettings.keyRebinds_18.clear();
		else
			DefaultSettings.keyRebinds_19.clear();
		
		final File keysFile = new File(getMainFolder(), "keys.txt");
		if (keysFile.exists()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(keysFile));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					if(DefaultSettings.mcVersion.startsWith("1.8"))
						DefaultSettings.keyRebinds_18.put(line.split(":")[0], Integer.parseInt(line.split(":")[1]));
					else
						DefaultSettings.keyRebinds_19.put(line.split(":")[0], new KeyContainer(Integer.parseInt(line.split(":")[1]), KeyModifier.valueFromString(line.split(":")[2])));
				}
			} catch (IOException e) {
				throw e;
				
			} catch (NullPointerException e) {
				throw e;
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					throw e;
				} catch (NullPointerException e) {
					throw e;
				}
			}

			for (KeyBinding keyBinding : MC.gameSettings.keyBindings) {
				if(DefaultSettings.mcVersion.startsWith("1.8")) {
					if (DefaultSettings.keyRebinds_18.containsKey(keyBinding.getKeyDescription())) {
						keyBinding.keyCodeDefault = DefaultSettings.keyRebinds_18.get(keyBinding.getKeyDescription());
					}
				}else {
					
					if (DefaultSettings.keyRebinds_19.containsKey(keyBinding.getKeyDescription())) {
						KeyContainer container = DefaultSettings.keyRebinds_19.get(keyBinding.getKeyDescription());
						setField("keyModifierDefault", KeyBinding.class, keyBinding, container.modifier);
						keyBinding.keyCodeDefault = container.input;
						keyBinding.setKeyModifierAndCode(keyBinding.getKeyModifierDefault(), keyBinding.keyCodeDefault);
					}
				}
			}
			
			KeyBinding.resetKeyBindingArrayAndHash();
		}
	}
	
	public static void restoreOptionsOF() throws IOException {
		final File optionsOFFile = new File(getMainFolder(), "optionsof.txt");
		if (optionsOFFile.exists()) {
			BufferedReader reader = null;
			PrintWriter writer = null;
			try {
				reader = new BufferedReader(new FileReader(optionsOFFile));
				writer = new PrintWriter(new FileWriter(new File(mcDataDir, "optionsof.txt")));
				String line;
				while ((line = reader.readLine()) != null) {
					writer.print(line + "\n");
				}
			} catch (IOException e) {
				throw e;
			} catch (NullPointerException e) {
				throw e;
			} finally {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					throw e;
				} catch (NullPointerException e) {
					throw e;
				}
			}
		}
	}
	
	public static void restoreConfigs() throws IOException {
		try {
			FileFilter fileFilter = new FileFilter() {
				@Override
				public boolean accept(File file) {

					if (!file.getName().equals("defaultsettings") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat"))
						return true;

					return false;
				}
			};
			FileUtils.copyDirectory(getMainFolder(), new File(mcDataDir, "config"), fileFilter);
		} catch (IOException e) {
			throw e;
		}
	}

	public static void restoreServers() throws IOException {
		try {
			FileUtils.copyFile(new File(getMainFolder(), "servers.dat"), new File(mcDataDir, "servers.dat"));
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static void saveKeys() throws IOException, NullPointerException {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "keys.txt")));
			for (KeyBinding keyBinding : MC.gameSettings.keyBindings) {
				if(DefaultSettings.mcVersion.startsWith("1.8"))
					writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode() + "\n");
				else
					writer.print(keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode() + ":" + keyBinding.getKeyModifier().name() + "\n");
			}
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			writer.close();
		}
	}

	public static void saveOptions() throws NullPointerException, IOException {
		MC.gameSettings.saveOptions();
		PrintWriter writer = null;
		BufferedReader reader = null;
		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "options.txt")));
			reader = new BufferedReader(new FileReader(new File(mcDataDir, "options.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("key_")) {
					continue;
				}
				writer.print(line + "\n");
			}
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				throw e;
			} catch (NullPointerException e) {
				throw e;
			}
		}

		if (!FMLClientHandler.instance().hasOptifine()) {
			return;
		}

		try {
			writer = new PrintWriter(new FileWriter(new File(getMainFolder(), "optionsof.txt")));
			reader = new BufferedReader(new FileReader(new File(mcDataDir, "optionsof.txt")));
			String line;
			while ((line = reader.readLine()) != null) {
				writer.print(line + "\n");
			}
		} catch (IOException e) {
			throw e;
		} catch (NullPointerException e) {
			throw e;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
				throw e;
			} catch (NullPointerException e) {
				throw e;
			}
		}	
	}

	public static void saveServers() throws IOException {
		final File serversFile = new File(mcDataDir, "servers.dat");
		if (serversFile.exists()) {
			try {
				FileUtils.copyFile(serversFile, new File(getMainFolder(), "servers.dat"));
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	public static boolean serversFileExists() {
		final File serversFile = new File(getMainFolder(), "servers.dat");
		return serversFile.exists();
	}
	
	@SuppressWarnings("rawtypes")
	private static void setField(String name, Class clazz, Object obj, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		} catch (NoSuchFieldException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.ERROR, "Reflection exception: ", e);
		}
	}

}