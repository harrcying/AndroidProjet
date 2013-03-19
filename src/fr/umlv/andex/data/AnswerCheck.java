package fr.umlv.andex.data;

import java.util.ArrayList;
import java.util.List;

public class AnswerCheck extends Answer{
	
	private List<Option> options = new ArrayList<Option>();
	private List<Integer> values = new ArrayList<Integer>();
	
	public AnswerCheck(){
		super();
		setTypeAnswer(TypeAnswer.TYPE_ANSWER_CHECK);
	}
	
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}
}
