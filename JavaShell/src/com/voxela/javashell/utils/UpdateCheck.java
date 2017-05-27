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
		
	public static void check() {
		
		new BukkitRunnable() {
			public void run() {
				
				Properties prop = new Properties();
				InputStream is = null;
				
				try {
					is = new ByteArrayInputStream(HttpUtil.requestHttp("https://raw.githubusercontent.com/Ladinn/JavaShell/master/JavaShell/plugin.yml").getBytes(StandardCharsets.UTF_8));
					prop.load(is);
				} catch (IOException e) {
					Bukkit.getServer().getLogger().info("Error checking for update.");
					e.printStackTrace();
					return;
				}
				
				double latest = Double.parseDouble(prop.getProperty("version"));
				double current = JavaShell.version;
						
				if (latest == current) Bukkit.getServer().getLogger().info("You have the latest version! v" + latest);
				
				if (latest > current) {
					
					double behind = (latest - current) * 10;
					String msg = HttpUtil.requestHttp("http://net.voxela.com/javashell/outdated.html");
					
					Bukkit.getServer().getLogger().warning(msg);
					Bukkit.getServer().getLogger().warning("You are " + (int) behind + " version(s) behind.");
				}
				
			}
		}.runTaskLater(JavaShell.getInstance(), 100L);
		
    }

}
