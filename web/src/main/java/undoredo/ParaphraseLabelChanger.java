package undoredo;

import com.google.gson.JsonArray;

public  class ParaphraseLabelChanger implements Changeable{

	

    private final JsonArray lables;

	public ParaphraseLabelChanger(JsonArray lables){

		super();

		this.lables = lables;

	}

	public JsonArray undo(){

		return lables;

	}

	public JsonArray redo(){

		return lables;

	}
	
	public JsonArray get() {
	    return lables;
	}
	

}