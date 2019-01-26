package model;

import java.awt.Image;
import java.util.Arrays;

/**
 * Parent class for different kinds of Game objects.
 * Set values position and image.
 * 
 * @author Antje
 *
 */
public class GameObject {
	protected int[] position;
	protected Image png;
	protected int initialPosition[];

	public GameObject(int[] position) {
		this.position = position;
		initialPosition = Arrays.copyOf(position, position.length);
	}

	public int[] getPosition() {
		return this.position;
	}

	public Image getPng() {
		return png;
	}
}
