package beans.grafo;

public class Vertice implements Comparable<Vertice> {
	private String id;
	private String rotulo;

	// Atributos para busca em grafo.
	private double distancia;
	private boolean visitado;
	private Vertice antecessor;

	public Vertice(String rotulo) {
		this.rotulo = rotulo;
	}

	public Vertice(String id, String rotulo) {
		this(rotulo);
		this.id = id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRotulo(String rotulo) {
		this.rotulo = rotulo;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}

	public void setAntecessor(Vertice antecessor) {
		this.antecessor = antecessor;
	}

	public String getId() {
		return id;
	}

	public String getRotulo() {
		return rotulo;
	}

	public double getDistancia() {
		return distancia;
	}

	public boolean isVisitado() {
		return visitado;
	}

	public Vertice getAntecessor() {
		return antecessor;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Vertice)) {
			return false;
		}

		Vertice temp = (Vertice) obj;
		return temp.rotulo.equals(this.rotulo);
	}

	@Override
	public int hashCode() {
		return this.rotulo.hashCode();
	}

	@Override
	public String toString() {
		return String.format("(%s)", this.rotulo);
	}

	@Override
	public int compareTo(Vertice vertice) {
		if (this.getDistancia() < vertice.getDistancia())
			return -1;
		else if (this.getDistancia() == vertice.getDistancia())
			return 0;
		else
			return 1;
	}
}
