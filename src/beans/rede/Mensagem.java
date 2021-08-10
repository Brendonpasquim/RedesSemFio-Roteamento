package beans.rede;

public class Mensagem {
	public enum TipoDeMensagem {
        HELLO, TOPOLOGY_UPDATE
    }
	
    private Host origem;
    private Host destino;
    private final TipoDeMensagem header;
    private final Rede content;


    public Mensagem(Host origem, Host destino, TipoDeMensagem conteudo, Rede content) {
        this.origem = origem;
        this.destino = destino;
        this.header = conteudo;
        this.content = content;
    }

    public Host getOrigem() {
        return origem;
    }

    public Host getDestino() {
        return destino;
    }

    public TipoDeMensagem getHeader() {
        return header;
    }

    public Rede getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("%s =[%s]=> %s", this.origem, this.header, this.destino);
    }
}
