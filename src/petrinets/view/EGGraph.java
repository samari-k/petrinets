package petrinets.view;

import java.util.Observable;
import java.util.Observer;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import petrinets.model.eg.Erreichbarkeitsgraph;

/**
 * Die Klasse EGGraph repräsentiert die Visualisierung des
 * Erreichbarkeitsgraphen mittels der GraphStream-Bibliothek.
 * Als Observer holt sie sich ihre Daten direkt aus dem {@link Erreichbarkeitsgraph}
 */
public class EGGraph extends MultiGraph implements Observer {

	// URL-Angabe zur css-Datei, in der das Layout des Graphen angegeben ist.
	private static String CSS_FILE = "url(" + EGGraph.class.getResource("/eggraph.css") + ")";
	private PNGraph pnGraph;
	private Edge letzterUebergang;
	private Node letzterKnoten;

	/**
	 * Der Konstruktor erstellt einen leeren Multigraphen und setzt den
	 * zugehörigen Petrinetz-Graphen
	 * 
	 * @param id Name des Graphen
	 * @param pnGraph zugehöroger PetrinetzGraph
	 */
	public EGGraph(String id, PNGraph pnGraph) {
		super(id);
		// Angabe einer css-Datei für das Layout des Graphen
		this.addAttribute("ui.stylesheet", CSS_FILE);
		this.pnGraph = pnGraph;
		// Qualitäts-Attribute für einen schöneren Graphen
		this.setAttribute("ui.antialias");
		this.setAttribute("ui.quality");

	}

	/**
	 * Löscht den Erreichbarkeitsgraphen komplett und setzt die Attribute für Style
	 * und Qualität neus
	 */
	private void clearEG() {
		// alles auf Null setzen
		this.clear();
		// Stylesheet und Qualität neu hinzufügen
		this.addAttribute("ui.stylesheet", CSS_FILE);
		this.setAttribute("ui.antialias");
		this.setAttribute("ui.quality");
	}
		
	/**
	 * Markiert nach einer Schaltung den Übergang und den Folgeknoten (der
	 * die Folgemarkierung des PN repräsentiert)
	 * @param aktuell Markierung vor der Schaltung
	 * @param folge Markierung nach der Schaltung
	 * @param trans ID der schaltenden Transition
	 */
	public void markiereAktuellenUebergang(String aktuell, String folge, String trans) {
		
		Edge e = this.getEdge(aktuell + trans + folge);
		if (letzterUebergang != null)
			letzterUebergang.removeAttribute("ui.class");
		e.addAttribute("ui.class", "aktuell");
		letzterUebergang = e;

		Node nach = this.getNode(folge);
		if (letzterKnoten != null) {
			letzterKnoten.removeAttribute("ui.class");
			if(letzterKnoten.hasAttribute("anfangsknoten"))
				letzterKnoten.addAttribute("ui.class", "anfangsknoten");
		}
		if(nach.hasAttribute("anfangsknoten"))
			nach.addAttribute("ui.class", "anfangsknotenAktuell");
		else
			nach.addAttribute("ui.class", "aktuell");
		letzterKnoten = nach;
	}
	
	/**
	 * Markiert den Knoten, der die Markierung repräsentiert, auf die das PN
	 * durch einen Knlick im EG zurück gesetzt wurde und löscht die Markierung
	 * des Übergangs
	 * @param knoten Markierung auf die das Petrinetz zurück gesetzt wurde
	 */
	public void markiereKnoten(String knoten) {
		
		if (letzterUebergang != null)
			letzterUebergang.removeAttribute("ui.class");
		
		Node n = this.getNode(knoten);
		if (letzterKnoten != null) {
			letzterKnoten.removeAttribute("ui.class");
			if(letzterKnoten.hasAttribute("anfangsknoten"))
				letzterKnoten.addAttribute("ui.class", "anfangsknoten");
		}
		if(n.hasAttribute("anfangsknoten"))
			n.addAttribute("ui.class", "anfangsknotenAktuell");
		else
			n.addAttribute("ui.class", "aktuell");
		letzterKnoten = n;
	}

	/**
	 * Der Anfangsknoten bekommt das attribut und die Klasse "Anfangsmarkierung",
	 * um als solcher speziell visualisiert werden zu können
	 * @param anfangsmarkierung ID des Knotens der als Anfangsmarkierung gekennzeichnet werden soll
	 */
	private void erstelleAnfangsknoten(String anfangsmarkierung) {
		if (this.getNode(anfangsmarkierung) == null) {
			Node n = this.addNode(anfangsmarkierung);
			n.addAttribute("ui.label", anfangsmarkierung);
			n.addAttribute("anfangsknoten", true);
			n.addAttribute("ui.class", "anfangsknoten");
			n.setAttribute("layout.weight", 20);
		}
	}

	/**
	 * Erstellt einen neuen Knoten mit der übergebenen Markierung als Label
	 * @param markierung Markierung zu der ein EGGraph-Knoten erstellt werden soll
	 */
	private void erstelleKnoten(String markierung) {
		if (this.getNode(markierung) == null) {
			Node n = this.addNode(markierung);
			n.addAttribute("ui.label", markierung);
			n.setAttribute("layout.weight", 20);
		}
	}

	/**
	 * Erstellt einen neuen Übergang, wenn dieser nicht bereits existiert
	 * 
	 * @param key String-Array aus Vorgänger, Transition und Nachfolger des Übergangs
	 * @param vor Vorgängermarkierung
	 * @param nach Nachfolgemarkierung
	 * @param label Label des Übergangs
	 */
	private void erstelleUebergang(String key, String vor, String nach, String label) {
		if (!this.edgeMap.containsKey(key)) {
			Edge e = this.addEdge(key, vor, nach, true);
			e.addAttribute("ui.label", label);
			e.setAttribute("layout.weight", 2);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		
		Erreichbarkeitsgraph eg = (Erreichbarkeitsgraph) o;
		
		// Bei Bedarf vorhandenen Graphen löschen
		if(arg != null)
			if (arg.equals("clear"))
				clearEG();
		
		// Anfangsknoten hervorherben
		if (eg.getAlleKnoten().size() == 1)
			erstelleAnfangsknoten(eg.getAnfangsknotenID());

		// Knoten erstellen
		for (String m : eg.getAlleKnoten().keySet())
			erstelleKnoten(m);

		// Übergänge erstellen
		for (String uebergangKey : eg.getAlleUebergaenge().keySet()) {
			String vor = eg.getUebergang(uebergangKey).getVorgaengerMarkierung();
			String nach = eg.getUebergang(uebergangKey).getFolgemarkierung();
			String transition = eg.getUebergang(uebergangKey).getTransition();
			String label = pnGraph.getNode(transition).getAttribute("ui.label");
			erstelleUebergang(uebergangKey, vor, nach,label);
		}

	}

}
