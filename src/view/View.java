package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Optional;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.PropertyHandler;
import controller.ICallback;
import model.Ghost;
import model.Ghost.GhostMode;
import model.Model;
import model.Pacman;
/**
* @author Antje Dehmel
* @version 1.0
*
*/
public class View extends JFrame {
	private Model m;
	private ICallback callback;
	private BoardView board;

	public View(Model m) {
		super();
		this.m = m;

		setTitle(PropertyHandler.getProperty("frame.name"));
		setSize(PropertyHandler.getPropertyAsInt("frame.sizeX"), PropertyHandler.getPropertyAsInt("frame.sizeY"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		this.board = new BoardView(m);
		this.board.setBackground(Color.BLACK); // Background Color Startseite

		add(this.board);
		setVisible(true);
	}

	public int[] getLevelData() {
		return this.board.getData();
	}

	public boolean isGameActive() {
		return this.board.isGameActive();
	}

	@Override
	public synchronized void addKeyListener(KeyListener l) {
		// TODO Auto-generated method stub
		this.board.addKeyListener(l);
	}

	@Override
	public void repaint() {
		// TODO Auto-generated method stub
		super.repaint();
		this.board.repaint();
	}

	public Optional<Ghost> checkCollision() {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		Optional<Ghost> ghosts = this.m.getGhosts().stream().filter(ghost -> {
			Rectangle rectGhost = new Rectangle(ghost.getPosition()[0], ghost.getPosition()[1], blockSize, blockSize);
			Rectangle rectPacman = new Rectangle(this.m.getPacman().getPosition()[0],
					this.m.getPacman().getPosition()[1], blockSize, blockSize);
			return rectGhost.intersects(rectPacman) && !ghost.getMode().equals(GhostMode.STOP);	
		}).findFirst();
			
		return ghosts;
	}

	public void setLevelData(int index, int data) {
		this.board.setData(index, data);
	}

	public void resetGame(boolean resetCoins, boolean deleteScore) {
		this.m.getPacman().reset(deleteScore);
		this.m.getGhosts().stream().forEach(ghost -> ghost.resetGhost());
		if (resetCoins) {
			this.board.restartGame(deleteScore);
		}
		
	}
}
