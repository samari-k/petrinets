package petrinets.view;

import java.util.Observable;
import java.util.Observer;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import petrinets.model.pn.Kante;
import petrinets.model.pn.Petrinetz;
import petrinets.model.pn.Stelle;
import petrinets.model.pn.Transition;

/**
 * Die Klasse PNGraph repräsentiert die Visualisierung des Petrinetzes mittels der 
 * GraphStream-Bibliothek. Als Observer holt sie sich ihre Daten direkt aus dem 
 * {@link Petrinetz}
 */
public class PNGraph extends MultiGraph implements Observer{
	
	// URL-Angabe zur css-Datei, in der das Layout des Graphen angegeben ist.
    private static String CSS_FILE = "url(" + PNGraph.class.getResource("/pngraph.css") + ")";
    // um nicht auf alle möglichen Zahlen zu testen, kriegen Stellen initial
    // das Symbol für mehr als zehn Marken, das wird anhand ihrer realen Markierung
    // im stylesheet überschrieben
    private static final String styleStelle = "shape: circle;"+
    										"fill-mode: image-scaled;"+
    										"fill-image: url('images/10.png');";
    private static final String styleTransition = "shape: box;";
    private Node geklickt;
    
    
    /**
     * Der Konstruktor erstellt einen Multigraphen mit der übergebenen ID
     * @param id Name des Graphen
     */
	public PNGraph(String id) {
		super(id);
	}

	
	/**
	 * Initialisiert den Graphen mit den Daten aus dem übergebenen Petrinetz
	 * @param pn Petrinetz, das die Daten für den PNGraphen bereithält
	 */
	private void initPNGraph(Petrinetz pn) {
		
		// alles auf Null setzen, falls mehr als ein Graph nacheinander geladen werden
		this.clear();
		
		// Angabe einer css-Datei für das Layout des Graphen
		this.addAttribute("ui.stylesheet", CSS_FILE);
		
		// Qualitäts-Attribute für einen schöneren Graphen
		this.setAttribute("ui.antialias");
		this.setAttribute("ui.quality");
		
		// Stellen erstellen
		for(String nodeID : pn.getStellen().keySet()) {
			erstelleStelle(nodeID,pn.getStellen().get(nodeID));
		}
				
		// Transitionen erstellen
		for(String nodeID : pn.getTransitionen().keySet()) {
			erstelleTransition(nodeID,pn.getTransitionen().get(nodeID));
		}
				
		// Kanten erstellen
		for(String arcID : pn.getKanten().keySet()) {
			erstelleKante(arcID, pn.getKanten().get(arcID));
		}
	}


	/**
	 * Erstellt eine Graphstream Node für die übergebene Stelle
	 * @param id Bezeichner der Node
	 * @param stelle Stelle aus der die Node erzeugt wird
	 */
	private void erstelleStelle(String id, Stelle stelle) {
		Node n = this.addNode(id);
		n.addAttribute("stelle", true);
		n.addAttribute("ui.label", "["+id+"] "+stelle.getName()+" <"+stelle.getAnfangsMarken()+">");
		n.addAttribute("xy", stelle.getPosition()[0],stelle.getPosition()[1]);
		n.addAttribute("ui.class", "m"+String.valueOf(stelle.getAnfangsMarken()));
		n.addAttribute("ui.style", styleStelle);
	}
	
	/**
	 * Erstellt eine Graphstream Node für die übergebene Transition
	 * @param id Bezeichner der Node
	 * @param stelle Stelle aus der die Node erzeugt wird
	 */
	private void erstelleTransition(String id, Transition transition) {
		Node n = this.addNode(id);
		n.addAttribute("transition", true);
		n.addAttribute("ui.label", "["+id+"] "+transition.getName());
		n.addAttribute("xy", transition.getPosition()[0],transition.getPosition()[1]);
		n.addAttribute("ui.style", styleTransition);
		if (transition.istAktiviert()) {
			n.addAttribute("ui.class", "aktiv");
		}
	}
	
	/**
	 * Erstellt eine GraphStream Edge für die übergebene Kante
	 * @param id Eindeutige ID der Kante
	 * @param kante das Kanen-Objekt
	 */
	private void erstelleKante(String id, Kante kante) {
		String quellID = kante.getQuellID();
		String zielID = kante.getZielID();
		Edge e = this.addEdge(id, nodeMap.get(quellID), nodeMap.get(zielID), true);
		e.addAttribute("ui.label", "["+id+"]");
	}
	
	
	/**
	 * Markiert eine Node als geklickt. Wird vom Controller beim Klick auf eine
	 * Stelle aufgerufen.
	 * @param n geklickte Stelle
	 */
	public void setGeklickt(Node n) {
		if (geklickt!=null) {
			geklickt.addAttribute("ui.style", "stroke-color: #003366;" + 
											"stroke-width: 1px;");
		}
		geklickt = n;
		geklickt.addAttribute("ui.style", "stroke-color: darkred;" + 
				"stroke-width: 2px;");
	}
	
	/**
	 * Gibt die aktuell geklickte Stelle zurück.
	 * @return aktuell geklickte Stelle
	 */
	public Node getClicked() {
		return geklickt;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg == null) 
			updateGraph((Petrinetz)o);
		else if(arg.equals("init")) 
			initPNGraph((Petrinetz)o);
	}
	
	/**
	 * Bringt das aktuelle Petrinetz erneut in die Graph-Struktur, um Änderungen darzustellen
	 * @param pn das Petrinetz, das die Daten für diesen Graphen bereithält
	 */
	private void updateGraph(Petrinetz pn) {
		for (Node n : this.getNodeSet()) {
			// Markierungen der Stellen neu einlesen
			if (n.hasAttribute("stelle")) {
				Stelle s = (Stelle) pn.getKnoten(n.getId());
				n.changeAttribute("ui.label", "[" + s.getID() + "] " + s.getName() + " <" + s.getMarken() + ">");
				n.changeAttribute("ui.class", "m" + String.valueOf(s.getMarken()));
			}
			// Aktivierungen der Transitionenneu einlesen
			if (n.hasAttribute("transition")) {
				Transition t = (Transition) pn.getKnoten(n.getId());
				if (t.istAktiviert())
					n.addAttribute("ui.class", "aktiv");
				else
					n.removeAttribute("ui.class");
			}
		}
	}
}
