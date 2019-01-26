package model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import client.PropertyHandler;

 /**
 * class Model
 * @author Antje Dehmel
 * @author Jose Mendez
 * @version 1.0
 *
 */
public class Model {
	private List<GameObject> fruits;
	private List<Ghost> ghosts;
	private Pacman pacman;
	private Score score;

	/**
	 * enum DIRECTION: define collections of directions constants
	 *
	 */
	public static enum DIRECTION {
		UP, RIGHT, DOWN, LEFT
	}

	/**
	 * Method Model create two ArrayList for GameObject object and Ghost object
	 */
	public Model() {
		this.fruits = new ArrayList<GameObject>();
		this.ghosts = new ArrayList<Ghost>();
		createPacman();
		createGhost();
	}

	/**
	 * 
	 */
	private void createPacman() {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int pacmanPos[] = { 15 * blockSize, 19 * blockSize };
		this.pacman = new Pacman(pacmanPos);
	}

	/**
	 * the method create a number of Ghost objects
	 */
	private void createGhost() {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int nBlocks = PropertyHandler.getPropertyAsInt("view.nblock");

		for (String ghostName : PropertyHandler.getGhostNames()) {
			int indexOf = PropertyHandler.getGhostNames().indexOf(ghostName);
			int pos[] = { indexOf * 8 * blockSize, 3 * blockSize };
			// TODO this only works for max. 4 ghosts at the moment. Find a generic version
			int scatterPos[] = { ((indexOf & 2) >> 1) * nBlocks * blockSize, (indexOf & 1) * nBlocks * blockSize };
			this.ghosts.add(new Ghost(pos, ghostName, scatterPos));
		}
	}

	/**
	 * Method createFruit
	 * @param position Array
	 */
	public void createFruit(int[] position) {
		this.fruits.add(new Fruit(position));
	}

	/**
	 * Method calculateScore
	 * @return int
	 */
	public int calculateScore() {
		return pacman.getCoinsEaten() * 10 + pacman.getFruitsEaten() * 50 + pacman.getGhostsEaten() * 100;
	}

	/**
	 * @return Pacman Object
	 */
	public Pacman getPacman() {
		return pacman;
	}

	/**
	 * @return List<GameObject> 
	 */
	public List<GameObject> getFruits() {
		return this.fruits;
	}

	/**
	 * @return List<Ghost>
	 */
	public List<Ghost> getGhosts() {
		return ghosts;
	}

	/**
	 * @return ImageIcon Object
	 */
	public Image getPacmanLivesImg() {
		return new ImageIcon("img/pacman_0_right.png").getImage();
	}
}
