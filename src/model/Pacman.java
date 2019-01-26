package model;

import java.awt.Image;

/**
 * Define all parameters and possible actions for Game object Pacman. 
 * 
 * @author José Mendez
 * @author antje
 */
import java.util.Arrays;

import javax.swing.ImageIcon;

import client.PropertyHandler;
import model.Model.DIRECTION;

public class Pacman extends GameObject implements IFigure {
	private int coinsEaten;
	private int fruitsEaten;
	private int ghostsEaten;
	private int hearts;
	private int anim;
	private int lastDx;
	private int lastDy;

	public Pacman(int[] position) {
		super(position);
		this.coinsEaten = 0;
		this.fruitsEaten = 0;
		this.ghostsEaten = 0;
		this.hearts = 3;
		this.anim = 0;
		this.lastDx = 0;
		this.lastDy = 0;
		this.setPng(DIRECTION.RIGHT);
	}

	@Override
	public void move(int dx, int dy) {
		int speed = PropertyHandler.getPropertyAsInt("speed.pacman");
		super.position[0] += (dx * speed);
		super.position[1] += (dy * speed);

		if ((this.lastDx != 0 && this.lastDx == dx) || (this.lastDy != 0 && this.lastDy == dy)) {
			anim = (anim + 1) % 3;
		} else {
			anim = 0;
		}

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

	@Override
	public void setPng(Model.DIRECTION direction) {
		String path = "img/pacman_" + anim;
		if (anim != 2) {
			path += "_" + direction.toString().toLowerCase();
		}
		path += ".png";
		super.png = new ImageIcon(path).getImage();
	}

	public void eatCoin() {
		coinsEaten++;
	}

	public void eatGhost() {
		ghostsEaten++;
	}

	public void eatFruit() {
		fruitsEaten++;
	}

	public boolean loseHeart() {
		--hearts;
		return hearts == 0;
	}

	public int getCoinsEaten() {
		return coinsEaten;
	}

	public int getFruitsEaten() {
		return fruitsEaten;
	}

	public int getGhostsEaten() {
		return ghostsEaten;
	}

	public void reset(boolean deleteScore) {
		this.position = Arrays.copyOf(initialPosition, initialPosition.length);
		this.anim = 0;
		this.lastDx = 0;
		this.lastDy = 0;

		if (deleteScore) {
			this.coinsEaten = 0;
			this.fruitsEaten = 0;
			this.ghostsEaten = 0;
			this.hearts = 3;
		}

		this.setPng(DIRECTION.RIGHT);
	}

	public int getHearts() {
		return hearts;
	}
}
