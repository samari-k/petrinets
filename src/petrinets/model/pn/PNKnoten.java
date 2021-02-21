package petrinets.model.pn;

import java.util.HashMap;

/**
 * PNKnoten ist eine abstrakte Klasse, die die Grundlagen von Knotenelementen
 * in Petrinetzen (Stellen und Transitionen) formalisiert. Sie definiert
 * ID, Name, Position, Vorgänger und Nachfolger der Knoten.
 */
public abstract class PNKnoten {

	private final String id;
	private String name;
	private int[] position;
	private HashMap<Kante,PNKnoten> vorgaenger;
	private HashMap<Kante,PNKnoten> nachfolger;
	
	/**
	 * Der Konstruktor erstellt einen eindeutigen Knoten 
	 * und initialisiert Sammlungen für die Vorgänger und Nachfolger des Knotens
	 * @param id	Die eindeutige ID des Knotens
	 */
	protected PNKnoten(String id) {
		this.id = id;
		vorgaenger = new HashMap<Kante,PNKnoten>();
		nachfolger = new HashMap<Kante,PNKnoten>();
	}
	

	/**
	 * Setzt den Namen des Elements.
	 * @param name Name des Elements
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt die eindeutige ID des Knotens zurück.
	 * @return String mit der ID des Knotens.
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gibt den Namen des Knotens zurück.
	 * @return String mit dem Namen des Knotens.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Setzt die Position des Knotens.
	 * @param x Position auf der x-Achse
	 * @param y Position auf der y-Achse
	 */
	protected void setPosition(int x, int y) {
		position = new int[] {x,y};
	}

	/**
	 * Gibt die Position des Knotens zurück.
	 * @return	int[] Position des Knotens in der Form [x,y]
	 */
	public int[] getPosition() {
		return position;
	}
	
	/**
	 * Fügt einen Vorgänger zur PNKnoten hinzu.
	 * @param k Kante zum Vorgänger
	 * @param n Vorgänger-Knoten
	 */
	protected void addVorgaenger(Kante k, PNKnoten n) {
		vorgaenger.put(k,n);
	}
	
	/**
	 * Fügt einen Nachfolger zur PNKnoten hinzu.
	 * @param k Kante zum Nachfolger
	 * @param n Nachfolger-Knoten
	 */
	protected void addNachfolger(Kante k, PNKnoten n) {
		nachfolger.put(k, n);
	}
	
	/**
	 * Gibt die Sammlung der Vorgänger zurück
	 * @return HashMap der Vorgänfger
	 */
	protected HashMap<Kante,PNKnoten> getVorgaenger(){
		return vorgaenger;
	}
	
	/**
	 * Gibt die Sammlung der Nachfolger unterwegs
	 * @return HashMap der Nachfolger
	 */
	protected HashMap<Kante,PNKnoten> getNachfolger(){
		return nachfolger;
	}
	
}
