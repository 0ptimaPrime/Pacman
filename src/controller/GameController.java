package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import client.PropertyHandler;
import connection.Connection;
import model.Ghost.GhostMode;
import model.Model;
import view.BlockElement;
import view.View;

public class GameController extends KeyAdapter implements ICallback {
	private Model m;
	private View v;
	private Connection server;
	private Timer ghostScaredTimer;
	private boolean isTimerRunning;
	private Thread moveGhostThread;

	public GameController(Model m, View v) {
		this.m = m;
		this.v = v;
		this.ghostScaredTimer = new Timer();
		this.isTimerRunning = false;
		this.v.addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!this.v.isGameActive()) {
			return;
		}

		if (this.moveGhostThread == null) {
			this.moveGhostThread = new Thread(new MoveGhostThread());
			this.moveGhostThread.start();
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

		this.v.repaint();
	}

	private void movePacman(int dx, int dy) {
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
			}
			// Check if fruit is there and eat it
			if ((levelBlock & BlockElement.FRUIT.getValue()) != 0) {
				this.m.getPacman().eatFruit();
				this.m.getGhosts().forEach(g -> {
					g.setMode(GhostMode.FRIGHTENED);
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
		// TODO Auto-generated method stub

	}

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
						int target[] = {0,0};
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
			this.v.repaint();
		});
	}

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

	private class MoveGhostThread implements Runnable {
		@Override
		public void run() {
			int count = 0;
			while (true) {
				if (v.isGameActive()) {
					updateGhosts();

					int scatterGhost = PropertyHandler.getPropertyAsInt("game.ghostscattertime");
					if (count == (scatterGhost - 1) * 10 && !m.getGhosts().get(0).getMode().equals(GhostMode.FRIGHTENED)) {
						// after 15sec
						m.getGhosts().stream().forEach(ghost -> ghost.setMode(GhostMode.CHASE));
					} else {
						++count;
					}
				}

				int updateGhost = PropertyHandler.getPropertyAsInt("game.updateghost");
				try {
					Thread.sleep(updateGhost * 100L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
