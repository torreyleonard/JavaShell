package com.voxela.javashell.utils;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

import com.voxela.javashell.JavaShell;

public class FileScanner {
	
	public static String paths = "";

	public static Integer jarScan() {

		int count = 0;
		
		File dataFolder = JavaShell.getInstance().getDataFolder();
		File pluginsFolder = dataFolder.getParentFile();
		File serverRoot = Bukkit.getWorldContainer();
				
		File[] rootJars = serverRoot.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("jar");
			}
		});
		
		File[] pluginJars = pluginsFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("jar");
			}
		});

		for (File file : (File[]) ArrayUtils.addAll(rootJars, pluginJars)) {
			String path = file.getAbsolutePath();
			paths += File.pathSeparator + path;
			count += 1;
		}
		return count;
	}

}
