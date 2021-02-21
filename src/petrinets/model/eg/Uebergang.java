package petrinets.model.eg;

/**
 * Die Klasse Übergang repräsentiert den Übergang von einer Markierung eines
 * Petrinetzes zu einer nächsten. Ein Übergangs-Objekt kennt seinen Folgeknoten
 * (die Folgemarkierung) und die IDs der Transitionen, die diesen Übergang
 * schalten.
 */
public class Uebergang {

	private Knoten vorgaenger;
	private Knoten nachfolger;
	/** enthält die IDs der Transitionen, die diesen Übergang schalten */
	private String transition;

	/**
	 * Der Konstruktor erstellt den Übergang mitVorgänger und Nachfolger Knoten.
	 * Die Lliste der Transitionen wird initialisiert und mit der ersten schaltenden
	 * Transition gefüllt.
	 * 
	 * @param v Vorgängerknoten des Übergangs
	 * @param n Nachfolgerknoten des Übergangs
	 * @param t ID der ersten schaltenden Transition
	 */
	protected Uebergang(Knoten v, Knoten n, String t) {
		vorgaenger = v;
		nachfolger = n;
		transition = t;
	}

	/**
	 * Liefert den Folgeknoten des Übergangs;
	 * 
	 * @return Folgeknoten
	 */
	public Knoten getNachfolger() {
		return nachfolger;
	}
	
	/**
	 * Liefert den Vorgängerknoten des Übergangs.
	 * 
	 * @return Vorgängerknoten
	 */
	public Knoten getVorgaenger() {
		return vorgaenger;
	}

	/**
	 * Liefert die Markierung des Folgeknotens.
	 * 
	 * @return String der Markierung
	 */
	public String getFolgemarkierung() {
		return nachfolger.getMarkierung();
	}

	public String getVorgaengerMarkierung() {
		return vorgaenger.getMarkierung();
	}

	/**
	 * Liefert die Liste der Transitionen, die diesen Übergang schalten.
	 * 
	 * @return Lise der Transitionen
	 */
	public String getTransition() {
		return transition;
	}

}
