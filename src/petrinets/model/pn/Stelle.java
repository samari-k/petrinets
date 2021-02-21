package petrinets.model.pn;

/**
 * Ein Objekt dieser Klasse repräsentiert eine Stelle in einem Petrinetz
 * mit allen Eigenschaften von {@link PNKnoten} sowie einer Anfangsmarkierung
 * und der aktuellen Anzahl an Marken.
 */
public class Stelle extends PNKnoten{
	
	private int anfangsMarken;
	private int marken;
	
	/**
	 * Der Konstruktor setzt die ID der Stelle mittels des Konstruktors 
	 * der Oberklasse und initialisiert die Anzahl der Marken und die 
	 * Anfangsmarkierung mit 0.
	 * @param id	Eindeutige ID der Stelle
	 */
	protected Stelle(String id) {
		super(id);
		anfangsMarken = 0;
		marken = 0;
	}

	/**
	 * Gibt die Anzahl der Marken der Anfangsmarkierung der Stelle zurück
	 * 
	 * @return Anfangsmarkierung
	 */
	public int getAnfangsMarken() {
		return anfangsMarken;
	}
	
	/**
	 * Setzt eine neue Anfangsmarkierung für diese Stelle.
	 * 
	 * @param markenZahl Zahl der Marken für die neue Anfangsmarkierung
	 */
	protected void setAnfangsMarken(int markenZahl) {
		anfangsMarken = markenZahl;
		this.marken = anfangsMarken;
	}
	
	/**
	 * Setzt die Marken-Anzahl dieser Stelle auf die übergebene Anzahl
	 * @param markenZahl neue Marken-Anzahl
	 */
	protected void setMarken(int markenZahl) {
		this.marken = markenZahl;
	}
	
	/**
	 * Gibt die Anzahl der Marken dieser Stelle zurück
	 * @return aktuelle Marken-Anzahl
	 */
	public int getMarken() {
		return marken;
	}
	
	/**
	 * Erhöht die Anzahl der Marken um eins
	 */
	protected void markenPlus() {
		marken ++;
	}
	
	/**
	 * Verringert die Anzahl der Marken um eins.
	 */
	protected void markenMinus() {
		marken--;
	}
	
	/**
	 * Setzt die aktuelle Markenzahl auf die Anfangsmarkenzahl zurück
	 */
	protected void resetMarken() {
		marken = anfangsMarken;
	}
}
