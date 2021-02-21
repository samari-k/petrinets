package petrinets.model.pn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;

import petrinets.model.eg.Erreichbarkeitsgraph;

/**
 * Hier bündeln sich alle Informationen und Funktionen des Petrinetzes. Ein
 * Petrinetz-Objekt fungiert als Container für alle zugehörigen Elemente
 * (Stellen, Transitionen und Kanten) des Netzes.
 * 
 * Im Petrinetz wird auch der zugehörige Erreichbarkeitsgraph initialisiert.
 * 
 * Diese Klasse erweitert die Klasse Observable, um ihr View-Äquivalent ohne 
 * großen Aufwand über Änderungen zu informieren.
 */
public class Petrinetz extends Observable {

	// Alle Elemente des Petrnietzes sind in HashMaps gesammelt,
	// mit dem String ihrer ID als eindeutigem key
	private HashMap<String, Stelle> stellen;
	private HashMap<String, Transition> transitionen;
	private HashMap<String, Kante> kanten;
	private HashMap<String, PNKnoten> pnKnoten;
	
	private ArrayList<String> aktivierteTransitionen;

	private String dateiname;

	/** zum Petrinnetz assoziierter Erreichbarkeitsgraph */
	private Erreichbarkeitsgraph eg;

	/**
	 * Im Konstruktor werden die Sammlungen der Basiselemente initialisiert und die
	 * übergebene Datei geparsed
	 */
	public Petrinetz(File pnml) {

		// initialisiere Sammlungen
		stellen = new HashMap<String, Stelle>();
		transitionen = new HashMap<String, Transition>();
		kanten = new HashMap<String, Kante>();
		pnKnoten = new HashMap<String, PNKnoten>();
		aktivierteTransitionen = new ArrayList<String>();

		// Überprüfen, ob die Datei existiert, wenn ja:
		// Dateiname aus Pfad auslesen und Datei parsen
		if (pnml.exists()) {
			setDateiname(pnml);
			parse(pnml);
		} else {
			System.err.println("Die Datei " + pnml.getAbsolutePath() + " wurde nicht gefunden!");
		}
		
		// Erreichbarkeitsgraph initialisieren
		eg = new Erreichbarkeitsgraph(getMarkierung());

		// Changed-Flag für die Observer setzen
		setChanged();
	}

	/**
	 * Die zu parsende Datei wird vom Controller durchgereicht. Hier erstellt sich
	 * das Petrinetz einen Parser, welcher das Netz mit dem Inhalt der Datei füllt.
	 * Anschließend werden die Kanten verbunden, die aktivierten Transitionen als
	 * solche gekennzeichnet und der zugehörige EG initialisiert.
	 * 
	 * @param pnml zu parsende Datei
	 */
	private void parse(File pnml) {
		PNMLParser parser = new PNMLParser(pnml, this);
		// Parser ausführen
		parser.initParser();
		parser.parse();

		// Kanten verbinden
		verbindeKanten();

		// überprüfen, welche Transitionen als aktiviert gekennzeichnet werden
		aktiviereTransitionen();
	}

	/**
	 * Liest den Dateiname aus dem Dateipfad und speichert ihn.
	 * 
	 * @param datei Pfad zur letzten geladenen Datei
	 */
	private void setDateiname(File datei) {
		String d = datei.toString();
		dateiname = d.substring(d.lastIndexOf("/") + 1);
	}

	/**
	 * Gibt den Dateinamen zu diesem Petrinetz zurück
	 * 
	 * @return Dateiname 
	 */
	public String getDateiname() {
		return dateiname;
	}

	/**
	 * Fügt eine Stelle zur stellen-Collection hinzu.
	 * 
	 * @param sID ID der hinzuzufüngenden Stelle
	 */
	protected void addStelle(String sID) {
		stellen.put(sID, new Stelle(sID));
	}

	/**
	 * Fügt eine Transition zur transitionen-Collection hinzu.
	 * 
	 * @param tID ID der hinzuzufügenden Transition
	 */
	protected void addTransition(String tID) {
		transitionen.put(tID, new Transition(tID));
	}

