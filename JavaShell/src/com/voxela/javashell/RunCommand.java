package com.voxela.javashell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.voxela.javashell.utils.HttpUtil;

import net.md_5.bungee.api.ChatColor;

public class RunCommand implements CommandExecutor {
	
	JavaShell plugin;

	public RunCommand(JavaShell passedPlugin) {

		this.plugin = passedPlugin;

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
				

		if (cmd.getName().equalsIgnoreCase("run")) {
			
			if (!(sender.isOp())) {
				sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "Insufficient permissions.");
				return true;
			}
			
			if (args.length > 0) {
				
				String code = "";
				
	            for(int i = 0; i != args.length; i++) code += args[i] + " ";
	            
	            if (code.startsWith("http")) {
	            	code = HttpUtil.requestHttp(code);
	            }
				
				try {
					Runner.runCode(code);
				} catch (Exception e) {
					sender.sendMessage(JavaShell.gamePrefix + ChatColor.RED + "Exception when running code. See console.");
					e.printStackTrace();
					return true;
				}

				sender.sendMessage(JavaShell.gamePrefix + ChatColor.GREEN + "Executed.");
				return true;
			}
			
			sender.sendMessage(JavaShell.gamePrefix + ChatColor.GOLD + "JavaShell" + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Version " + JavaShell.version);
			return true;
		}
		return false;
	}

}
