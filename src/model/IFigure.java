package model;

/**
 * interface IFigure Adds methods for movable game objects.
 * 
 * @author Antje Dehmel
 * @author Jose Mendez
 * @version 1.0
 *
 */
public interface IFigure {
	public void move(int dx, int dy);

	public void setPng(Model.DIRECTION direction);
}
