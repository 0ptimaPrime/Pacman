package model;

import java.util.Random;

import javax.swing.ImageIcon;

/**
* class Fruits
* @author Antje Dehmel
* @author Jose Mendez
* @version 1.0
*
*/
public class Fruit extends GameObject {
	public Fruit(int[] position) {
		super(position);
		this.setRandomFruit();
	}
	
	/**
	 * The method creates a random fruit
	 */
	public void setRandomFruit() {
		Random rand = new Random();
		String path = "img/fruit_" + rand.nextInt(3) + ".png";
		super.png = new ImageIcon(path).getImage();
	}
}
