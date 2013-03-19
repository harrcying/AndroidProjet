package fr.umlv.andex.data;

import java.util.ArrayList;
import java.util.List;

public class AnswerRadio extends Answer{
	
	private List<Option> options = new ArrayList<Option>();
	private int value;
	
	public AnswerRadio(){
		super();
		setTypeAnswer(TypeAnswer.TYPE_ANSWER_RADIO);
	}
	
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
