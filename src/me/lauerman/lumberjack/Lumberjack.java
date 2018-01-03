package me.lauerman.lumberjack;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Lumberjack extends JavaPlugin {
	
	private BlockBreakListener blockBreakListener = new BlockBreakListener();
	private static Lumberjack instance;

	@Override
	public void onEnable() {
		instance = this;
		//Register the block break event handler with this plugin
		Bukkit.getPluginManager().registerEvents(blockBreakListener, this);
	}

	@Override
	public void onDisable() {
	}

	public static Lumberjack getPlugin() {
		return instance;
	}
}
