/**
 * 
 */
package petrinets.model.pn;

import java.io.File;


/**
 * Diese Klasse erweitert den PNMLWopedParser aus dem Basis-Projekt. Hier wird
 * eine übergebene PNML-Datei in die in diesem Programm implementierte
 * Datenstruktur eines Petrinetzes geparsed.
 */
public class PNMLParser extends PNMLWopedParser {

	// Das Petrinetz, in das vom Parser geschrieben wird
	private Petrinetz pn;

	/**
	 * Der Konstruktor wird vom {@link Petrinetz} aufgerufen.
	 * Er übergibt die PNML-Datei an seine Oberklasse und
	 * setzt das Petrinetz, in das die Daten geparsed werden sollen.
	 * 
	 * @param pnml zu parsende PNML-Datei
	 * @param pn das zu schreibende Petrinetz
	 */
	protected PNMLParser(File pnml, Petrinetz pn) {
		super(pnml);
		this.pn = pn;
	}

	/**
	 * Fügt dem Petrinetz eine neue Transition mit der gelesenen ID hinzu.
	 * 
	 * @param id Identifikationstext der Transition
	 */
	public void newTransition(final String id) {
		System.out.println("Transition mit id " + id + " wurde gefunden.");
		pn.addTransition(id);
	}

	/**
	 * Fügt dem Petrinetz eine neue Stelle mit der gelesenen ID hinzu.
	 * 
	 * @param id Identifikationstext der Stelle
	 */
	public void newPlace(final String id) {
		System.out.println("Stelle mit id " + id + " wurde gefunden.");
		pn.addStelle(id);
	}

	/**
	 * Fügt dem Petrinetz eine neue Kante mit der gefunden ID hinzu und setzt diese
	 * den entsprechenden Knoten als Vorgänger bzw Nachfolger.
	 * 
	 * @param id     Identifikationstext der Kante
	 * @param source Identifikationstext des Startelements der Kante
	 * @param target Identifikationstext des Endelements der Kante
	 */
	public void newArc(final String id, final String source, final String target) {
		System.out.println("Kante mit id " + id + " von " + source + " nach " + target + " wurde gefunden.");
		pn.addKante(id, source, target);
	}

	/**
	 * Aktualisiert die Positionen der geladenen Elemente.
	 * 
	 * @param id Identifikationstext des Elements
	 * @param x  x Position des Elements
	 * @param y  y Position des Elements
	 */
	public void setPosition(final String id, final String x, final String y) {
		System.out.println("Setze die Position des Elements " + id + " auf (" + x + ", " + y + ")");
		int newY = Integer.parseInt(y);
		pn.getKnoten(id).setPosition(Integer.parseInt(x), newY - 2 * newY);
	}

	/**
	 * Aktualisiert den Namen des geladenen Elements.
	 * 
	 * @param id   Identifikationstext des Elements
	 * @param name Beschriftungstext des Elements
	 */
	public void setName(final String id, final String name) {
		System.out.println("Setze den Namen des Elements " + id + " auf " + name);
		pn.getKnoten(id).setName(name);
	}

	/**
	 * Aktualisiert die Anfangsmarkierung des geladenen Elements.
	 * 
	 * @param id     Identifikationstext des Elements
	 * @param tokens Markenanzahl des Elements
	 */
	public void setTokens(final String id, final String tokens) {
		System.out.println("Setze die Markenanzahl des Elements " + id + " auf " + tokens);
		Stelle s;
		s = (Stelle) pn.getKnoten(id);
		s.setAnfangsMarken(Integer.parseInt(tokens));
	}
}
