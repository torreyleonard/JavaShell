package com.voxela.javashell.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.voxela.javashell.JavaShell;

public class UpdateCheck {
		
	private static final String prefix = "[JavaShell] ";
	private static final String red = "\u001B[31m";
	private static final String reset = "\u001B[0m";
	
	public static void check() {
		
		new BukkitRunnable() {
			public void run() {
				
				Properties prop = new Properties();
				InputStream is = null;
				
				try {
					is = new ByteArrayInputStream(HttpUtil.requestHttp("https://raw.githubusercontent.com/Ladinn/JavaShell/master/JavaShell/plugin.yml").getBytes(StandardCharsets.UTF_8));
					prop.load(is);
				} catch (IOException e) {
					Bukkit.getServer().getLogger().info(prefix + "Error checking for update.");
					e.printStackTrace();
					return;
				}
				
				double latest = Double.parseDouble(prop.getProperty("version"));
				double current = JavaShell.version;
						
				if (latest == current) Bukkit.getServer().getLogger().info(prefix + "You have the latest version! v" + latest);
				
				if (latest > current) {
					
					double behind = (latest - current) * 10;
					String msg = HttpUtil.requestHttp("http://net.voxela.com/javashell/outdated.html");
					
					Bukkit.getServer().getLogger().warning(red + msg + reset);
					Bukkit.getServer().getLogger().warning(prefix + red + "You are " + (int) behind + " version(s) behind." + reset);
				}
				
			}
		}.runTaskLater(JavaShell.getInstance(), 200L);
		
    }

}
