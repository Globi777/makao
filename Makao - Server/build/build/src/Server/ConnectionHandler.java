package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//Klasa watku obslugujacego polaczenie z klientem
public class ConnectionHandler extends Thread {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private int playerNumber;
	private int numberOfCards = 5;
	private boolean myTurn = true;
	
	public static synchronized void updatePlayers() {
		Server.getPlayers().clear();
		Server.getPlayers().appendText("Number of players: " + Integer.toString(Listener.numberOfPlayers));
	}
	
	public BufferedReader getIn() {
		return this.in;
	}
	
	public PrintWriter getOut() {
		return this.out;
	}
	
	public int getNumberOfCards() {
		return this.numberOfCards;
	}
	
	public void setNumberOfCards(int i) {
		this.numberOfCards = i;
	}
	
	public int getPlayerNumber() {
		return this.playerNumber;
	}
	
	public boolean getMyTurn() {
		return this.myTurn;
	}
	
	public ConnectionHandler(Socket socket) throws IOException {
		Listener.players.add(this);
		this.playerNumber = Listener.numberOfPlayers++;
		this.socket = socket;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(), true);
		
		if (Listener.numberOfPlayers == 2) {
			this.myTurn = false;
			Listener.deck = new ArrayList<String>(Server.getCards());
			long seed = System.nanoTime();
			Collections.shuffle(Listener.deck, new Random(seed));
			Listener.dealCards();
			Listener.getTopFromDeck();
			Listener.setTurn();
		}
		
		Server.getTextArea().appendText("Connection from: " + socket + "\n");
	}

	public void run() {
		try {
			updatePlayers();
			if (Listener.numberOfPlayers == 1) {
				out.println("WAIT");
				while (Listener.numberOfPlayers == 1) {
					out.println("CHECK");
					String response = in.readLine();
					if (response.startsWith("HALO")) {}		
					else if (response.startsWith("END"))
						break;				
					Thread.sleep(100);		
				}
			}
			if (Listener.numberOfPlayers > 2)
				out.println("TOOMANYPLAYERS");
			else if (Listener.numberOfPlayers == 2){
				out.println("OK");
				
				// Podanie ilosci kart przeciwnika
				if (this.playerNumber == 0) 
					out.println("OPPONENT-" + Integer.toString(Listener.players.get(1).getNumberOfCards()));
				else
					out.println("OPPONENT-" + Integer.toString(Listener.players.get(0).getNumberOfCards()));

				while (true) {	
					String command = in.readLine();
					if (command.startsWith("THROW")) {
						String[] parts = command.split("-");
						Listener.topCard = parts[1];

						if (this.playerNumber == 0) {
							Listener.players.get(0).setNumberOfCards(Integer.parseInt(parts[2]));
							Listener.players.get(1).getOut().println("TOP-" + Listener.topCard);
							Listener.players.get(1).getOut().println("THROWOPPONENT-" + this.numberOfCards + "-" 
																	+ parts[3] + "-" + parts[4] + "-" + parts[5]);
							Listener.players.get(1).getOut().println("TURN");
						}
						else {
							Listener.players.get(1).setNumberOfCards(Integer.parseInt(parts[2]));
							Listener.players.get(0).getOut().println("TOP-" + Listener.topCard);
							Listener.players.get(0).getOut().println("THROWOPPONENT-" + this.numberOfCards + "-" 
																	+ parts[3] + "-" + parts[4] + "-" + parts[5]);
							Listener.players.get(0).getOut().println("TURN");
						}
					}
					else if (command.startsWith("DRAW")) {
						String[] parts = command.split("-");
						
						if (this.playerNumber == 0) {
							Listener.players.get(0).setNumberOfCards(Listener.players.get(0).getNumberOfCards() + 1);
							Listener.players.get(0).getOut().println("GETDRAW-" + Listener.getCardFromDeck());
							Listener.players.get(1).getOut().println("THROWOPPONENT-" + this.numberOfCards + "-" 
																	+ parts[1] + "-" + parts[2] + "-" + parts[3]);
						}
						else {
							Listener.players.get(1).getOut().println("GETDRAW-" + Listener.getCardFromDeck());
							Listener.players.get(1).setNumberOfCards(Listener.players.get(1).getNumberOfCards() + 1);
							Listener.players.get(0).getOut().println("THROWOPPONENT-" + this.numberOfCards + "-" 
																	+ parts[1] + "-" + parts[2] + "-" + parts[3]);
						}
					}
					else if (command.startsWith("TURN")) {
						if (this.playerNumber == 0) {
							Listener.players.get(1).getOut().println("TURN");
						}
						else {
							Listener.players.get(0).getOut().println("TURN");
						}
					}
					else if (command.startsWith("WIN")) {
						if (this.playerNumber == 0) 
							Listener.players.get(1).getOut().println("DEFEAT");						
						else 
							Listener.players.get(0).getOut().println("DEFEAT");
						break;
					}
					else if (command.startsWith("END")) {
						if (this.playerNumber == 0) 
							Listener.players.get(1).getOut().println("END");						
						else 
							Listener.players.get(0).getOut().println("END");
						break;
					}
					else if (command.startsWith("SECONDEND")) {
						break;
					}
				}
			}
		} catch (IOException e) {
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				out.close();
			}
			try {
				Listener.numberOfPlayers--;
				updatePlayers();
				Listener.players.clear();
				Server.getTextArea().appendText("Disconnection from: " + socket + "\n");
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
