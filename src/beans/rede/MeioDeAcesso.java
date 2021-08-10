package beans.rede;

import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

import beans.grafo.Vertice;
import utilitarios.GUI;

public class MeioDeAcesso {
    private static GUI gui;
    private Set<Host> hosts;
    private HashMap<Host, HashMap<Host, Double>> distanciaEntreHosts;
    private ConcurrentHashMap<Host, LinkedTransferQueue<Mensagem>> canal;

    public MeioDeAcesso(Set<Host> hosts, GUI gui) {
        this.hosts = hosts;
        MeioDeAcesso.gui = gui;
        this.distanciaEntreHosts = new HashMap<Host, HashMap<Host, Double>>();
        this.canal = new ConcurrentHashMap<Host, LinkedTransferQueue<Mensagem>>();

        calcularDistanciaEntreHosts();
        inicializarCanais();
    }

    private void inicializarCanais() {
        for (Host host : hosts) {
            LinkedTransferQueue<Mensagem> fila = new LinkedTransferQueue<Mensagem>();
            this.canal.put(host, fila);
        }
    }

    public void calcularDistanciaEntreHosts() {
        try {
            for (Host origem : this.hosts) {
                for (Host destino : this.hosts) {
                    if (origem.equals(destino))
                        continue;
    
                    if (this.distanciaEntreHosts.get(origem) == null) {
                        this.distanciaEntreHosts.put(origem, new HashMap<Host, Double>());
                    }
    
                    double distancia = gui.getNodeDistance(origem, destino);
                    this.distanciaEntreHosts.get(origem).put(destino, distancia);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica se um dado host é vizinho de origem. Um vizinho é aquele host a 1
     * salto de distância, capaz de receber mensagens diretamente de origem.
     * 
     * @param origem
     * @param destino
     * @return True se é vizinho. False caso contrário.
     */
    public boolean isVizinho(Host origem, Host destino) {
        Double distancia = -1.0;
        synchronized(distancia){
            distancia = this.distanciaEntreHosts.get(origem).get(destino);
            // System.out.println(String.format("IsVizinho(%s, %s) = %b [%f]", origem, destino, distancia < origem.getAlcanceMaximo(), distancia));
        }
        return distancia < origem.getAlcanceMaximo();
    }

    public double getDistancia(Host origem, Host destino) {
    	Double distancia = -1.0;
        synchronized(distancia){
            distancia = this.distanciaEntreHosts.get(origem).get(destino);
        }
        
        return distancia;
    }
    
    /**
     * 
     * @param mensagem
     */
    public void enviar(Mensagem mensagem) {
        Host origem, destino;
        HashMap<Host, Double> vizinhos;
        synchronized(mensagem) {
            origem = mensagem.getOrigem();
            destino = mensagem.getDestino();
            vizinhos = this.distanciaEntreHosts.get(origem);
        }

        if (destino.equals(Rede.BROADCAST)) {
            // Verifica a distância entre o host origem e todos os outros hosts
            // conhecidos. Se a distância entre eles for menor que o alcance máximo
            // do host de origem, a mensagem é entregue de maneira não bloqueante.
            for (Host vizinho : vizinhos.keySet()) {
                if (isVizinho(origem, vizinho)) {
                    this.canal.get(vizinho).offer(mensagem);
                    // System.out.println("[BROADCAST] Mensagem Entregue: " + mensagem);
                }
            }
        } else if (isVizinho(origem, destino)) {
            this.canal.get(destino).offer(mensagem);
            System.out.println("[DIRECT] Mensagem Entregue: " + mensagem);
        } else {
            System.out.println("Mensagem Não Entregue: " + mensagem);
        }

        // printCanal();
    }

    /**
     * Retorna a próxima mensagem disponível. Aguarda um período de 10 segundos
     * antes de abortar a espera por mensagens.
     * 
     * @param receptor Host a qual a mensagem deve ser entregue.
     * @return Mensagem, se alguma for recebida antes do timeout. NULL caso
     *         contrário.
     * @throws InterruptedException
     */
    public Mensagem receber(Host receptor) throws InterruptedException {
        return this.canal.get(receptor).poll(10, TimeUnit.SECONDS);
    }
}
