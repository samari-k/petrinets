package petrinets.model.eg;

import java.util.HashMap;
import java.util.Observable;

import petrinets.controller.Controller;

/**
 * Die Klasse Erreichbarkeitsgraph bildet den zentralen Container für die Knoten
 * und Übergänge des Erreichbarkeitsgraphen zu einem Petrinetz. Diese Klasse erweitert
 * die Klasse Observable, um ihr View-Äquivalent ohne großen Aufwand über Änderungen
 * zu informieren.
 */
public class Erreichbarkeitsgraph extends Observable{

	/**
	 * Sammlung aller Knoten des EG mit ihren Markierungen als eindeutigem key
	 */
	private HashMap<String, Knoten> knoten;

	/**
	 * Sammlung aller Übergänge des EG mit einem String als eindeutigen key,
	 * dieser setzt sich zusammen aus Vorgänger-Markierung, schaltende
	 *  Transition und Nachfolgemarkierung
	 */
	private HashMap<String, Uebergang> uebergaenge;
	
	private String anfangsknotenID;
	private String aktuellerKnoten;
	private String folgeKnoten;

	/**
	 * Im Konstruktor werden Die Knoten- und Übergangs-Sammlungen initialisiert und
	 * der Starknoten (die Anfangsmarkierung des Petrinetzes) gesetzt.
	 * Der Konstruktor wird bei der Erzeugung eines Petrinetzes aufgerufen.
	 * 
	 * @param anfangsmarkierung Die Anfangsmarkierung des zum Erreichbarkeitsgraphen
	 * 							zugehörigen Petrinetzes
	 */
	public Erreichbarkeitsgraph(String anfangsmarkierung) {
		knoten = new HashMap<String, Knoten>();
		knoten.put(anfangsmarkierung,new Knoten(anfangsmarkierung));
		uebergaenge = new HashMap<String, Uebergang>();
		anfangsknotenID = anfangsmarkierung;
		
		// Changed-Flag für die Observer setzen
		setChanged();
	}

	/**
	 * Gibt die ID des Anfangsknotens zurück.
	 * @return ID des Anfangsknotens
	 */
	public String getAnfangsknotenID() {
		return anfangsknotenID;
	}
	

	/**
	 * Überprüft die Existenz eines Übergangs und erstellt bei Bedarf
	 * einen neuen.
	 * 
	 * @param markierung Markierung des Vorgängerknotens
	 * @param folgemarkierung Markierung des Folgeknotens
	 * @param transition die ID der den Übergang schaltenden Transition
	 */
	public void addUebergang(String markierung, String folgemarkierung, String transition) {
		Knoten vor = knoten.get(markierung);
		
		// überprüfen, ob der neue Folgenoten schon existiert
		if(knoten.containsKey(folgemarkierung)) {
			
			// Knoten existiert:
			// überprüfen, ob Übergang schon existiert
			Knoten nach = knoten.get(folgemarkierung);
			
			String uebergangKey = markierung+transition+folgemarkierung;
			if (!uebergaenge.containsKey(uebergangKey)) {
				
				// Übergang existiert noch nicht:
				// Übergang zu existentem Knoten erstellen
				Uebergang u = new Uebergang(vor, nach, transition);
				vor.addUebergang(u);
				uebergaenge.put(uebergangKey, u);
				
				// Changed Flag setzen
				setChanged();
			}
			
			// Übergang existiert:
			// nichts tun.
		}
		else {
			// Knoten existiert nicht:
			// Übergang zu neuem Knoten erstellen
			Knoten nach = new Knoten(folgemarkierung);
			Uebergang u = new Uebergang(vor, nach, transition);
			vor.addUebergang(u);
			String uebergangKey = markierung+transition+folgemarkierung;
			uebergaenge.put(uebergangKey,u);
			knoten.put(folgemarkierung, nach);
			
			// Changed Flag setzen
			setChanged();
		}	
		
		// Aktuelle und Folgemarkierung speichern
		aktuellerKnoten = markierung;
		folgeKnoten = folgemarkierung;
		
		// Observer über Änderung benachrichtigen
		notifyObservers();
	}

	/**
	 * Wird beim Schalten vom {@link Controller} aufgerufen.
	 * Gibt den aktuellen (zuletzt geschalteten) Knoten zurück.
	 * @return aktueller Knoten
	 */
	public String getAktuellerKnoten() {
		return aktuellerKnoten;
	}
	
	/**
	 * Wird beim Schalten vom {@link Controller} aufgerufen.
	 * Gibt den nach der Schaltung aktuellen Knoten zurück.
	 * @return Folgeknoten nach der Schaltung
	 */
	public String getFolgeKnoten() {
		return folgeKnoten;
	}
	

	/**
	 * Gibt die Knotensammlung zurück.
	 * 
	 * @return Sammlung aller Knoten des EG
	 */
	public HashMap<String, Knoten> getAlleKnoten() {
		return knoten;
	}

	/**
	 * Gibt die Übergängesammlung zurück.
	 * 
	 * @return Sammlung aller Übergänge des EG
	 */
	public HashMap<String, Uebergang> getAlleUebergaenge() {
		return uebergaenge;
	}
	
	/**
	 * Gibt den Übergang mit dem entsprechenden key zurück.
	 * @param key des gesuchten Übergangs
	 * @return Übergang mit dem übergebenen key
	 */
	public Uebergang getUebergang(String key) {
		return uebergaenge.get(key);
	}
	
	/**
	 * Setzt den EG auf die übergeben neue Anfangsmarkierung zurück.
	 * der EG hat anschließend wieder nur einen Knoten.
	 * @param anfangsmarkierung neue Anfangsmarkierung
	 */
	public void reset(String anfangsmarkierung) {
		knoten = new HashMap<String, Knoten>();
		knoten.put(anfangsmarkierung,new Knoten(anfangsmarkierung));
		uebergaenge = new HashMap<String, Uebergang>();
		anfangsknotenID = anfangsmarkierung;
		
		// Observer benachrichtigen
		setChanged();
		notifyObservers("clear");
	}

	/**
	 * Gibt die Anzahl der Knoten des EG zurück
	 * @return Anzahl der Knoten des EG
	 */
	public int getAnzahlKnoten() {
		return knoten.size();
	}
	
	/**
	 * Gibt die Anzahl der Übergänge des EG zurück
	 * @return Anzahl der Übergänge
	 */
	public int getAnzahlUebergaenge() {
		return uebergaenge.size();
	}

}
