package beans.rede;

public class Metrica {

	public enum TipoDeMetrica {
		ADITIVA, MULTIPLICATIVA, CONCAVA;
	}

	private TipoDeMetrica tipo;
	private double valor;
	
	public Metrica(TipoDeMetrica tipo) {
		this.tipo = tipo;
	}
	
	public TipoDeMetrica getTipo() {
		return tipo;
	}
	
	public double getValor() {
		return valor;
	}
	
	public void setValor(double valor) {
		this.valor = valor;
	}
}