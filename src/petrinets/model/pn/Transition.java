package petrinets.model.pn;

import java.util.Iterator;

/**
 * Ein Objekt dieser Klasse repräsentiert eine Transition in einem Petrinetz
 * mit allen Eigenschaften von {@link PNKnoten} sowie einer möglichen Aktivierung
 * und Vorgänger- und Nachfolger-Stelle.
 */
public class Transition extends PNKnoten{

	private boolean istAktiviert;
	
	/**
	 * Der Konstruktor setzt die ID der Transition mittels des Konstruktors 
	 * der Oberklasse und setzt istAktiviert auf false
	 * @param id	Eindeutige ID der Stelle
	 */
	protected Transition(String id) {
		super(id);
		istAktiviert = false;
	}

	/**
	 * Wird vom {@link Petrinetz} aufgerufen. Prüft an Hand der Vorgängerstellen,
	 * ob die Transition aktiviert ist und setzt dementsprechend die Aktivierung
	 */
	protected void pruefeAktivierung() {
		Iterator<PNKnoten> it = this.getVorgaenger().values().iterator();
		this.aktivieren(true);;
		while(it.hasNext()) {
			Stelle n = (Stelle) it.next();
			if(n.getMarken()==0) {
				// deaktiviert, sobald ein Vorgänger 0 Marken hat
				this.aktivieren(false);
				break;
			}
		}	
	}
	
	
	/**
	 * Gibt zurück, ob die Transition unter der aktuellen Markierung aktivert ist.
	 * @return true, wenn alle Vorgänger-Stellen markiert sind, sonst false
	 */
	public boolean istAktiviert() {
		return istAktiviert;
	}
	
	/**
	 * Aktiviert oder deaktivert die aktuelle Transition
	 * @param aktiv Boolean-Wert auf den die Aktivierung gesetzt wird
	 */
	protected void aktivieren(boolean aktiv) {
		istAktiviert = aktiv;
	}
	
}
