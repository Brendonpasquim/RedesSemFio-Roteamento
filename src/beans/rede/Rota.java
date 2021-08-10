package beans.rede;

import java.util.List;

import beans.grafo.Vertice;

public class Rota implements Comparable<Rota> {
	private Metrica metrica;
	private List<Vertice> hops;

	public Rota(Metrica metrica, List<Vertice> hops) {
		this.metrica = metrica;
		this.hops = hops;

		calcularMetrica();
	}

	public Metrica getMetrica() {
		return metrica;
	}

	public List<Vertice> getHops() {
		return hops;
	}

	private void calcularMetrica() {
		double acumulado = 0;
		double atual = 0;

		switch (this.metrica.getTipo()) {
		case ADITIVA:
			Vertice destino = this.hops.get(this.hops.size() - 1);
			acumulado = destino.getDistancia();
			break;
		case MULTIPLICATIVA:
			acumulado *= atual;
			break;
		case CONCAVA:
			acumulado = Double.min(atual, acumulado);
			break;
		}

		this.metrica.setValor(acumulado);
	}

	@Override
	public String toString() {
		return String.format("%s [%f] %s", this.metrica.getTipo(), this.metrica.getValor(), this.hops);
	}

	@Override
	public int compareTo(Rota o) {
		if (this.metrica.getValor() > o.getMetrica().getValor()) {
			return 1;
		} else if (this.metrica.getValor() == o.getMetrica().getValor()) {
			return 0;
		} else {
			return -1;
		}
	}
}