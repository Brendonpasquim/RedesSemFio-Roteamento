package utilitarios;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;

import beans.rede.Conexao;
import beans.rede.Host;
import beans.rede.MeioDeAcesso;
import beans.rede.Rota;
import main.Main;

public class Comandos {
    private final String[] acoes = new String[] { "ADD", "GET", "REMOVE", "SHOW" };
    private final String[] alvos = new String[] { "EDGE", "MESSAGE", "NODE" };

    private GUI gui;
    private MeioDeAcesso meio;

    public Comandos(GUI gui, MeioDeAcesso meio) {
        this.gui = gui;
        this.meio = meio;
    }

    public boolean processar(String entrada) {

        String[] partes = validar(entrada);
        if (partes == null)
            return false;

        String acao = partes[0];
        String categoria = partes[1];

        if (acao.equals("ADD")) {
            add(partes);
        } else if (acao.equals("GET")) {
            get(categoria, partes[2]);
        } else if (acao.equals("REMOVE")) {
            remove(categoria, partes[2]);
        } else if (acao.equals("SHOW")) {
            show(categoria);
        }

        return true;
    }

    private String[] validar(String entrada) {
        if (entrada == null || entrada.isBlank())
            return null;

        entrada = entrada.toUpperCase();
        String[] partes = entrada.split("\s");
        if (partes.length < 2) {
            System.err.format("Comando '%s' não é válido.%s", Arrays.toString(partes), System.lineSeparator());
            return null;
        }

        String acao = partes[0];
        String alvo = partes[1];

        int indiceAcao = Arrays.binarySearch(acoes, acao);
        if (indiceAcao < 0) {
            System.err.format("Ação '%s' não é válida.%s", acao, System.lineSeparator());
            return null;
        }

        int indiceAlvo = Arrays.binarySearch(alvos, alvo);
        if (indiceAlvo < 0) {
            System.err.format("Alvo '%s' não é válido.%s", acao, System.lineSeparator());
            return null;
        }

        return partes;
    }

    private void add(String... alvos) {
        if (alvos[1].equals("NODE")) {
            Node node = this.gui.getGraph().addNode(alvos[2]);
            node.setAttribute("xyz", 1, 1, 0);
            Host host = new Host(alvos[2], alvos[2], 100, meio);
            this.gui.getRede().getHosts().add(host);
        } else if (alvos[1].equals("EDGE")) {
            if (alvos.length < 4) {
                System.err.format("Comando 'ADD EDGE' requer dois parâmetros.%s", System.lineSeparator());
                return;
            }
            var node1 = this.gui.getGraph().getNode(alvos[2]);
            var node2 = this.gui.getGraph().getNode(alvos[3]);
            String id = node1.getId() + "--" + node2.getId();
            this.gui.getGraph().addEdge(id, node1, node2);
            Host origem = this.gui.getRede().getHosts().stream().filter(host -> host.getId().equals(alvos[2]))
                    .findFirst().get();
            Host destino = this.gui.getRede().getHosts().stream().filter(host -> host.getId().equals(alvos[3]))
                    .findFirst().get();
            Conexao conexao = new Conexao(id, id, origem, destino);
            this.gui.getRede().getConexoes().add(conexao);
        }
    }

    private void get(String categoria, String alvo) {
        if (categoria.equals("NODE")) {
            Host target = this.gui.getRede().getHosts().stream().filter(host -> host.getId().equals(alvo)).findFirst()
                    .get();

            System.out.println();
            System.out.println("===== TOPOLOGIA =====");
            System.out.println(target.getTopologia());
            System.out.println("===== ROTEAMENTO =====");
            for (Entry<Host, List<Rota>> rota : target.getRoteamento().entrySet()) {
                System.out.println(rota.getKey() + " = " + rota.getValue().get(0));
            }
        } else if (categoria.equals("EDGE")) {
            Conexao target = this.gui.getRede().getConexoes().stream().filter(conexao -> conexao.getId().equals(alvo))
                    .findFirst().get();
            System.out.println(target);
        }
    }

    private void remove(String categoria, String alvo) {
        if (categoria.equals("NODE")) {
            try {
                this.gui.getGraph().removeNode(alvo);
                Host host = this.gui.getRede().getHosts().stream().filter(h -> h.getId().equals(alvo)).findFirst()
                        .get();
                if (host != null)
                    this.gui.getRede().getHosts().remove(host);
            } catch (ElementNotFoundException e) {
                System.out.format("Node '%s' não encontrado.%s", alvo, System.lineSeparator());
            }
        } else if (categoria.equals("EDGE")) {
            try {
                this.gui.getGraph().removeEdge(alvo);
            } catch (ElementNotFoundException e) {
                System.out.format("Edge '%s' não encontrado.%s", alvo, System.lineSeparator());
            }
        }
    }

    private void show(String categoria) {
        if (categoria.equals("NODE")) {
            System.out.println(this.gui.getRede().getHosts());
        } else if (categoria.equals("EDGE")) {
            System.out.println(this.gui.getRede().getConexoes());
        } else if (categoria.equals("MESSAGE")) {
            Main.DEBUG.set(!Main.DEBUG.get());
        }
    }
}