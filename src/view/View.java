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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.PropertyHandler;
import controller.ICallback;
import model.Model;
import model.Pacman;

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

	public void setLevelData(int index, int data) {
		this.board.setData(index, data);
	}
}
