package com.voxela.javashell;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxela.javashell.utils.FileScanner;
import com.voxela.javashell.utils.HttpUtil;
import com.voxela.javashell.utils.JarUtils;
import com.voxela.javashell.utils.Metrics;
import com.voxela.javashell.utils.UpdateCheck;

import net.md_5.bungee.api.ChatColor;

public class JavaShell extends JavaPlugin {

	protected static JavaShell instance;
	protected static Metrics metrics;

	private static String prefix = "[JavaShell] ";
	protected static String gamePrefix = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "JavaShell" + ChatColor.DARK_GRAY
			+ "] ";

	public static double version;

	private static final String green = "\u001B[32m";
	private static final String reset = "\u001B[0m";

	@Override
	public void onEnable() {
		instance = this;
		metrics = new Metrics(this);
		String version = System.getProperty("java.version");
		Bukkit.getServer().getLogger().info(prefix + "Running Java version " + version + ".");
		loadFiles();
		loadDependencies();
		int scriptsLoaded = loadScripts();
		if (scriptsLoaded > 0) {
			Bukkit.getServer().getLogger().info(prefix + "Downloaded " + scriptsLoaded + " script(s).");
		}
		Bukkit.getServer().getLogger().info(prefix + "Loaded " + FileScanner.jarScan() + " plugins and libraries.");
		loadMsg();
		this.getCommand("run").setExecutor(new RunCommand(this));
		getVersion();
		UpdateCheck.check();
	}

	public static void loadFiles() {

		File runtime = new File(JavaShell.getInstance().getDataFolder() + File.separator + "runtime");
		File scriptDir = new File(JavaShell.getInstance().getDataFolder() + File.separator + "scripts");
		
		if (!runtime.exists()) runtime.mkdirs();
		if (!scriptDir.exists()) scriptDir.mkdirs();
	}

	public static void loadDependencies() {

		try {
			final File[] libs = new File[] {
					new File(JavaShell.getInstance().getDataFolder() + File.separator + "lib", "tools.jar") };
			for (final File lib : libs) {
				if (!lib.exists()) {
					JarUtils.extractFromJar(lib.getName(), lib.getAbsolutePath());
				}
			}
			for (final File lib : libs) {
				if (!lib.exists()) {
					Bukkit.getServer().getLogger()
							.warning(prefix + "Critical error! Could not find lib: " + lib.getName());
					Bukkit.getServer().getPluginManager().disablePlugin(JavaShell.instance);
					return;
				}
				JarUtils.addClassPath(JarUtils.getJarUrl(lib));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private static void loadMsg() {
		String msg = HttpUtil.requestHttp("http://net.voxela.com/javashell/msg.html");
		Bukkit.getServer().getLogger().info(prefix + green + msg + reset);
	}

	public static JavaShell getInstance() {
		return instance;
	}

	private void getVersion() {
		String currentString = getDescription().getVersion();
		double current = Double.parseDouble(currentString.replace("version: ", ""));
		version = current;
	}

	protected static Metrics getMetrics() {
		return metrics;
	}

	// API

	public static void runFromURL(String requestURL, String[] imports) throws Exception {
		String code = HttpUtil.requestHttp(requestURL);
		Runner.run(code, imports);
	}

	public static void runFromString(String code, String[] imports) throws Exception {
		Runner.run(code, imports);
	}
	
	// Load scripts
	
	public static int loadScripts() {
		
		int count = 0;
		
		File scriptDir = new File(JavaShell.getInstance().getDataFolder() + File.separator + "scripts");
		
		String string = HttpUtil.requestHttp("https://raw.githubusercontent.com/Ladinn/JavaShell/master/Scripts/scripts.yml");
		string = string.replace("\n", "").replace("\r", "");
		String[] parts = string.split(":");
		
		for (String script : parts) {
			File scriptFile = new File(scriptDir + File.separator + script + ".java");
			if (scriptFile.exists()) continue;
			HttpUtil.downloadFile("https://raw.githubusercontent.com/Ladinn/JavaShell/master/Scripts/" + script + ".java", scriptFile);
			count += 1;
		}
		return count;
	}
}
