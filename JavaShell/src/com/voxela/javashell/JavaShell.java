package com.voxela.javashell;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxela.javashell.utils.HttpUtil;
import com.voxela.javashell.utils.Metrics;
import com.voxela.javashell.utils.UpdateCheck;

import net.md_5.bungee.api.ChatColor;

public class JavaShell extends JavaPlugin {
	
	protected static JavaShell instance;
	protected static Metrics metrics;
	
	private static String prefix = "[JavaShell] ";
	protected static String gamePrefix = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "JavaShell" + ChatColor.DARK_GRAY + "] ";
	
	public static double version;
	
	private static final String red = "\u001B[31m";
	private static final String green = "\u001B[32m";
	private static final String reset = "\u001B[0m";
	
	@Override
	public void onEnable() {
		
		instance = this;
		metrics = new Metrics(this);
		
		checkJDK();
		loadMsg();

		getVersion();
		
		this.getCommand("run").setExecutor(new RunCommand(this));
		
		UpdateCheck.check();
	}
	
	private static void checkJDK() {
		
		String version = System.getProperty("java.version");
		Bukkit.getServer().getLogger().info(prefix + "Running Java version " + version + ".");
		
		String prop = System.getProperty("java.library.path");
		if (prop == null) {
			Bukkit.getServer().getLogger().severe(prefix + red + "Cannot find Java Development Kit!" + reset);
			Bukkit.getServer().getLogger().severe(prefix + red + "Install Java JDK on Linux with 'apt-get install default-jdk'" + reset);
			Bukkit.getServer().getLogger().severe(prefix + red + "Disabling JavaShell!" + reset);
			Bukkit.getPluginManager().disablePlugin(instance);
		} else {
			Bukkit.getServer().getLogger().info(prefix+ "Found Java Development Kit!");
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
		Runner.runCode(code);
	}
	
	public static void runFromString(String code) throws Exception {
		Runner.runCode(code);
	}
}
