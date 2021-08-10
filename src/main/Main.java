package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import beans.rede.Host;
import beans.rede.MeioDeAcesso;
import utilitarios.Comandos;
import utilitarios.GUI;
import utilitarios.Utils;

public class Main {
	public static AtomicBoolean DEBUG = new AtomicBoolean(false);

	public static void main(String[] args) {
		GUI gui = new GUI();
		int numeroDeHosts = gui.getRede().getHosts().size();
		MeioDeAcesso meioDeAcesso = new MeioDeAcesso(gui.getRede().getHosts(), gui);
		Host.setMeioDeAcesso(meioDeAcesso);

		// Uma thread para cada host e uma thread para mostrar o raio de alcance de cada
		// host.
		ExecutorService executor = Executors.newFixedThreadPool(numeroDeHosts + 1);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		executor.submit(showRaioDeComunicacao(gui));
		scheduler.scheduleWithFixedDelay(atulizarDistanciaEntreHosts(gui, meioDeAcesso), 500, 500,
				TimeUnit.MILLISECONDS);

		Utils.sleep(1);
		for (Host host : gui.getRede().getHosts())
			executor.submit(host);

		Comandos interpretador = new Comandos(gui, meioDeAcesso);
		while (true) {
			try {
				String comando = Utils.IOString(">> ");
				interpretador.processar(comando);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static Runnable showRaioDeComunicacao(GUI gui) {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						gui.refresh();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public static Runnable atulizarDistanciaEntreHosts(GUI gui, MeioDeAcesso meioDeAcesso) {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						gui.getPipe().blockingPump();
					} catch (Exception e) {
						e.printStackTrace();
					}

					meioDeAcesso.calcularDistanciaEntreHosts();
				}
			}
		};
	}
}
