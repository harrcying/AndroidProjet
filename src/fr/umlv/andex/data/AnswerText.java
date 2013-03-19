package fr.umlv.andex.data;

public class AnswerText extends Answer{
	
	private String value;
	
	public AnswerText(){
		super();
		setTypeAnswer(TypeAnswer.TYPE_ANSWER_TEXT);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
