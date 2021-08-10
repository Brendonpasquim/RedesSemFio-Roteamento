package main;

import java.util.HashSet;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import beans.grafo.Aresta;
import beans.grafo.Grafo;
import beans.grafo.Vertice;

public class Test {
    public static void main(String[] args) {
        Grafo grafo = testGraphBeans();

        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("Tutorial 1");

        for (Vertice vertice : grafo.getVertices()) {
            Node novo = graph.addNode(vertice.getRotulo());
            novo.setAttribute("ui.label", vertice.getRotulo());
        }

        for (Aresta aresta : grafo.getArestas()) {
            String origem = aresta.getOrigem().getRotulo();
            String destino = aresta.getDestino().getRotulo();
            String label = origem + destino;
            Edge nova = graph.addEdge(label, origem, destino);
            nova.setAttribute("ui.label", label);
        }

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }

        graph.edges().forEach(edge -> {
            edge.setAttribute("ui.label", edge.getId());
        });

        graph.display();
    }

    public static Grafo testGraphBeans() {
        Vertice v1 = new Vertice("v1");
        Vertice v2 = new Vertice("v2");
        Vertice v3 = new Vertice("v3");
        Vertice v4 = new Vertice("v4");

        Aresta a1 = new Aresta("a1", 1, false, v1, v2);
        Aresta a2 = new Aresta("a2", 1, false, v3, v4);

        Aresta a3 = new Aresta("a3", 1, false, v2, v3);
        Aresta a4 = new Aresta("a4", 1, false, v4, v1);

        HashSet<Vertice> vertices = new HashSet<Vertice>();
        HashSet<Aresta> arestas = new HashSet<Aresta>();

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);

        arestas.add(a1);
        arestas.add(a2);
        arestas.add(a3);
        arestas.add(a4);

        Grafo g1 = new Grafo(vertices, arestas);
        return g1;
    }

    public static void testGraphicalInterface() {
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("Tutorial 1");

        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");

        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }

        graph.edges().forEach(edge -> {
            edge.setAttribute("ui.label", edge.getId());
        });

        graph.display();
    }
}
