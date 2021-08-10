package beans.grafo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Grafo {
    private HashSet<Vertice> vertices;
    private HashSet<Aresta> arestas;
    private HashMap<Vertice, HashSet<Aresta>> mapeamento;

    public Grafo() {
        this.vertices = null;
        this.arestas = null;
        this.mapeamento = new HashMap<Vertice, HashSet<Aresta>>();
    }

    public Grafo(HashSet<Vertice> vertices, HashSet<Aresta> arestas) {
        this.vertices = vertices;
        this.arestas = arestas;
        this.mapeamento = new HashMap<Vertice, HashSet<Aresta>>();
        criarConexoes(vertices, arestas);
    }

    public void setVertices(HashSet<Vertice> vertices) {
        this.vertices = vertices;
    }

    public void setArestas(HashSet<Aresta> arestas) {
        this.arestas = arestas;
    }

    public HashSet<Vertice> getVertices() {
        return vertices;
    }

    public HashSet<Aresta> getArestas() {
        return arestas;
    }

    public HashMap<Vertice, HashSet<Aresta>> getMapeamento() {
        return mapeamento;
    }

    public void criarConexoes(HashSet<Vertice> vertices, HashSet<Aresta> arestas) {
        this.mapeamento.clear();
        for (Vertice vertice : vertices) {
            for (Aresta aresta : arestas) {
                if (aresta.isExtremo(vertice)) {
                    if (mapeamento.containsKey(vertice)) {
                        mapeamento.get(vertice).add(aresta);
                    } else {
                        HashSet<Aresta> conexoes = new HashSet<>();
                        conexoes.add(aresta);
                        mapeamento.put(vertice, conexoes);
                    }
                }
            }
        }
    }

    public List<Vertice> getVizinhos(Vertice vertice) {
        if (!this.mapeamento.containsKey(vertice))
            return null;

        List<Vertice> vizinhos = new ArrayList<Vertice>();
        for (Aresta conexao : this.mapeamento.get(vertice)) {
            vizinhos.add(conexao.getOutroExtremo(vertice));
        }
        return vizinhos;
    }
}
