package testes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import beans.rede.Conexao;
import beans.rede.Host;

public class Rede {
    public static void main(String[] args) {
        HashSet<Conexao> conexoes = new HashSet<Conexao>();
        Host host1 = new Host("1", "1", 100, null);
        Host host2 = new Host("2", "2", 100, null);
        Host host3 = new Host("3", "3", 100, null);

        String nomeConexao = host1.getId() + "--" + host2.getId();
        Conexao conexao1 = new Conexao(nomeConexao, nomeConexao, host1, host2);
        Conexao conexao2 = new Conexao(nomeConexao, nomeConexao, host1, host2);

        if (conexao1.equals(conexao2))
            System.out.println("YES");
        else
            System.out.println("NO");

        conexoes.add(conexao1);
        conexoes.add(conexao2);

        System.out.println(conexoes);
        System.out.println(conexao1.getId());
        System.out.println(conexao1.hashCode());
        System.out.println(conexao2.getId());
        System.out.println(conexao2.hashCode());

        Conexao direto = new Conexao(nomeConexao, nomeConexao, host1, host2);
        Conexao reverso = new Conexao(nomeConexao, nomeConexao, host2, host1);
        Conexao diferente = new Conexao(nomeConexao, nomeConexao, host1, host3);
        System.out.println();
        System.out.println("Direto  == Reverso: " + direto.equals(reverso));
        System.out.println("Reverso == Direto: " + reverso.equals(direto));
        System.out.println("Diferente == Direto: " + diferente.equals(direto));
        System.out.println("Diferente == Reverso: " + diferente.equals(reverso));

        System.out.println();
        List<String> ids = new ArrayList<>();
        ids.add("(1_1)");
        ids.add("(0_1)");
        Collections.sort(ids);
        System.out.println(ids);
        String[] ids2 = new String[] {"(2_4)", "(1_5)"};
        Arrays.sort(ids2);
        System.out.println(Arrays.toString(ids2));

        List<Host> hosts = new ArrayList<Host>();
        hosts.add(host1);
        hosts.add(host2);
        hosts.add(host3);

        System.out.println(hosts);

        HashMap<Host, List<Host>> roteamento = new HashMap<>();
        roteamento.put(host1, hosts);
        roteamento.put(host2, hosts);
        roteamento.put(host3, hosts);

        System.out.println(roteamento);
        for (Entry<Host, List<Host>> rota : roteamento.entrySet()) {
            System.out.println(rota.getKey() + " = " + rota.getValue());
        }
    }
}
