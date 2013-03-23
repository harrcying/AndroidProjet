package fr.umlv.andex.data;

public class AnswerSchema extends Answer{
	
	private static final long serialVersionUID = 1L;
	private String path;

	public AnswerSchema(){
		super();
		setTypeAnswer(TypeAnswer.TYPE_ANSWER_SCHEMA);
	}
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
