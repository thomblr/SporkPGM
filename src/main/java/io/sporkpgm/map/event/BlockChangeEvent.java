package io.sporkpgm.map.event;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.player.SporkPlayer;
import io.sporkpgm.region.types.BlockRegion;
import io.sporkpgm.util.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BlockChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Event cause;
	private SporkMap map;
	private SporkPlayer player;
	private BlockState oldState;
	private BlockState newState;

	public BlockChangeEvent(Event cause, SporkMap map, SporkPlayer player, BlockState oldState, BlockState newState) {
		this.cause = cause;
		this.map = map;
		this.player = player;
		this.oldState = oldState;
		this.newState = newState;

		check();
	}

	public BlockChangeEvent(Event cause, SporkMap map, Player player, BlockState oldState, BlockState newState) {
		this(cause, map, SporkPlayer.getPlayer(player), oldState, newState);
	}

	private void check() {
		BlockRegion block = getRegion();

		if(block.getStringX().equals("-10.0") && block.getStringY().equals("3.0") && block.getStringZ().equals("46.0")) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for(StackTraceElement element : trace) {
				Log.info(element.toString());
			}
		}
	}

	public Event getEvent() {
		return cause;
	}

	public SporkMap getMap() {
		return map;
	}

	public boolean hasPlayer() {
		return player != null;
	}

	public SporkPlayer getPlayer() {
		return player;
	}

	public BlockState getState() {
		return getLocation().getBlock().getState();
	}

	public BlockState getOldState() {
		return oldState;
	}

	public BlockState getNewState() {
		return newState;
	}

	public Block getBlock() {
		return getLocation().getBlock();
	}

	public Block getOldBlock() {
		return getOldState().getBlock();
	}

	public Block getNewBlock() {
		return getNewState().getBlock();
	}

	public Location getLocation() {
		return getNewState().getLocation();
	}

	public boolean isCancellable() {
		return cause instanceof Cancellable;
	}

	public boolean isCancelled() {
		if(!isCancellable()) {
			BlockState state = getNewState();
			BlockState current = getBlock().getState();

			boolean type = state.getData().getItemType() != current.getData().getItemType();
			boolean data = state.getData().getData() != current.getData().getData();

			return type || data;
		}

		Cancellable cancel = (Cancellable) cause;
		return cancel.isCancelled();
	}

	public boolean setCancelled(boolean cancelled) {
		if(!isCancellable()) {
			BlockState state = getOldState();
			if(!cancelled) {
				state = getNewState();
			}

			getLocation().getBlock().setType(state.getType());
			getLocation().getBlock().setData(state.getData().getData());
			return false;
		}

		Cancellable cancel = (Cancellable) cause;
		cancel.setCancelled(cancelled);

		return true;
	}

	public boolean isBreak() {
		return getOldState().getType() != Material.AIR || getNewState().getType() == Material.AIR;
	}

	public boolean isPlace() {
		return getOldState().getType() == Material.AIR;
	}

	public BlockRegion getRegion() {
		return new BlockRegion(getLocation());
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
