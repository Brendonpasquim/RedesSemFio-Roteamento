package main;

import java.util.Iterator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphExplorer {
	public static void main(String args[]) {
		new GraphExplorer();
	}

	public GraphExplorer() {
		Graph graph = new SingleGraph("tutorial 1");

		System.setProperty("org.graphstream.ui", "swing");
		graph.setAttribute("ui.stylesheet", styleSheet);
		graph.setAutoCreate(true);
		graph.setStrict(false);
		graph.display();

		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.addEdge("AD", "A", "D");
		graph.addEdge("DE", "D", "E");
		graph.addEdge("DF", "D", "F");
		graph.addEdge("EF", "E", "F");

		for (Node node : graph) {
			node.setAttribute("ui.label", node.getId());
		}

		explore(graph.getNode("A"));
	}

	public void explore(Node source) {
		Iterator<? extends Node> k = source.getBreadthFirstIterator();

		while (k.hasNext()) {
			Node next = k.next();
			next.setAttribute("ui.class", "marked");
			next.edges().forEach(edge -> {
				edge.setAttribute("ui.class", "marked");
				System.out.println(edge.getId());
			});
			sleep();
		}
	}

	protected void sleep() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
	}

	protected String styleSheet =
        "node {" +
        "	shape: box;" +
        "	size: 15px, 20px;" +
        "	fill-mode: plain;" +
        "	fill-color: red;" +
        "	stroke-mode: plain;" +
        "	stroke-color: blue;" +
        "}" +
        "node.marked {" +
        "	fill-color: red;" +
        "}" +
        "edge.marked {" +
        "	fill-color: green;" +
        "}";
}
