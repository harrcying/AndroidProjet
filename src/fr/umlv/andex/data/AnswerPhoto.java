package fr.umlv.andex.data;

public class AnswerPhoto extends Answer {
	
	private static final long serialVersionUID = 1L;
	private String path;
	
	public AnswerPhoto(){
		super();
		setTypeAnswer(TypeAnswer.TYPE_ANSWER_PHOTO);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
