package beans.rede;

import beans.grafo.Aresta;

public class Conexao extends Aresta {

    public Conexao(String id, String rotulo, Host origem, Host destino) {
        super(id, rotulo, origem, destino);
    }
    
    public Conexao(String id, String rotulo, Host origem, Host destino, double peso) {
    	super(id, rotulo, origem, destino);
    	super.peso = peso;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (!(obj instanceof Conexao))
            return false;

        Conexao temp = (Conexao) obj;
        if (isDirecionado()){
            return this.getOrigem().equals(temp.getOrigem()) && this.getDestino().equals(temp.getDestino());
        } else {
            boolean origem = this.getOrigem().equals(temp.getOrigem()) || this.getOrigem().equals(temp.getDestino());
            boolean destino = this.getDestino().equals(temp.getOrigem()) || this.getDestino().equals(temp.getDestino());
            return origem && destino;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
