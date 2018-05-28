package undoredo;

public  class ParaphraseTextChanger implements Changeable{

	

    private final String text;

	public ParaphraseTextChanger(String text){

		super();

		this.text = text;

	}

	public String undo(){

		return text;

	}

	public String redo(){

		return text;

	}

	public String get() {
	    return text;
	}
}