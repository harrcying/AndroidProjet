package fr.umlv.andex.data;

import java.util.ArrayList;
import java.util.List;

public class NodeQuestion {

	private String title;
	private long time;
	private int id;
	private boolean open;
	private List<NodeQuestion> nodes = new ArrayList<NodeQuestion>();

	
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
		return nodes.size()==0;
	}
}
