package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import client.PropertyHandler;
import connection.Connection;
import model.Audio;
import model.Ghost;
import model.Ghost.GhostMode;
import model.Model;
import view.BlockElement;
import view.View;

/**
 * 
 * Controller class. Defines how the game objects act and react to certain
 * events. Updates model and view accordingly.
 * 
 * @author antje
 * 
 *
 */
public class GameController extends KeyAdapter implements ICallback {
	private Model m;
	private View v;
	private Connection server;
	private Timer ghostScaredTimer;
	private boolean isTimerRunning;
	private Thread moveGhostThread;
	private volatile boolean ghostCanMove;
	private volatile boolean pacmanCanMove;
	private Audio audio;
	private String lastAudio;
	private int lastAudioPrio;

	public GameController(Model m, View v) {
		this.m = m;
		this.v = v;
		this.ghostScaredTimer = new Timer();
		this.isTimerRunning = false;
		this.ghostCanMove = false;
		this.pacmanCanMove = true;
		this.lastAudio = "";
		this.lastAudioPrio = Integer.MAX_VALUE;
		playSound("sounds/pacman_beginning.wav", 5);
		this.v.addKeyListener(this);
	}

	/**
	 * Start thread for ghost movement. Determine how Pacman moves wafter key
	 * events. Run method start collision.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (!this.v.isGameActive()) {
			return;
		}

		if (this.moveGhostThread == null) {
			this.moveGhostThread = new Thread(new MoveGhostThread());
			this.moveGhostThread.start();
		}

		if (this.m.getPacman().getHearts() == 0 || !this.pacmanCanMove) {
			return;
		}

		super.keyPressed(e);
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_LEFT) {
			movePacman(-1, 0);
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			movePacman(1, 0);
		} else if (keyCode == KeyEvent.VK_UP) {
			movePacman(0, -1);
		} else if (keyCode == KeyEvent.VK_DOWN) {
			movePacman(0, 1);
		}

		this.ghostCanMove = true;
		checkCollision();
		this.v.repaint();
	}

	/**
	 * Move Pacman. Check whether there is a wall in the direction the player wants
	 * to move.
	 * 
	 * @param dx
	 * @param dy
	 */
	private void movePacman(int dx, int dy) {
		playSound("sounds/pacman_chomp.wav", 4);
		// If pacman completely in one square
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");

		if (this.m.getPacman().getPosition()[0] % blockSize == 0
				&& this.m.getPacman().getPosition()[1] % blockSize == 0) {
			// Only move, when there is no wall in direction
			if (!isWallNextToPacman(dx, dy)) {
				this.m.getPacman().move(dx, dy);
			}
		} else {
			// Move pacman if possible and eat coin if within square
			if (this.m.getPacman().getPosition()[0] % blockSize == 0 && dx == 0
					|| (this.m.getPacman().getPosition()[1] % blockSize == 0 && dy == 0)) {
				this.m.getPacman().move(dx, dy);
				isSomethingEatable();
			}
		}
	}

