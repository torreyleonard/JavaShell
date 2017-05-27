package com.voxela.javashell;
import org.bukkit.plugin.java.JavaPlugin;

import com.voxela.javashell.utils.HttpUtil;
import com.voxela.javashell.utils.Metrics;
import com.voxela.javashell.utils.UpdateCheck;

import net.md_5.bungee.api.ChatColor;

public class JavaShell extends JavaPlugin {
	
	protected static JavaShell instance;
	protected static Metrics metrics;
	
	protected static String gamePrefix = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "JavaShell" + ChatColor.DARK_GRAY + "] ";
	
	public static double version;
	
	@Override
	public void onEnable() {
		
		instance = this;
		metrics = new Metrics(this);

		getVersion();
		
		this.getCommand("run").setExecutor(new RunCommand(this));
		
		UpdateCheck.check();
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
