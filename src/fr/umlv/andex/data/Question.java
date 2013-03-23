package fr.umlv.andex.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable{
		
	private static final long serialVersionUID = 1L;
	private String title;
	private String text;
	private long time;
	private long idQuestion;
	private boolean readOnly;
	private byte[] image;
	private List<Answer> answers = new ArrayList<Answer>();

			
	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public long getIdQuestion() {
		return idQuestion;
	}
	public void setIdQuestion(long idQuestion) {
		this.idQuestion = idQuestion;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}	
}
