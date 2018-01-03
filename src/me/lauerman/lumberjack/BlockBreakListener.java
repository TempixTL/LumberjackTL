package me.lauerman.lumberjack;

import java.util.ArrayList;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.metadata.FixedMetadataValue;

class BlockBreakListener implements Listener {

	//Create constant for metedata key used to index logs
	private final String indexedLogMetadataKey = "indexedLogTL";

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
	
		
		if (isLog(block)) {
			//Get a list of all connected logs to the one broken (see method below)
			ArrayList<Block> logList = getAdjacentLogs(block);

			//Iterate to find highest and lowest block (by y position)
			Block lowestLog = logList.get(0);
			Block highestLog = logList.get(0);
			for (Block log : logList) {
				if (log.getLocation().getY() < lowestLog.getLocation().getY()) {
					lowestLog = log;
				}
				if (log.getLocation().getY() > highestLog.getLocation().getY()) {
					highestLog = log;
				}
			}
			
			//Iterate through block faces to check if highest log has at least one leaves block near it
			boolean hasLeaves = false;
			for (BlockFace face : BlockFace.values()) {
				Block adjacentBlock = highestLog.getRelative(face);
				if (isLeaves(adjacentBlock)) {
					hasLeaves = true;
					break;
				}
			}
			
			//If lowest block has dirt below it and highest block has leaves then it's a tree in my book! Destroy all logs
			if (lowestLog.getRelative(BlockFace.DOWN).getType() == Material.DIRT && hasLeaves) {
				for (Block log: logList) {
					log.breakNaturally();
				}
			} else {
			//If it's not a tree, iterate through logs and remove metadata key
				for (Block log : logList) {
					log.removeMetadata(indexedLogMetadataKey, Lumberjack.getPlugin());
				}
			}
		}
	}

	//Recursive method to get all surrounding blocks of type Material.LOG
	private ArrayList<Block> getAdjacentLogs(Block startingBlock) {
		//Create ArrayList and add startingBlock
		ArrayList<Block> blockList = new ArrayList<Block>();
		blockList.add(startingBlock);
		//Set metadata value showing that we have already found this log
		startingBlock.setMetadata(indexedLogMetadataKey, new FixedMetadataValue(Lumberjack.getPlugin(), true));	

		//Iterate through all surrounding blocks to find logs that we haven't checked
		for (BlockFace face: BlockFace.values()) {
			Block adjacentBlock = startingBlock.getRelative(face);
			if (isLog(adjacentBlock) && adjacentBlock.hasMetadata(indexedLogMetadataKey) == false) {
				//Add result of recursive call to array list when it returns
				blockList.addAll(getAdjacentLogs(adjacentBlock));
			}
		}
		
		return blockList;
	}
	
	//Two blocks can be logs, Material.LOG_2 seems to be used with acacia and dark oak
	private boolean isLog(Block b) {
		return (b.getType() == Material.LOG || b.getType() == Material.LOG_2);
	}
	
	//Same for leaves
	private boolean isLeaves(Block b) {
		return (b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2);
	}
}
