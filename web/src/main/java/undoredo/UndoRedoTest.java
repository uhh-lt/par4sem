package undoredo;

public class UndoRedoTest{

	public static void main(String[] args){

		ChangeManager manager = new ChangeManager();

		manager.addChangeable(new ParaphraseTextChanger("A") );
		manager.addChangeable(new ParaphraseTextChanger("B") );
		manager.addChangeable(new ParaphraseTextChanger("C"));
		System.out.println(manager.undo());
		manager.addChangeable(new ParaphraseTextChanger("X"));
		
		System.out.println(manager.undo());
		System.out.println(manager.undo());
		System.out.println(	manager.redo());
		System.out.println(	manager.redo());
		System.out.println(	manager.redo());
		System.out.println(manager.undo());
		System.out.println(manager.undo());
		System.out.println(manager.redo());
		manager.addChangeable(new ParaphraseTextChanger("Hi") );
		System.out.println(manager.undo());
		System.out.println(manager.redo());
		System.out.println(manager.redo());

	}

}
