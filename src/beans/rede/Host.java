package beans.rede;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import beans.grafo.Vertice;
import beans.rede.Metrica.TipoDeMetrica;
import beans.rede.Mensagem.TipoDeMensagem;
import buscas.Dijkstra;
import main.Main;
import utilitarios.Utils;

public class Host extends Vertice implements Runnable {
	private double alcanceMaximo;

	// Atributos para descoberta de rede.
	// private Set<Host> vizinhos;
	private Map<Host, Boolean> vizinhos;
	// private HashMap<Host, List<Vertice>> roteamento;
	private Map<Host, List<Rota>> roteamento;
	private Rede topologia;
	private static MeioDeAcesso meioDeAcesso;

	public Host(String id, String rotulo, double alcanceMaximo, MeioDeAcesso meioDeAcesso) {
		super(id, rotulo);
		this.alcanceMaximo = alcanceMaximo;
		// this.vizinhos = new HashSet<Host>();
		this.vizinhos = new HashMap<Host, Boolean>();
		// this.roteamento = new HashMap<Host, List<Vertice>>();
		this.roteamento = new HashMap<Host, List<Rota>>();
		Host.meioDeAcesso = meioDeAcesso;

		// Inicializa a topologia conhecida contendo apenas o host atual.
		HashSet<Host> hosts = new HashSet<Host>();
		HashSet<Conexao> conexoes = new HashSet<Conexao>();
		hosts.add(this);
		this.topologia = new Rede(hosts, conexoes);

		System.out.println(String.format("Host %s foi criado.", this));
	}

	private void enviar(Mensagem mensagem) {
		Host.meioDeAcesso.enviar(mensagem);
	}

	private Mensagem receber() {
		try {
			return Host.meioDeAcesso.receber(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Conexao novaConexao(Host destino) {
		String[] IDs = new String[] { this.getId(), destino.getId() };
		Arrays.sort(IDs);
		String nomeConexao = IDs[0] + IDs[1];
		double distancia = meioDeAcesso.getDistancia(this, destino);
		return new Conexao(nomeConexao, nomeConexao, this, destino, distancia);
	}

	private void esquecer() {
		System.out.println(this + " Resetando memória");
		this.vizinhos.clear();
		this.topologia.limpar();

		// Inicializa a topologia com apenas o host atual.
		this.topologia.getHosts().add(this);
		this.topologia.getVertices().add(this);

		// Apaga a tabela de roteamento.
		this.roteamento.clear();
	}

	public double getAlcanceMaximo() {
		return alcanceMaximo;
	}

	public void calcularRoteamento() {
		this.roteamento.clear();
		if (this.topologia.getHosts().size() < 2)
			return;

		for (Host destino : this.topologia.getHosts()) {
			if (this.topologia.getHosts().contains(destino)) {
				List<Vertice> caminho = Dijkstra.getMenorCaminho(this.topologia, this, destino);
				Rota rota = new Rota(new Metrica(TipoDeMetrica.ADITIVA), caminho);

				if (this.roteamento.get(destino) == null) {
					List<Rota> rotas = new ArrayList<>();
					rotas.add(rota);
					this.roteamento.put(destino, rotas);
				} else {
					this.roteamento.get(destino).add(rota);
				}
			}

			// Mantem as rotas com menor métrica no começo da lista.
			// O roteamento sempre escolhe a rota de menor métrica.
			Collections.sort(this.roteamento.get(destino));
		}

	}

	public Map<Host, List<Rota>> getRoteamento() {
		return roteamento;
	}

	public Rede getTopologia() {
		return topologia;
	}

	public static void setMeioDeAcesso(MeioDeAcesso meioDeAcesso) {
		Host.meioDeAcesso = meioDeAcesso;
	}

	public static MeioDeAcesso getMeioDeAcesso() {
		return meioDeAcesso;
	}

	@Override
	public void run() {
		if (Main.DEBUG.get())
			System.out.println(String.format("Host %s foi ligado", this));

		while (true) {
			Mensagem descobrirVizinhos = new Mensagem(this, Rede.BROADCAST, TipoDeMensagem.HELLO, this.topologia);
			enviar(descobrirVizinhos);
			this.vizinhos.values().forEach(status -> status = false);

			if (Main.DEBUG.get())
				System.out.println(this + " [ENVIADA] " + descobrirVizinhos);

			for (Mensagem reply = receber(); reply != null; reply = receber()) {
				if (Main.DEBUG.get())
					System.out.println(this + " => Mensagem recebida: " + reply);

				switch (reply.getHeader()) {
					case HELLO:
						if (Main.DEBUG.get())
							System.out.println(this + " [CONSUMIR] " + reply);

						Host emissor = reply.getOrigem();
						// if (!this.vizinhos.containsKey(emissor)) {
							// Cria uma nova conexão com o emissor da mensagem.
							// Se a conexão já existir na topologia, a estrutura de dados
							// lida com a duplicata.
							Conexao conexao = novaConexao(emissor);
							this.topologia.getConexoes().add(conexao);
						// }

						// Identifica o emissor da mensagem como vizinho.
						this.vizinhos.put(emissor, true);

						this.topologia.unificar(reply.getContent());
						break;
					case TOPOLOGY_UPDATE:
						throw new UnsupportedOperationException();
				}
			}

			// Calcula a tabela de roteamento
			try {
				calcularRoteamento();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (Main.DEBUG.get())
				System.out.println(this + " dormindo por 30 segundos");
			Utils.sleep(30);
			// esquecer();
		}
	}
}
