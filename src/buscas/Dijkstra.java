package buscas;

import java.util.List;

import beans.grafo.Aresta;
import beans.grafo.Grafo;
import beans.grafo.Vertice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Dijkstra {
	public static synchronized List<Vertice> getMenorCaminho(Grafo grafo, Vertice origem, Vertice destino) {
		if (!grafo.getVertices().contains(origem))
			throw new IllegalArgumentException(String.format("O v�rtice de origem '%s' n�o existe no grafo.", origem));
		else if (!grafo.getVertices().contains(destino))
			throw new IllegalArgumentException(String.format("O v�rtice de destino '%s' n�o existe no grafo.", destino));
		else if (grafo.getMapeamento() == null || grafo.getMapeamento().isEmpty())
			throw new IllegalArgumentException("Mapeamento de v�rtices � nulo.");
		else if (grafo.getMapeamento().get(origem).isEmpty())
			throw new IllegalArgumentException(String.format("O v�rtice de origem '%s' tem grau zero.", origem));
		else if (grafo.getMapeamento().get(destino).isEmpty())
			throw new IllegalArgumentException(String.format("O v�rtice de origem '%s' tem grau zero.", destino));
		
		List<Vertice> menorCaminho = new ArrayList<Vertice>();
		List<Vertice> naoVisitados = new ArrayList<Vertice>();

		// Adiciona todos os vertices na lista de n�o visitados.
		naoVisitados.addAll(grafo.getVertices());

		// Inicializa todos os vertices com distancia = MAX, visitado = FALSE e
		// antecessor = NULL (N�o tem);
		for (Vertice vertice : grafo.getVertices()) {
			vertice.setAntecessor(null);
			vertice.setVisitado(false);
			if (vertice.equals(origem))
				vertice.setDistancia(0);
			else
				vertice.setDistancia(Double.POSITIVE_INFINITY / 2);
		}

		// Vertices com a menor distancia s�o colocados no in�cio.
		Collections.sort(naoVisitados);

		// Para cada vertice não visitado, se relaxa as arestas conectadas.
		while (!naoVisitados.isEmpty()) {
			// O fato da lista estar sempre ordenada garante que o primeiro elemento
			// sempre é o de menor distância.
			Vertice atual = naoVisitados.get(0);
			// System.out.println("[Vertice] => " + atual);

			// Visita-se todos os vertices adjacentes (vizinhos) e se recalcula a dist�ncia
			// do caminho deste vertice ao vertice origem (relaxamento de aresta).
			HashSet<Aresta> arestas = grafo.getMapeamento().get(atual);
			if (arestas != null) {
				for (Aresta aresta : arestas) {
					Vertice vizinho = aresta.getOutroExtremo(atual);
					// System.out.println("	[Vizinho] => " + vizinho);
	
					if (vizinho.isVisitado())
						continue;
	
					// Se a atual distancia at� o vizinho � maior do que a dist�ncia do vertice
					// atual mais o peso da aresta, a distancia para o vizinho � atualizada.
					if (vizinho.getDistancia() > atual.getDistancia() + aresta.getPeso()) {
						vizinho.setDistancia(atual.getDistancia() + aresta.getPeso());
						vizinho.setAntecessor(atual);
					}
				}
			}

			atual.setVisitado(true);
			naoVisitados.remove(atual);

			// Reordena a lista, colocando as menores distancias no inicio,
			// após as arestas terem sido relaxadas.
			Collections.sort(naoVisitados);
		}

		Vertice atual = destino;
		while (atual != origem) {
			menorCaminho.add(atual);
			atual = atual.getAntecessor();
		}
		menorCaminho.add(origem);
		Collections.sort(menorCaminho);

		return menorCaminho;
	}
}
