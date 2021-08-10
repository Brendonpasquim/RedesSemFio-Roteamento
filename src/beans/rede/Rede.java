package beans.rede;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import beans.grafo.Aresta;
import beans.grafo.Grafo;
import beans.grafo.Vertice;

public class Rede extends Grafo {
    public static final Host BROADCAST = new Host("X", "X", 0, null);

    private Set<Host> hosts;
    private Set<Conexao> conexoes;

    public Rede(Set<Host> hosts, Set<Conexao> conexoes) {
        this.hosts = hosts;
        this.conexoes = conexoes;

        HashSet<Vertice> vertices = new HashSet<Vertice>();
        vertices.addAll(hosts);

        HashSet<Aresta> arestas = new HashSet<Aresta>();
        arestas.addAll(conexoes);

        super.setVertices(vertices);
        super.setArestas(arestas);
        super.criarConexoes(vertices, arestas);
    }

    public Set<Host> getHosts() {
        return hosts;
    }

    public Set<Conexao> getConexoes() {
        return conexoes;
    }

    /**
     * Unifica a topologia atual com a topologia recebida.
     * 
     * @param rede Rede a qual se deseja unificar.
     */
    public void unificar(Rede rede) {
        this.hosts.addAll(rede.getHosts());
        this.conexoes.addAll(rede.getConexoes());
        this.getVertices().addAll(rede.getHosts());
        this.getArestas().addAll(rede.getConexoes());

        HashSet<Vertice> vertices = new HashSet<Vertice>();
        vertices.addAll(this.hosts);

        HashSet<Aresta> arestas = new HashSet<Aresta>();
        arestas.addAll(this.conexoes);

        super.criarConexoes(vertices, arestas);
    }
    
    /**
     * Limpa a topologia atual, deletando todos os hosts e conexões conhecidas.
     */
    public void limpar() {
      this.getConexoes().clear();
      this.getHosts().clear();
      this.getArestas().clear();
      this.getVertices().clear();
      this.getMapeamento().clear();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (Entry<Vertice, HashSet<Aresta>> elemento : this.getMapeamento().entrySet()) {
            output.append(String.format("Host: %s%s", elemento.getKey(), System.lineSeparator()));
            for (Aresta aresta : elemento.getValue()) {
                output.append(String.format("  Conexao: %s%s", aresta, System.lineSeparator()));
            }
        }

        return output.toString();
    }
}