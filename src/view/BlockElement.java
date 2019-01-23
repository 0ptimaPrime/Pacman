package view;


/**
* Festlegen der BIT-Repräsentations-Werte der Wände für das Labyrinth. 
* Jeder Zahlenwert steht für die Richtung einer festzulegenden Wand (oben, unten, rechts, links).
* @param BORDER_LEFT
* @param BORDER_TOP
* @param BORDER_RIGHT
* @param BORDER_BOTTOM
* @param POINT
* @param BORDER_BLOCK
* @param FRUIT
* @return value
*/


public enum BlockElement {
	BORDER_LEFT(1),
	BORDER_TOP(2),
	BORDER_RIGHT(4),
	BORDER_BOTTOM(8),
	POINT(16),
	BORDER_BLOCK(32),
	FRUIT(64);
	
	private final int value;
	
	BlockElement(final int newValue) {
		value = newValue;
	}
	
	public int getValue() {
		return value;
	}
}
