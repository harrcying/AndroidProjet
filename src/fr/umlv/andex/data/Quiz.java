package fr.umlv.andex.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class Quiz implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long idQuiz;
	private String title;
	private String description;
	private StateQuiz state;
	private TreeQuestion tree;
	private List<Question> questionsByOrder = new LinkedList<Question>();
	
	public StateQuiz getState() {
		return state;
	}
	public void setState(StateQuiz state) {
		this.state = state;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public TreeQuestion getTree() {
		return tree;
	}
	public void setTree(TreeQuestion tree) {
		this.tree = tree;
	}
	
	public long getIdQuiz() {
		return idQuiz;
	}
	public void setIdQuiz(long idQuiz) {
		this.idQuiz = idQuiz;
	}
	public List<Question> getQuestionsByOrder() {
		return questionsByOrder;
	}
	public void setQuestionsByOrder(List<Question> questionsByOrder) {
		this.questionsByOrder = questionsByOrder;
	}
}
