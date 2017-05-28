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
		Bukkit.getServer().getLogger().info(prefix + "Loaded " + FileScanner.jarScan() + " plugins and libraries.");
		loadMsg();
		this.getCommand("run").setExecutor(new RunCommand(this));
		getVersion();
		UpdateCheck.check();
	}

	public static void loadFiles() {

		File runtime = new File(JavaShell.getInstance().getDataFolder() + File.separator + "runtime");
		if (!runtime.exists()) {
			System.out.print(prefix + "Creating runtime folder...");
			runtime.mkdirs();
		}

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

	public static void runFromURL(String requestURL) throws Exception {
		String code = HttpUtil.requestHttp(requestURL);
		Runner.run(code);
	}

	public static void runFromString(String code) throws Exception {
		Runner.run(code);
	}
}
