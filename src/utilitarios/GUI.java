package utilitarios;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import beans.grafo.Aresta;
import beans.grafo.Vertice;
import beans.rede.Conexao;
import beans.rede.Host;
import beans.rede.Rede;

public class GUI {
    private static final String styleSheet = "node {text-color: red; size: 15px, 20px;}"
            + "node.marked {fill-color: red;}" + "edge.marked {fill-color: orange;}";

    private Rede rede;
    private Graph graph;
    private Viewer viewer;
    private ProxyPipe pipe;

    public GUI() {
        init();
        generateGridNetwork(1);
        setStyle();
        
        ProxyPipe pipe = this.viewer.newViewerPipe();
        pipe.addAttributeSink(this.graph);
        this.pipe = pipe;
    }

    private void init() {
        System.setProperty("org.graphstream.ui", "swing");
    }

    private void setStyle() {
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", styleSheet);
    }

    // #region Métodos para exibir menor caminho entre nós.
    private Graph showGraph() {
        this.viewer.setCloseFramePolicy(CloseFramePolicy.EXIT);

        for (Vertice vertice : this.rede.getVertices()) {
            Node novo = graph.addNode(vertice.getId());
            novo.setAttribute("ui.label", vertice.getRotulo());
        }

        Utils.sleep(1);
        for (Aresta aresta : this.rede.getArestas()) {
            String origem = aresta.getOrigem().getId();
            String destino = aresta.getDestino().getId();
            String label = origem + destino;
            Edge nova = graph.addEdge(label, origem, destino);

            double distancia = getNodeDistance(aresta.getOrigem(), aresta.getDestino());
            nova.setAttribute("ui.label", distancia);
        }

        return graph;
    }

    private void showPath(List<Vertice> caminho) {
        Utils.sleep(1);
        for (int indice = 0; indice < caminho.size(); indice++) {
            Vertice atual = caminho.get(indice);
            Node current = this.graph.getNode(atual.getId());

            current.setAttribute("ui.class", "marked");

            if (indice + 1 < caminho.size()) {
                Utils.sleep(1);
                Vertice proximo = caminho.get(indice + 1);
                Node next = this.graph.getNode(proximo.getId());
                Edge edge = current.getEdgeBetween(next);
                edge.setAttribute("ui.class", "marked");
            }

            Utils.sleep(1);
        }
    }

    private void cleanPaths() {
        this.graph.nodes().forEach(node -> node.removeAttribute("ui.class"));
        this.graph.edges().forEach(edge -> edge.removeAttribute("ui.class"));
    }
    // #endregion

    private Point3 getPosition(Vertice vertice) {
        DefaultView view = (DefaultView) this.viewer.getDefaultView();
        Node node = viewer.getGraphicGraph().getNode(vertice.getId());

        Point3 point = new Point3();
        try {
            point = Toolkit.nodePointPosition(node);
        } catch (Exception e) {
            Utils.sleep(1);
            node = viewer.getGraphicGraph().getNode(vertice.getId());
            point = Toolkit.nodePointPosition(node);
        }
        return view.getCamera().transformGuToPx(point.x, point.y, 0);
    }

    private void generateGridNetwork(int numeroDeVertices) {
        Graph graph = new SingleGraph("grid");
        Generator gen = new GridGenerator();

        gen.addSink(graph);
        gen.begin();
        for (int i = 0; i < numeroDeVertices; i++) {
            gen.nextEvents();
        }
        gen.end();

        this.graph = graph;
        while (this.graph.getEdgeCount() > 0) {
            this.graph.removeEdge(0);
        }

        HashMap<String, Host> caching = new HashMap<String, Host>();
        // HashSet<Host> hosts = new HashSet<Host>();
        // HashSet<Conexao> conexoes = new HashSet<Conexao>();
        Set<Host> hosts = Collections.newSetFromMap(new ConcurrentHashMap<Host, Boolean>());
        Set<Conexao> conexoes = Collections.newSetFromMap(new ConcurrentHashMap<Conexao, Boolean>());

        for (Node node : graph) {
            String label = node.getId();
            node.setAttribute("ui.label", label);

            Host host = new Host(label, label, 100, null);
            hosts.add(host);
            caching.put(label, host);
        }

        for (int indice = 0; indice < graph.getEdgeCount(); indice++) {
            Edge edge = graph.getEdge(indice);
            String sourceLabel = edge.getSourceNode().getId();
            String targetLabel = edge.getTargetNode().getId();
            String label = sourceLabel + "--" + targetLabel;
            edge.setAttribute("ui.label", label);

            Host origem = caching.get(sourceLabel);
            Host destino = caching.get(targetLabel);
            Conexao aresta = new Conexao(edge.getId(), label, origem, destino);
            conexoes.add(aresta);
        }

        // Nodes already have a position.
        this.viewer = graph.display(false);
        this.viewer.disableAutoLayout();
        Utils.sleep(1);

        this.rede = new Rede(hosts, conexoes);
    }

    public double getNodeDistance(Vertice origem, Vertice destino) {
        Point3 source = getPosition(origem);
        Point3 destination = getPosition(destino);

        return source.distance(destination);
    }

    public void drawCommunicationRange(Vertice vertice, double alcanceMaximo) {
        DefaultView view = (DefaultView) viewer.getDefaultView();
        Point3 position = getPosition(vertice);

        int raio = (int) alcanceMaximo;
        view.getGraphics().drawOval((int) position.x - raio, (int) position.y - raio, raio * 2, raio * 2);
    }

    public void refresh() {
        for (Host host : this.rede.getHosts()) {
            Node novo = graph.getNode(host.getId());
            Point3 position = getPosition(host);
            // String rotulo = String.format("%s [x: %d; y: %d]", host,
            // Math.round(position.x), Math.round(position.y));
            // novo.setAttribute("ui.label", rotulo);
            novo.setAttribute("ui.label", host);
            drawCommunicationRange(host, host.getAlcanceMaximo());
        }

        for (Conexao conexao : this.rede.getConexoes()) {
            Edge edge = graph.getEdge(conexao.getId());

            double distancia = getNodeDistance(conexao.getOrigem(), conexao.getDestino());
            edge.setAttribute("ui.label", Math.round(distancia));
        }
    }

    public Rede getRede() {
        return rede;
    }

    public Graph getGraph() {
        return graph;
    }

    public ProxyPipe getPipe() {
        return pipe;
    }
}
