package model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import client.PropertyHandler;
import model.Model.DIRECTION;

/**
 * Define Ghosts as GameObjects, add additional parameters and methods.
 * 
 * @author antje
 *
 */

public class Ghost extends GameObject implements IFigure {
	// states for ghosts
	public enum GhostMode {
		STOP, CHASE, SCATTER, FRIGHTENED
	}

	private GhostMode mode;
	private String name;
	private int[] scatterPos;
	// Prevent compiler optimization
	private volatile int lastDx;
	private volatile int lastDy;

	// Set starting parameters for ghosts
	public Ghost(int[] position, String name, int[] scatterPos) {
		super(position);
		this.mode = GhostMode.SCATTER;
		this.name = name;
		this.scatterPos = scatterPos;

		this.setPng(DIRECTION.RIGHT);
	}

	/**
	 * Reset ghosts to their position of origin.
	 */
	public void resetGhost() {
		position = Arrays.copyOf(initialPosition, initialPosition.length);
		this.lastDx = 0;
		this.lastDy = 0;
		this.setPng(DIRECTION.RIGHT);
	}

	@Override
	/**
	 * Set different paramaters for ghost movement
	 */
	public void move(int dx, int dy) {
		if (this.mode.equals(GhostMode.STOP)) {
			return;
		}
		int speed = PropertyHandler.getPropertyAsInt("speed.ghost");
		this.position[0] += (dx * speed);
		this.position[1] += (dy * speed);

		this.lastDx = dx;
		this.lastDy = dy;
		if (dx < 0) {
			this.setPng(DIRECTION.LEFT);
		} else if (dx > 0) {
			this.setPng(DIRECTION.RIGHT);
		} else if (dy < 0) {
			this.setPng(DIRECTION.UP);
		} else {
			this.setPng(DIRECTION.DOWN);
		}
	}

	public boolean isGhostDeadOnCollision() {
		return false;
	}

	/**
	 * Set new image for ghosts when state is frightened.
	 */
	@Override
	public void setPng(DIRECTION direction) {
		String path = "";
		if (this.mode == GhostMode.FRIGHTENED) {
			path = "img/scared_" + direction.toString().toLowerCase() + ".png";
		} else {
			path = "img/" + this.name + "_" + direction.toString().toLowerCase() + ".png";
		}

		super.png = new ImageIcon(path).getImage();
	}

	public GhostMode getMode() {
		return mode;
	}

	/**
	 * Check mode of ghost. Change mode according to previous ghost mode. Set speed
	 * of thread (slower thread for frightened ghosts, faster for non frightened
	 * ghosts.
	 * 
	 * @param mode
	 */
	public void setMode(GhostMode mode) {
		if (this.mode != mode) {
			if (mode == GhostMode.FRIGHTENED) {
				int speed = PropertyHandler.getPropertyAsInt("game.updateghost");
				PropertyHandler.setGhostUpdate(speed + 2);
				this.lastDx *= -1;
				this.lastDy *= -1;
				this.mode = mode;
				return;
			} else if (this.mode == GhostMode.FRIGHTENED) {
				int speed = PropertyHandler.getPropertyAsInt("game.updateghost");
				PropertyHandler.setGhostUpdate(speed - 2);
			}
			this.mode = mode;
		}
	}

	public int[] getScatterPos() {
		return scatterPos;
	}

	public int[] getLastD() {
		int dx[] = { this.lastDx, this.lastDy };
		return dx;
	}
}
