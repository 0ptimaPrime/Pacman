package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.PropertyHandler;
import model.GameObject;
import model.Ghost;
import model.Model;

/**
* @author Antje Dehmel
* @version 1.0
*
*/
public class BoardView extends JPanel {
	private Model m;
	private boolean isLoginScreen;
	private Image ii;
	private int data[];

	public BoardView(Model m) {
		this.m = m;
		this.isLoginScreen = true;
		this.data = PropertyHandler.getLevelData();

		addRandomFruits();
		setFocusable(true);
		setLayout(null);
	}
	/**
	*Method to add fruit objects to the board, in between the walls on a random position.
	*/

	private void addRandomFruits() {
		int nBlocks = PropertyHandler.getPropertyAsInt("view.nblock");
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		for (int i = 0; i < PropertyHandler.getPropertyAsInt("game.nfruits"); ++i) {
			Random rand = new Random();
			int pos[] = { 0, 0 };
			do {
				pos[0] = rand.nextInt(nBlocks);
				pos[1] = rand.nextInt(nBlocks);
			} while ((this.data[pos[0] + nBlocks * pos[1]] & BlockElement.POINT.getValue()) == 0);
			this.setData(pos[0] + nBlocks * pos[1],
					this.data[pos[0] + nBlocks * pos[1]] & (BlockElement.POINT.getValue() - 1));
			this.setData(pos[0] + nBlocks * pos[1],
					this.data[pos[0] + nBlocks * pos[1]] | BlockElement.FRUIT.getValue());
			pos[0] *= blockSize;
			pos[1] *= blockSize;
			this.m.createFruit(pos);
		}
	}

	/**
	*Method to paint login screen, remove login screen when the game starts and then paint the maze and initial objects.
	*/
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		if (this.isLoginScreen) {
			drawLoginView();
			return;
		}