	/**
	 * Fügt eine Kante zur Kanten-Sammlung hinzu.
	 * 
	 * @param kID     ID der hinzuzufügenden Kante
	 * @param quellID ID der Quellknoten
	 * @param zielID  ID der Zielknoten
	 */
	protected void addKante(String kID, String quellID, String zielID) {
		kanten.put(kID, new Kante(kID, quellID, zielID));
	}

	/**
	 * Wird nach dem Parsen aufgerufen, um die gesammelten Kanten zu durchlaufen und
	 * in den Knoten die entsprechenden Vorgänger-Nachfolger-Relationen zu setzen.
	 */
	private void verbindeKanten() {
		for (Kante k : kanten.values()) {
			PNKnoten quelle = getKnoten(k.getQuellID());
			PNKnoten ziel = getKnoten(k.getZielID());
			quelle.addNachfolger(k, ziel);
			ziel.addVorgaenger(k, quelle);
		}
	}

	/**
	 * Durchsucht Stellen und Transitionen nach dem PNKnoten mit der entsprechenden ID
	 * und gibt diese zurück
	 * 
	 * @param id String der ID der PNKnoten die gesucht wird
	 * @return PNKnoten mit der übergebenen ID, Stelle oder Transition
	 */
	public PNKnoten getKnoten(String id) {
		pnKnoten.putAll(stellen);
		pnKnoten.putAll(transitionen);
		return pnKnoten.get(id);
	}

	/**
	 * Gibt die HashMap mit den Stellen zurück.
	 * 
	 * @return die HashMap mit allen Stellen
	 */
	public HashMap<String, Stelle> getStellen() {
		return stellen;
	}

	/**
	 * Gibt die HashMap mit den Transitionen zurück.
	 * 
	 * @return die HashMap mit allen Transitionen
	 */
	public HashMap<String, Transition> getTransitionen() {
		return transitionen;
	}

	/**
	 * Gibt die HashMap mit den Kanten zurück.
	 * 
	 * @return die HashMap mit allen Kanten
	 */
	public HashMap<String, Kante> getKanten() {
		return kanten;
	}

	/**
	 * Setzt das Petrinetz auf die entsprechende Markierung zurück
	 * 
	 * @param markierung Markierung auf die das Petrinetz zurück gesetzt werden soll
	 */
	public void resetTo(String markierung) {

		// Klammern entfernen
		markierung = markierung.substring(1, markierung.length() - 1);
		// String in die einzelnen Zahlen für die Stellen-Marken splitten
		String[] marken = markierung.split("\\|");

		// Marken den entsprechenden Stellen zuweisen
		ArrayList<String> stellenSortiert = new ArrayList<String>();
		stellenSortiert.addAll(stellen.keySet());
		stellenSortiert.sort(null);

		Iterator<String> it = stellenSortiert.iterator();

		// Iterator<Stelle> it = stellen.values().iterator();
		for (int i = 0; i < marken.length; i++) {
			Stelle s = stellen.get(it.next());
			s.setMarken(Integer.parseInt(marken[i]));
		}

		// Transitionen aktivieren
		aktiviereTransitionen();

		// Changed Flag setzen und Observer benachrichtigen
		setChanged();
		notifyObservers();
	}

	/**
	 * Wird am Ende des Parsing-Vorgangs und nach dem Schalten aufgerufen. Setzt die
	 * Aktivierung der Transitionen, wenn alle Vorgängerstellen markiert sind.
	 */
	private void aktiviereTransitionen() {
		aktivierteTransitionen.clear();
		Iterator<Transition> it = transitionen.values().iterator();
		while (it.hasNext()) {
			Transition t = it.next();
			t.pruefeAktivierung();
			if (t.istAktiviert())
				aktivierteTransitionen.add(t.getID());
		}
	}

