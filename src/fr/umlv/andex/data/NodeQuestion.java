package fr.umlv.andex.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeQuestion implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title;
	private long time;
	private int id;
	private boolean open;
	private List<NodeQuestion> nodes = new ArrayList<NodeQuestion>();
	private Question question = null;

	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
		
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<NodeQuestion> getNodes() {
		return nodes;
	}
	public void setNodes(List<NodeQuestion> nodes) {
		this.nodes = nodes;
	}
	
	public boolean isLeaf(){
		return (question != null);
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		if (nodes.size()==0) {
			this.question = question;
		}
	}
}
