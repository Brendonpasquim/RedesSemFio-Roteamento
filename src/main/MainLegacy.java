package main;

import buscas.Dijkstra;
import utilitarios.Utils;

import java.util.List;
import java.util.Scanner;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;

import beans.grafo.Aresta;
import beans.grafo.Grafo;
import beans.grafo.Vertice;

import static org.graphstream.ui.graphicGraph.GraphPosLengthUtils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MainLegacy {
    private static final Scanner SCAN = new Scanner(System.in);
    private static HashMap<String, Vertice> caching = new HashMap<String, Vertice>();
    private static String styleSheet = 
        "node {" +
        "	text-color: red;" +
        "	size: 15px, 20px;" +
        "}" +
        "node.marked {" +
        "	fill-color: red;" +
        "}" +
        "edge.marked {" +
        "	fill-color: orange;" +
        "}";

    public static void main(String[] args) {
        List<String> entradas = Utils.LerTxt("src/main/input.txt");
        if (entradas == null) {
            System.out.println("Nenhum dado de entrada foi encontrado.");
            System.exit(-1);
        }

        Grafo grafo = CarregarGrafo(entradas);
        Graph graph = showGrafo(grafo);

        String entrada;
        do {
            entrada = IOString("ORIGEM:");
            if (entrada.equalsIgnoreCase("sair"))
                break;
            Vertice origem = getCachedVertice(entrada);

            entrada = IOString("DESTINO:");
            if (entrada.equalsIgnoreCase("sair"))
                break;
            Vertice destino = getCachedVertice(entrada);

            cleanGrafo(graph);
            List<Vertice> menorCaminho = Dijkstra.getMenorCaminho(grafo, origem, destino);
            System.out.println(System.lineSeparator() + Arrays.toString(menorCaminho.toArray()));
            showCaminho(graph, menorCaminho);
        } while (true);
        System.out.println("Encerrando...");
        System.exit(0);
    }

    private static Grafo CarregarGrafo(List<String> entradas) {
        HashSet<Vertice> vertices = new HashSet<Vertice>();
        HashSet<Aresta> arestas = new HashSet<Aresta>();

        for (int indice = 0; indice < entradas.size(); indice++) {
            String[] valores = entradas.get(indice).split("\s");
            if (valores.length < 3) {
                // Pattern:
                // Origem[String] Destino[String] Peso[double]
                throw new IllegalArgumentException(String.format("Linha %d não estão no formato correto.", indice));
            }

            // Caching garante que cada vertice será representado pelo mesmo objeto.
            Vertice origem = getCachedVertice(valores[0]);
            Vertice destino = getCachedVertice(valores[1]);
            Aresta aresta = new Aresta(Double.parseDouble(valores[2]), origem, destino);
            vertices.add(origem);
            vertices.add(destino);
            arestas.add(aresta);
        }

        return new Grafo(vertices, arestas);
    }

    private static Vertice getCachedVertice(String rotulo) {
        Vertice vertice;
        if (caching.containsKey(rotulo))
            vertice = caching.get(rotulo);
        else {
            vertice = new Vertice(rotulo);
            caching.put(rotulo, vertice);
        }
        return vertice;
    }

    private static Graph showGrafo(Grafo grafo) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("Grafo");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", styleSheet);

        for (Vertice vertice : grafo.getVertices()) {
            Node novo = graph.addNode(vertice.getRotulo());
            novo.setAttribute("ui.label", vertice.getRotulo());
        }

        for (Aresta aresta : grafo.getArestas()) {
            String origem = aresta.getOrigem().getRotulo();
            String destino = aresta.getDestino().getRotulo();
            String label = origem + destino;
            Edge nova = graph.addEdge(label, origem, destino);
            // nova.setAttribute("ui.label", label);
            nova.setAttribute("ui.label", aresta.getPeso());
        }

        // viewer = graph.display();
        // DefaultView panel = (DefaultView) viewer.getDefaultView();
        Viewer v = graph.display();
        v.setCloseFramePolicy(CloseFramePolicy.EXIT);
        DefaultView view = (DefaultView) v.getDefaultView();

        try {	// Allow time for the viewer to build
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
                
        graph.nodes().forEach(node -> { // Only in GraphStream 2.0, otherwise use the traditional loop in lower versions
            var n = v.getGraphicGraph().getNode(node.getId());
            Point3 position = view.getCamera().transformGuToPx(Toolkit.nodePosition(n)[0], Toolkit.nodePosition(n)[1], 0);
            Point3 nova = nodePointPosition(n);
            System.out.println("Coordinate = "+position);
            System.out.println("Coordinate2 = "+nova);
            n.setAttribute("ui.label", position);
            view.getGraphics().drawOval((int)position.x-25, (int)position.y-25, 50, 50);
        });

        return graph;
    }

    private static void showCaminho(Graph graph, List<Vertice> caminho) {
        sleep(1);
        for (int indice = 0; indice < caminho.size(); indice++) {
            Vertice atual = caminho.get(indice);
            Node current = graph.getNode(atual.getRotulo());

            current.setAttribute("ui.class", "marked");

            if (indice + 1 < caminho.size()) {
                sleep(1);
                Vertice proximo = caminho.get(indice + 1);
                Node next = graph.getNode(proximo.getRotulo());
                Edge edge = current.getEdgeBetween(next);
                edge.setAttribute("ui.class", "marked");
            }

            sleep(1);
        }
    }

    private static void cleanGrafo(Graph graph) {
        graph.nodes().forEach(node -> node.removeAttribute("ui.class"));
        graph.edges().forEach(edge -> edge.removeAttribute("ui.class"));
    }

    private static void sleep(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (Exception e) {
        }
    }

    private static String IOString(String mensagem) {
        if (mensagem == null || mensagem.isEmpty()) {
            return null;
        }

        System.out.println(mensagem);
        return SCAN.nextLine();
    }
}
