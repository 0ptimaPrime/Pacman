package model;

import java.awt.Image;

import javax.swing.ImageIcon;

import client.PropertyHandler;
import model.Model.DIRECTION;

public class Ghost extends GameObject implements IFigure {
	public enum GhostMode {
		CHASE,
		SCATTER,
		FRIGHTENED
	}
	
	private GhostMode mode;
	private GhostMode lastMode;
	private String name;
	private int[] scatterPos;
	private volatile int lastDx;
	private volatile int lastDy;
	
	public Ghost(int[] position, String name, int[] scatterPos) {
		super(position);
		this.mode = GhostMode.SCATTER;
		this.name = name;
		this.scatterPos = scatterPos;
		
		this.setPng(DIRECTION.RIGHT);
	}

	@Override
	public void move(int dx, int dy) {
		int speed = PropertyHandler.getPropertyAsInt("speed.ghost");
		super.position[0] += (dx * speed);
		super.position[1] += (dy * speed);
		
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

	public void setMode(GhostMode mode) {
		if (this.mode != mode) {
			if (mode == GhostMode.FRIGHTENED) {
				this.lastMode = this.mode;
				this.lastDx *= -1;
				this.lastDy *= -1;
				this.mode = mode;
			} else if (this.mode == GhostMode.FRIGHTENED) {
				this.mode = lastMode;
			} else {
				this.mode = mode;
			}
		}
	}

	public int[] getScatterPos() {
		return scatterPos;
	}
	
	public int[] getLastD() {
		int dx[] = {this.lastDx, this.lastDy};
		return dx;
	}
}
