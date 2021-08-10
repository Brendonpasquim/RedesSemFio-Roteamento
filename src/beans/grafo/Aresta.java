package beans.grafo;

public class Aresta {
    private String id;
    private String rotulo;
    protected double peso;
    private boolean isDirecionado;
    private Vertice origem;
    private Vertice destino;

    public Aresta(double peso, Vertice origem, Vertice destino) {
        this(String.valueOf(peso), peso, false, origem, destino);
    }

    public Aresta(String id, String rotulo, Vertice origem, Vertice destino) {
        this(rotulo, 1, false, origem, destino);
        this.id = id;
    }

    public Aresta(String rotulo, double peso, boolean isDirecionado, Vertice origem, Vertice destino) {
        this.rotulo = rotulo;
        this.peso = peso;
        this.isDirecionado = isDirecionado;
        this.origem = origem;
        this.destino = destino;
    }

    public String getId() {
        return id;
    }

    public String getRotulo() {
        return rotulo;
    }

    public double getPeso() {
        return peso;
    }

    public boolean isDirecionado() {
        return isDirecionado;
    }

    public Vertice getOrigem() {
        return origem;
    }

    public Vertice getDestino() {
        return destino;
    }

    public boolean isExtremo(Vertice extremo) {
        return extremo.equals(this.origem) || extremo.equals(this.destino);
    }

    public Vertice getOutroExtremo(Vertice extremo) {
        if (!isExtremo(extremo))
            return null;
        else if (this.origem.equals(extremo))
            return this.destino;
        else
            return this.origem;
    }

    @Override
    public String toString() {
        if (this.isDirecionado) {
            return String.format("%s --[%f]-> %s", this.origem, this.peso, this.destino);
        } else {
            return String.format("%s --[%f]-- %s", this.origem, this.peso, this.destino);
        }
    }
}
