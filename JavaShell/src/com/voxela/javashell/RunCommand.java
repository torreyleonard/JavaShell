package com.voxela.javashell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.voxela.javashell.utils.HttpUtil;

import net.md_5.bungee.api.ChatColor;

public class RunCommand implements CommandExecutor {
	
	JavaShell plugin;

	public RunCommand(JavaShell passedPlugin) {

		this.plugin = passedPlugin;

	}
	
	private static HashSet<String> importSet = new HashSet<String>();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
				

		if (cmd.getName().equalsIgnoreCase("run")) {
			
			if ( !(sender.isOp()) || !(sender.hasPermission(new Permission("voxela.javashell"))) ) {
				sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "Insufficient permissions.");
				return true;
			}
			
			if (args.length > 0) {
				
				if (args[0].startsWith("-")) {
					
					if (args[0].equalsIgnoreCase("-import")) {
						
						if (args.length > 1) {
							
							if (args[1].equalsIgnoreCase("add")) {
								if ( (args.length > 3) || (args.length == 2) ) {
									sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Usage: " + ChatColor.GRAY + "-import add [package]");
									return true;
								}
								String pkg = args[2];
								importSet.add(pkg);
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.GREEN + "Added " + ChatColor.GRAY + pkg + ChatColor.GREEN + " to session imports.");
								return true;
							}
							
							if (args[1].equalsIgnoreCase("remove")) {
								if ( (args.length > 3) || (args.length == 2) ) {
									sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Usage: " + ChatColor.GRAY + "-import remove [package]");
									return true;
								}
								String pkg = args[2];
								if (!(importSet.contains(pkg))) {
									sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "The package " + ChatColor.GRAY + pkg + ChatColor.RED + " has not been imported!");
									sender.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Type " + ChatColor.DARK_AQUA + "/run -import list" + ChatColor.GRAY + " to see current imports");
									return true;
								}
								importSet.remove(pkg);
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.GREEN + "Removed " + ChatColor.GRAY + pkg + ChatColor.GREEN + " from session imports.");
								return true;
							}
							
							if (args[1].equalsIgnoreCase("clear")) {
								if (args.length > 3) {
									sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Usage: " + ChatColor.GRAY + "-import clear");
									return true;
								}
								importSet.clear();
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.GREEN + "Cleared imports.");
								return true;
							}
							
							if (args[1].equalsIgnoreCase("list")) {
								if (args.length > 3) {
									sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Usage: " + ChatColor.GRAY + "-import list");
									return true;
								}
								String imports = "";
								
								for (String string : importSet) {
									imports += string + ",";
								}
								if (importSet.isEmpty()) {
									imports = "None!";
								}
								
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Imported packages: " + ChatColor.GRAY + imports);
								return true;
							}
							
						}
						
						sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Usage: " + ChatColor.GRAY + "-import [add/remove/clear/list]");
						return true;
					}
					
					if (args[0].equalsIgnoreCase("-script")) {
						
						File scriptDir = new File(JavaShell.getInstance().getDataFolder() + File.separator + "scripts");
						
						if (args.length == 2) {
							String script = args[1];
							File file = new File(scriptDir + File.separator + script + ".java");
							
							if (!file.exists()) {
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "Script not found!");
								return true;
							}
							
							String content = "";
							try {
								content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
							} catch (IOException e) {
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "IOException while reading script.");
								e.printStackTrace();
								return true;
							}
							
							try {
								Runner.run(content, importSet.toArray(new String[importSet.size()]));
							} catch (Exception e) {
								sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "Exception when running code. See console.");
								e.printStackTrace();
								return true;
							}
							sender.sendMessage(JavaShell.gamePrefix + ChatColor.GREEN + "Executed script.");
							return true;
						}
						
						if ( (args.length >= 3) || (args.length == 1) ) {
							sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "Usage: " + ChatColor.GRAY + "/run -script [script name]");
							return true;
						}
						
					}
					
					
				}
				
				String code = "";
				
	            for(int i = 0; i != args.length; i++) code += args[i] + " ";
	            
	            if (code.startsWith("http")) {
	            	code = HttpUtil.requestHttp(code);
	            }
				
				try {
					Runner.run(code, importSet.toArray(new String[importSet.size()]));
				} catch (Exception e) {
					sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "Exception when running code. See console.");
					e.printStackTrace();
					return true;
				}

				sender.sendMessage(JavaShell.gamePrefix + ChatColor.GREEN + "Executed.");
				return true;
			}
			
			sender.sendMessage(JavaShell.gamePrefix + ChatColor.DARK_AQUA + "JavaShell" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Version " + JavaShell.version);
			return true;
		}
		return false;
	}

}