		removeAll();
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int screenSize = PropertyHandler.getPropertyAsInt("view.nblock") * blockSize;
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, screenSize + 20, screenSize + 20);
		drawScore(g2d);
		drawMaze(g2d);
		drawPacman(g2d);
		drawGhost(g2d);

		g2d.drawImage(ii, 5, 5, this);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}
	/**
	* Draw ghost objects into the maze according to the position and image of each given type of ghost.
	*/
	private void drawGhost(Graphics2D g2d) {
		for (Ghost ghost : this.m.getGhosts()) {
			g2d.drawImage(ghost.getPng(), ghost.getPosition()[0] + 1, ghost.getPosition()[1] + 1, this);
		}
	}
	/**
	* Draw pacman into the maze according to it's predefined position and image.
	*/
	private void drawPacman(Graphics2D g2d) {
		g2d.drawImage(this.m.getPacman().getPng(), this.m.getPacman().getPosition()[0] + 1,
				this.m.getPacman().getPosition()[1] + 1, this);
	}
	/**
	* Draw (walls of) the maze based on the properties leveldata and blocksize. 
	*/
	private void drawMaze(Graphics2D g2d) {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int screenSize = PropertyHandler.getPropertyAsInt("view.nblock") * blockSize;

		int i = 0;
		for (int y = 0; y < screenSize; y += blockSize) {
			for (int x = 0; x < screenSize; x += blockSize) {
				g2d.setColor(new Color(5, 100, 5));
				g2d.setStroke(new BasicStroke(2));

				// Bitoperations to determine block type
				if ((data[i] & BlockElement.BORDER_LEFT.getValue()) != 0) {
					g2d.drawLine(x, y, x, y + blockSize - 1);
				}
				if ((data[i] & BlockElement.BORDER_TOP.getValue()) != 0) {
					g2d.drawLine(x, y, x + blockSize - 1, y);
				}
				if ((data[i] & BlockElement.BORDER_RIGHT.getValue()) != 0) {
					g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1, y + blockSize - 1);
				}
				if ((data[i] & BlockElement.BORDER_BOTTOM.getValue()) != 0) {
					g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1, y + blockSize - 1);
				}
				if ((data[i] & BlockElement.POINT.getValue()) != 0) {
					g2d.setColor(new Color(192, 192, 0));
					g2d.fillRect(x + blockSize / 2 - 1, y + blockSize / 2 - 1, 2, 2);

				}
				if ((data[i] & BlockElement.BORDER_BLOCK.getValue()) != 0) {
					g2d.fillRect(x, y, blockSize, blockSize);
				}

				if ((data[i] & BlockElement.FRUIT.getValue()) != 0) {
					final int xVal = x;
					final int yVal = y;
					GameObject fruit = this.m.getFruits().stream()
							.filter(f -> f.getPosition()[0] == xVal && f.getPosition()[1] == yVal).findFirst().get();
					g2d.drawImage(fruit.getPng(), fruit.getPosition()[0] + 1, fruit.getPosition()[1] + 1, this);
				}
				++i;
			}
		}
	}
	/**
	* Draw score info below the maze.
	*/
	private void drawScore(Graphics2D g) {
		g.setColor(new Color(96, 128, 255));
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		String s = "Score: " + this.m.calculateScore();

		int frameSizeX = PropertyHandler.getPropertyAsInt("frame.sizeX");
		int frameSizeY = PropertyHandler.getPropertyAsInt("frame.sizeY");
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		g.drawString(s, (int) (frameSizeX - 100), (int) (frameSizeY * 0.90));
		g.drawString("User: " + PropertyHandler.getProperty("app.user"), 100, (int) (frameSizeY * 0.90));
		for (int i = 0; i < this.m.getPacman().getHearts(); ++i) {
			g.drawImage(this.m.getPacmanLivesImg(), 10 + i * (blockSize + 5),
					(int) (frameSizeY * 0.90 - blockSize * 0.7), this);
		}
	}
	/**
	*Set up the login view and add event listeners waiting for the player to push the start button.
	*/
	private void drawLoginView() {
		// Headerimage start page
		JLabel bg_image = new JLabel(new ImageIcon("img/pacman_logo.jpg"));
		bg_image.setSize(549, 250);
		bg_image.setLocation(180, 20);
		// Headerbild Startseite
		JLabel bg_image_2 = new JLabel(new ImageIcon("img/bild_start_2.gif"));
		bg_image_2.setSize(730, 520);
		bg_image_2.setLocation(150, 16);

		// UserData
		JLabel nameLabel = new JLabel("<html><font color='white'>Player Name:</font></html>");
		nameLabel.setFont(new Font("SANS_SERIF", Font.BOLD, 24));
		nameLabel.setSize(160, 28);
		nameLabel.setLocation(300, 450);

		// Field for text input
		Font font1 = new Font("SansSerif", Font.BOLD, 16);
		JTextField playerName = new JTextField();
		playerName.setSize(153, 32);
		playerName.setFont(font1);
		playerName.setLocation(nameLabel.getWidth() + nameLabel.getLocation().x + 10, nameLabel.getLocation().y);

		// Start button
		JButton startGame = new JButton(new ImageIcon("img/largeyellowbutton.gif"));
		startGame.setSize(156, 40);
		startGame.setLocation(playerName.getLocation().x, playerName.getLocation().y + playerName.getHeight() + 10);
		startGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (playerName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please enter a user Name", "Invalid playerName",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				PropertyHandler.setUserName(playerName.getText());
				isLoginScreen = false;
				repaint();
			}
		});

		// add elements
		add(bg_image);
		add(bg_image_2);
		add(nameLabel);
		add(playerName);
		add(startGame);
	}

	public boolean isGameActive() {
		return !this.isLoginScreen;
	}

	public int[] getData() {
		return data;
	}
	
	public void setData(int data[]) {
		this.data = data;
	}
	
	public void setData(int index, int data) {
		this.data[index] = data;
	}

	public void restartGame(boolean showLoginScreen) {
		this.data = PropertyHandler.getLevelData();
		addRandomFruits();
		this.isLoginScreen = showLoginScreen;
	}
}
