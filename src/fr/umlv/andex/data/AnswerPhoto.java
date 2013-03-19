package fr.umlv.andex.data;

public class AnswerPhoto extends Answer {
	
	public AnswerPhoto(){
		super();
		setTypeAnswer(TypeAnswer.TYPE_ANSWER_PHOTO);
	}
	
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
