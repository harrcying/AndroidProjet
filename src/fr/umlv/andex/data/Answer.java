package fr.umlv.andex.data;

import java.io.Serializable;

public class Answer implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private TypeAnswer typeAnswer;

	public TypeAnswer getTypeAnswer() {
		return typeAnswer;
	}

	public void setTypeAnswer(TypeAnswer typeAnswer) {
		this.typeAnswer = typeAnswer;
	}
}
