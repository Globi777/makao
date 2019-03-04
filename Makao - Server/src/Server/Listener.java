package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

//Klasa nasluchujaca klientow Makao
public class Listener extends Thread {
	private static final int port = 9002;
	private static ServerSocket listener = null;
	private volatile static boolean canceled;
	public static volatile ArrayList<String> deck;
	public static ArrayList<ConnectionHandler> players = new ArrayList<ConnectionHandler>();
	public static int numberOfPlayers = 0;
	public static int numberOfCardsToDraw = 1;
	public static String topCard;
	
	//FOR SCIENCE :)
	@SuppressWarnings("unused")
	private static void threads() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		for (Thread i : threadArray)
			Server.getTextArea().appendText(i.getName() + "\n");
	}

	private static void startServer() throws ClassNotFoundException {
		try {
			listener = new ServerSocket(port);
			try {
				Server.getTextArea().appendText("The server is running.\n");
				while (!canceled || (listener != null)) {
					new ConnectionHandler(listener.accept()).start();
				}
			} finally {
				listener.close();
			}
		} catch (IOException e) {
			return;
		}
	}

	public static void stopServer() {
		try {
			listener.close();
			canceled = true;
			Server.getTextArea().appendText("The server has stopped.\n");	
			players.clear();
		} catch (IOException e) {
			return;
		}
	}
	
	public static synchronized String getCardFromDeck() {
		if (deck.size() == 0) {
			Listener.deck = new ArrayList<String>(Server.getCards());
			long seed = System.nanoTime();
			Collections.shuffle(Listener.deck, new Random(seed));
		}
		int index = deck.size() - 1;
		String card = deck.get(index);
		deck.remove(index);
		return card;
	}
	
	public static synchronized void getTopFromDeck() {
		if (deck.size() == 0) {
			Listener.deck = new ArrayList<String>(Server.getCards());
			long seed = System.nanoTime();
			Collections.shuffle(Listener.deck, new Random(seed));
		}
		int index = deck.size() - 1;
		while (deck.get(index).startsWith("2") || deck.get(index).startsWith("3") || deck.get(index).startsWith("4") ||
			   deck.get(index).startsWith("walet") || deck.get(index).startsWith("dama") || deck.get(index).startsWith("krol") ||
			   deck.get(index).startsWith("as")) {
			index--;
		}		
		topCard = deck.get(index);
		deck.remove(index);
		players.get(0).getOut().println("FIRSTTOP-" + topCard);
		players.get(1).getOut().println("FIRSTTOP-" + topCard);
	}
	
	public static void dealCards() {
		String dealPlayer1 = "DEAL";
		String dealPlayer2 = "DEAL";
		for (int i = 0; i < 5; i++) {
			dealPlayer1 = dealPlayer1 + "-" + getCardFromDeck();
			dealPlayer2 = dealPlayer2 + "-" + getCardFromDeck();
		}
			players.get(0).getOut().println(dealPlayer1);
			players.get(1).getOut().println(dealPlayer2);
	}
	
	public static void setTurn() {
			players.get(0).getOut().println("FIRSTTURN-" + players.get(0).getMyTurn());
			players.get(1).getOut().println("FIRSTTURN-" + players.get(1).getMyTurn());
	}

	public void run() {
		try {
			startServer();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
