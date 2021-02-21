package petrinets.model.pn;

/**
 * Ein Objekt dieser Klasse repräsentiert eine eindeutige Kante im
 * Petrinetz mit einer ID, und seinen zugehörigen Quell- und Ziel-IDs
 */
public class Kante{
	
	private final String id;
	private final String quellID;
	private final String zielID;
	
	/*
	 * Im Konstruktor werden die IDs gesetzt.
	 */
	protected Kante(String id, String quellID, String zielID) {
		this.id = id;		
		this.quellID = quellID;
		this.zielID = zielID;
	}
	
	/**
	 * Gibt die eindeutige ID des Knotens zurück.
	 * @return String mit der ID des Knotens.
	 */
	protected String getID() {
		return id;
	}
	
	/**
	 * Gibt die ID des Quellknotens zurück.
	 * @return ID der Quelle
	 */
	public String getQuellID() {
		return quellID;
	}
	
	/**
	 * Gibt die ID des Zielknotens zurück.
	 * @return ID des Ziels
	 */
	public String getZielID() {
		return zielID;
	}
	
}
