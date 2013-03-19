package fr.umlv.andex.data;

public class QuizDescription {

	private String titleQuiz;
	private long idQuiz;
	private boolean inProgress;
	
	public boolean isInProgress() {
		return inProgress;
	}
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	public String getTitleQuiz() {
		return titleQuiz;
	}
	public void setTitleQuiz(String titleQuiz) {
		this.titleQuiz = titleQuiz;
	}
	public long getIdQuiz() {
		return idQuiz;
	}
	public void setIdQuiz(long idQuiz) {
		this.idQuiz = idQuiz;
	}
	
	public String toString(){
		return titleQuiz;
	} 
}
