package model;

import java.util.Date;

/**
 * Define Score for different actions of Pacman. 
 * @author antje
 */

import client.PropertyHandler;

public class Score {
	private int score;
	private String player;
	private String date;
	private Pacman pacman;

	public Score(Pacman pacman) {
		this.player = PropertyHandler.getUserName();
		this.pacman = pacman;
		this.date = new Date().toString();
	}

	public int getScoreValue() {
		updateScore();
		return score;
	}

	public void updateScore() {
		this.score = this.pacman.getCoinsEaten() + this.pacman.getFruitsEaten() * 50
				+ this.pacman.getGhostsEaten() * 100;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
