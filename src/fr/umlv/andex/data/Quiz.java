package fr.umlv.andex.data;


public class Quiz {
	
	private long idQuiz;
	private String title;
	private String description;
	private StateQuiz state;
	
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
	private TreeQuestion tree;
	
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
}