	/**
	 * Define what happens when Pacman collides witha ghost.
	 */
	private void checkCollision() {
		Optional<Ghost> ghost = this.v.checkCollision();
		if (ghost.isPresent()) {
			if (ghost.get().getMode().equals(GhostMode.FRIGHTENED)) {
				ghost.get().setMode(GhostMode.STOP);
				ghost.get().resetGhost();
				playSound("sounds/pacman_eatghost.wav", 1);
				this.m.getPacman().eatGhost();
				Timer t = new Timer();
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						ghost.get().setMode(GhostMode.CHASE);
						v.repaint();
					}
				}, 5 * 1000);
			} else {
				this.ghostCanMove = false;
				this.pacmanCanMove = false;
				Timer t = new Timer();
				t.schedule(new TimerTask() {

					@Override
					public void run() {
						pacmanCanMove = true;
					}
				}, 1000L);

				if (this.m.getPacman().loseHeart()) {
					v.resetGame(true, true);
					this.moveGhostThread.interrupt();
					playSound("sounds/pacman_death.wav", 0);
				} else {
					v.resetGame(false, false);
				}
			}
		}
	}

	/**
	 * Make coins vanish in field when Pacman runs over it. Change leveldata
	 * accordingly. When all coins are eaten, reset everything and repaint. Check if
	 * there is a fruit in the blockElement and make Pacman eat it. Fruit disappears
	 * an ghostmode is set to frightened.
	 * 
	 */
	private void isSomethingEatable() {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int nBlocks = PropertyHandler.getPropertyAsInt("view.nblock");
		int levelData[] = this.v.getLevelData();

		int index = this.m.getPacman().getPosition()[0] / blockSize
				+ nBlocks * (int) (this.m.getPacman().getPosition()[1] / blockSize);
		int levelBlock = levelData[index];

		if (this.m.getPacman().getPosition()[0] % blockSize == 0
				&& this.m.getPacman().getPosition()[1] % blockSize == 0) {
			// Check if coin is there and eat it
			if ((levelBlock & BlockElement.POINT.getValue()) != 0) {
				this.m.getPacman().eatCoin();
				this.v.setLevelData(index, levelBlock & (BlockElement.POINT.getValue() - 1));
				if (Arrays.asList(Arrays.stream(this.v.getLevelData()).boxed().toArray(Integer[]::new)).stream()
						.filter(d -> (d & BlockElement.POINT.getValue()) != 0).collect(Collectors.toList())
						.size() == 0) {
					// all coins eaten
					this.v.resetGame(true, false);
				}
			}
			// Check if fruit is there and eat it
			if ((levelBlock & BlockElement.FRUIT.getValue()) != 0) {
				this.m.getPacman().eatFruit();
				playSound("sounds/pacman_eatfruit.wav", 1);
				this.m.getGhosts().forEach(g -> {
					g.setMode(GhostMode.FRIGHTENED);
					playSound("sounds/pacman_intermission.wav", 1);
				});
				v.repaint();
				if (isTimerRunning) {
					ghostScaredTimer.cancel();
					ghostScaredTimer.purge();
					ghostScaredTimer = new Timer();
					isTimerRunning = false;
				}

				ghostScaredTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						isTimerRunning = true;
						m.getGhosts().forEach(g -> {
							g.setMode(GhostMode.CHASE);
						});
						v.repaint();
					}
				}, PropertyHandler.getPropertyAsInt("game.ghostscaretime") * 1000);

				this.v.setLevelData(index, levelBlock & (BlockElement.FRUIT.getValue() - 1));
			}
		}
	}

	/**
	 * Check if there is a wall in the direction the user wants to move Pacman.
	 * 
	 * @param dx
	 * @param dy
	 * @return boolean
	 */
	private boolean isWallNextToPacman(int dx, int dy) {
		int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
		int nBlocks = PropertyHandler.getPropertyAsInt("view.nblock");
		int levelData[] = this.v.getLevelData();

		int index = this.m.getPacman().getPosition()[0] / blockSize
				+ nBlocks * (int) (this.m.getPacman().getPosition()[1] / blockSize);
		int levelBlock = levelData[index];

		boolean result = false;
		result |= (dx > 0 && (levelBlock & BlockElement.BORDER_RIGHT.getValue()) != 0);
		result |= (dx < 0 && (levelBlock & BlockElement.BORDER_LEFT.getValue()) != 0);
		result |= (dy > 0 && (levelBlock & BlockElement.BORDER_BOTTOM.getValue()) != 0);
		result |= (dy < 0 && (levelBlock & BlockElement.BORDER_TOP.getValue()) != 0);

		return result;
	}

	@Override
	public void pacmanDead() {

	}

	/**
	 * Method defines how ghosts move through the labyrinth
	 */
	private void updateGhosts() {
		// ghost has 3 modes.. scattered chase frightened, when scattered, each ghost
		// moves to its personal targettile outside the corner, after x seconds (or
		// after pacman eats x coins) chase mode will be activated
		this.m.getGhosts().forEach(ghost -> {
			int dx = ghost.getLastD()[0];
			int dy = ghost.getLastD()[1];

			int blockSize = PropertyHandler.getPropertyAsInt("view.blocksize");
			if ((ghost.getPosition()[0] % blockSize == 0 && ghost.getPosition()[1] % blockSize == 0)
					|| (dx == 0 && dy == 0)) {
				// get actual tile
				int nBlocks = PropertyHandler.getPropertyAsInt("view.nblock");
				int levelData[] = this.v.getLevelData();
				int index = ghost.getPosition()[0] / blockSize + nBlocks * (int) (ghost.getPosition()[1] / blockSize);
				int levelBlock = levelData[index];

				// just an example of movement algorithm.. better way would be to describe
				// different algorithm for each ghost
				boolean recalculateMovement = (dx == 0 && dy == 0);
				recalculateMovement |= (dx > 0 && (levelBlock & BlockElement.BORDER_RIGHT.getValue()) != 0);
				recalculateMovement |= (dx < 0 && (levelBlock & BlockElement.BORDER_LEFT.getValue()) != 0);
				recalculateMovement |= (dy > 0 && (levelBlock & BlockElement.BORDER_BOTTOM.getValue()) != 0);
				recalculateMovement |= (dy < 0 && (levelBlock & BlockElement.BORDER_TOP.getValue()) != 0);
				recalculateMovement |= this.getPossibleMovementsInBlock(levelBlock) > 2;

				if (recalculateMovement) {
					boolean isPossibleToMoveLeft = (dx != 1)
							&& ((levelBlock & BlockElement.BORDER_LEFT.getValue()) == 0);
					boolean isPossibleToMoveRight = (dx != -1)
							&& ((levelBlock & BlockElement.BORDER_RIGHT.getValue()) == 0);
					boolean isPossibleToMoveDown = (dy != -1)
							&& ((levelBlock & BlockElement.BORDER_BOTTOM.getValue()) == 0);
					boolean isPossibleToMoveUp = (dy != 1) && ((levelBlock & BlockElement.BORDER_TOP.getValue()) == 0);

					if (this.getPossibleMovementsInBlock(levelBlock) == 1) {
						if ((levelBlock & BlockElement.BORDER_LEFT.getValue()) == 0) {
							dx = -1;
							dy = 0;
						} else if ((levelBlock & BlockElement.BORDER_RIGHT.getValue()) == 0) {
							dx = 1;
							dy = 0;
						} else if ((levelBlock & BlockElement.BORDER_BOTTOM.getValue()) == 0) {
							dx = 0;
							dy = 1;
						} else {
							dx = 0;
							dy = -1;
						}
					} else if (this.getPossibleMovementsInBlock(levelBlock) > 2 || (dx == 0 && dy == 0)) {
						int target[] = { 0, 0 };
						if (ghost.getMode() == GhostMode.SCATTER) {
							target = ghost.getScatterPos();
						} else if (ghost.getMode() == GhostMode.CHASE) {
							target = this.m.getPacman().getPosition();
						} else {
							Random rand = new Random();
							target[0] = rand.nextInt(37) * blockSize;
							target[1] = rand.nextInt(37) * blockSize;
						}

						// if distX is bigger move x, else y
						int distX = ghost.getPosition()[0] - target[0];
						int distY = ghost.getPosition()[1] - target[1];

						boolean wantToMoveX = Math.abs(distX) > Math.abs(distY);

						if (wantToMoveX) {
							if (distX > 0) {
								// want to go left
								if (isPossibleToMoveLeft) {
									dx = -1;
									dy = 0;
								} else {
									dx = 0;
									dy = isPossibleToMoveDown ? 1 : -1;
								}
							} else {
								if (isPossibleToMoveRight) {
									dx = 1;
									dy = 0;
								} else {
									dx = 0;
									dy = isPossibleToMoveDown ? 1 : -1;
								}
							}
						} else {
							if (distY > 0) {
								if (isPossibleToMoveUp) {
									dx = 0;
									dy = -1;
								} else {
									dx = isPossibleToMoveLeft ? -1 : 1;
									dy = 0;
								}
							} else {
								if (isPossibleToMoveDown) {
									dx = 0;
									dy = 1;
								} else {
									dx = isPossibleToMoveLeft ? -1 : 1;
									dy = 0;
								}
							}
						}
					} else {
						int move[] = getMovementAfterCorner(dx, dy, levelBlock);
						dx = move[0];
						dy = move[1];
					}
				}
			}

			ghost.move(dx, dy);
			checkCollision();
			this.v.repaint();
		});
	}

	/**
	 * Specify how ghosts move when meeting a corner on the map
	 */
	private int[] getMovementAfterCorner(int dx, int dy, int levelBlock) {
		int result[] = { 0, 0 };
		if (dx != 0) {
			result[0] = 0;
			result[1] = ((levelBlock & BlockElement.BORDER_TOP.getValue()) == 0) ? -1 : 1;
		} else {
			result[0] = ((levelBlock & BlockElement.BORDER_LEFT.getValue()) == 0) ? -1 : 1;
			result[1] = 0;
		}

		return result;
	}

	/**
	 * Define which sound is played when according to predefined priority for each
	 * sound.
	 * 
	 * @param soundName
	 * @param prio
	 */
	private void playSound(String soundName, int prio) {
		try {
			if (audio == null || !soundName.equals(this.lastAudio) || !audio.isPlaying()) {
				if (!this.lastAudio.isEmpty() && !soundName.equals(this.lastAudio) && prio < this.lastAudioPrio) {
					audio.reset();
				}

				audio = new Audio(new File(soundName).getAbsoluteFile());
				audio.play();
				this.lastAudioPrio = prio;
			}
			this.lastAudio = soundName;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Determine which movements are possible when moving from one block to another
	 * 
	 * @param levelBlock
	 * @return int
	 */
	private int getPossibleMovementsInBlock(int levelBlock) {
		Integer result = 0;
		List<Integer> walls = new ArrayList<Integer>();
		walls.add(BlockElement.BORDER_RIGHT.getValue());
		walls.add(BlockElement.BORDER_LEFT.getValue());
		walls.add(BlockElement.BORDER_BOTTOM.getValue());
		walls.add(BlockElement.BORDER_TOP.getValue());
		for (Integer wall : walls) {
			result += ((levelBlock & wall) / wall);
		}
		return walls.size() - result;
	}

	private void registerAtServer() {

	}

	/**
	 * Determine how ghosts move when the game starts. Movement changes after 15
	 * seconds, ghosts start chasing after Pacman.
	 * 
	 *
	 */
	private class MoveGhostThread implements Runnable {
		@Override
		public void run() {
			int count = 0;
			while (true) {
				if (v.isGameActive() && ghostCanMove) {
					updateGhosts();
					if (moveGhostThread.isInterrupted()) {
						moveGhostThread = null;
						return;
					}

					int scatterGhost = PropertyHandler.getPropertyAsInt("game.ghostscattertime");
					if (count == (scatterGhost - 1) * 10
							&& !m.getGhosts().get(0).getMode().equals(GhostMode.FRIGHTENED)) {
						// after 15sec, but not when frightened
						m.getGhosts().stream().forEach(ghost -> {
							if (ghost.getMode().equals(GhostMode.SCATTER)) {
								ghost.setMode(GhostMode.CHASE);
							}
						});

					} else {
						++count;
					}
				}

				int updateGhost = PropertyHandler.getPropertyAsInt("game.updateghost");
				try {
					if (!moveGhostThread.isInterrupted()) {
						moveGhostThread.sleep(updateGhost * 10L);
					}
				} catch (InterruptedException e) {

				}
			}
		}
	}
}
