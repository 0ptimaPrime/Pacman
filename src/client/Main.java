package client;

import java.awt.EventQueue;

import controller.GameController;
import model.Model;
import view.View;

/**
 * 
 * Main - Starts the application
 * @author Antje
 *
 */



public class Main {

	public static void main(String[] args) {
		// Start Application
		// Gui runs in own thread, runs savely forward even if other things are done
		// prevents gui from stopping when other things are done
		EventQueue.invokeLater(() -> {
			Model m = new Model();
			View v = new View(m);
			new GameController(m, v);			
		});		
	}
}
