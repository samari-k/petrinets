package petrinets.model.eg;

import java.util.HashMap;

/**
 * Die Klasse Knoten repräsentiert die Knoten des Erreichbarkeitsgraphen. Hier
 * wird mittels der aktuellen Markierung der Zustand des Petrinetzes
 * dargestellt.
 */
public class Knoten {

	private String markierung;
	/** Sammlung der Übergänge mit der Markierung des Nachfolgeknotens als key */
	private HashMap<String, Uebergang> uebergaenge;

	/**
	 * Im Konstruktor wird die Markierung des Knotens gesetzt und die
	 * Übergänge-Sammlung initialisiert. Ein neu erstellter Knoten at keine
	 * Nachfolger
	 * 
	 * @param m Markierung als String
	 */
	protected Knoten(String m) {
		markierung = new String(m);
		uebergaenge = new HashMap<String, Uebergang>();
	}

	/**
	 * Gibt die Markierung des Knotens zurück.
	 * 
	 * @return String der Markierung
	 */
	public String getMarkierung() {
		return markierung;
	}

	/**
	 * Fügt dem Knoten einen Übergang hinzu.
	 * 
	 * @param u Hinzuzufügender Übergang
	 */
	protected void addUebergang(Uebergang u) {
		uebergaenge.put(u.getFolgemarkierung(), u);
	}

	/**
	 * Gibt alle mit diesem Knoten assoziierten Übergänge zurück.
	 * 
	 * @return HashSet der Übergänge
	 */
	public HashMap<String, Uebergang> getUebergaenge() {
		return uebergaenge;
	}


}
