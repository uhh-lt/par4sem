package undoredo;


/**
 * 
 * Based ont the implimentation here:
 * http://www.algosome.com/articles/implementing-undo-redo-java.html
 *
 */

public interface Changeable {

	/**

	 * Undoes an action. Only on change lables

	 */

	public Object undo();
	

	/**

	 * Redoes an action. Only on change lables

	 */

	public Object redo();

	public Object get();

}