	/**
	 * Setzt die Markierung des PN auf die Anfangsmarkierung zurück.
	 */
	public void reset() {
		Iterator<Stelle> i = stellen.values().iterator();
		while (i.hasNext()) {
			Stelle s = i.next();
			s.resetMarken();
		}
		aktiviereTransitionen();

		// Changed Flag setzen und Observer benachrichtigen
		setChanged();
		notifyObservers();
	}

	/**
	 * Gibt die Liste mit den aktuell aktivierten Transitionen zurück
	 * 
	 * @return Liste der aktivierten Transitionen
	 */
	public ArrayList<String> getAktivierteTransitionen() {
		return aktivierteTransitionen;
	}

	/**
	 * Wenn die Transition aktiviert ist, kann sie geschlatet werden. Dabei
	 * verlieren alle Vorgänger eine Marke und alle Nachfolger erhalten eine Marke.
	 * Anschließend werden die aktuell aktivierten Transitionen als solche
	 * gekennzeichet und dem EG der geschaltete Übergang übergeben.
	 * 
	 */
	public void schalten(String t) {
		String markierung = getMarkierung();

		// Vorgängermarken reduzieren
		Iterator<PNKnoten> i = getKnoten(t).getVorgaenger().values().iterator();
		while (i.hasNext()) {
			Stelle s = (Stelle) i.next();
			s.markenMinus();
		}

		// Nachfolgermarken erhöhen
		i = getKnoten(t).getNachfolger().values().iterator();
		while (i.hasNext()) {
			Stelle s = (Stelle) i.next();
			s.markenPlus();
		}

		// Aktivierungen aktualisieren
		aktiviereTransitionen();

		// Übergang zum EG hinzufügen
		String folgeMarkierung = getMarkierung();
		eg.addUebergang(markierung, folgeMarkierung, t);

		// Changed Flag setzen und Observer benachrichtigen
		setChanged();
		notifyObservers();
	}

	/**
	 * Gibt die aktuelle Markierung des Petrinetzes als String in der
	 * Form(m1|m2|...) zurück.
	 * 
	 * @return String der aktuellen Markierung
	 */
	public String getMarkierung() {

		ArrayList<String> stellenSortiert = new ArrayList<String>();
		stellenSortiert.addAll(stellen.keySet());
		stellenSortiert.sort(null);

		Iterator<String> it = stellenSortiert.iterator();

		String markierung = "(";
		while (it.hasNext()) {
			String sID = it.next();
			markierung += stellen.get(sID).getMarken() + "|";
		}

		// letztes Trennzeichen entfernen
		markierung = markierung.substring(0, markierung.length() - 1);

		markierung += ")";
		return markierung;
	}

	/**
	 * Gibt den zugehörogen EG zurück
	 * 
	 * @return zu diesem PN assoziierter EG
	 */
	public Erreichbarkeitsgraph getEG() {
		return eg;
	}

	/**
	 * Setzt den EG auf die aktuelle Markierung zurück.
	 */
	public void resetEG() {
		eg.reset(getMarkierung());
	}

	/**
	 * Erhöht oder verringert die Anfangsmarkierung der ausgewählten Stelle um eins.
	 * @param id ID der zu verändernden Stelle
	 * @param vorzeichen '+' oder '-'
	 */
	public void markePlusMinus(String id, char vorzeichen) {
		Stelle s = getStellen().get(id);
		int marken = s.getMarken();

		// neue Anfangsmarkierung setzen für ausgewählte Stelle
		switch (vorzeichen) {
		case '+':
			s.setAnfangsMarken(marken + 1);
			break;
		case '-':
			// nur Marke abziehen, wenn welche vorhanden sind
			if (marken > 0) {
				s.setAnfangsMarken(marken - 1);
			}
			break;
		}

		// alle anderen Stellen mit ihrer aktuellen Anzahl an Marken
		// zur Anfangsmarkierung hinzunehmen
		for (Stelle stelle : stellen.values()) {
			stelle.setAnfangsMarken(stelle.getMarken());
		}

		aktiviereTransitionen();
		resetEG();

		// Changed Flag setzen und Observer benachrichtigen
		setChanged();
		notifyObservers();
	}

}
