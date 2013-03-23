package fr.umlv.andex.data;

import java.io.Serializable;
import java.util.List;

public class TreeQuestion implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public List<NodeQuestion> nodes;

	public List<NodeQuestion> getNodes() {
		return nodes;
	}

	public void setNodes(List<NodeQuestion> node) {
		this.nodes = node;
	}

}
