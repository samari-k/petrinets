package petrinets.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Stack;

import org.graphstream.graph.Node;

import petrinets.model.pn.Petrinetz;
import petrinets.view.EGGraph;
import petrinets.view.PNGraph;
import petrinets.view.Hauptfenster;

/**
 * Diese Klasse Controller dient der Steuerung der Anwendung.
 */
public class Controller {

	private Hauptfenster frame;
	private PNGraph pnGraph;
	private EGGraph egGraph;
	private Petrinetz petrinetz;

	/**
	 * Im Konstruktor wird dem Controller der Hauptframe zur Kontrolle übergeben
	 * und die beiden Graphstream-Graphen initialisiert.
	 * 
	 * @param frame das Hauptfensters
	 */
	public Controller(Hauptfenster frame) {
		this.frame = frame;
		pnGraph = new PNGraph("Petrinetz");
		egGraph = new EGGraph("Erreichbarkeitsgraph", pnGraph);
	}

	/**
	 * Gibt den Petrinetz-Graphen zurück.
	 * 
	 * @return Petrinetzraph
	 */
	public PNGraph getPNGraph() {
		return pnGraph;
	}

	/**
	 * Gibt den Erreichbarkeitsgraphen zurück.
	 * 
	 * @return Erreichbarkeitsgraph
	 */
	public EGGraph getEGGraph() {
		return egGraph;
	}

	/**
	 * Wird vom {@link Hauptfenster} beim Öffnen einer Datei aufgerufen.
	 * Erstellt ein neues Petrinetz-Objekt und übergibt diesem die PNML-Datei.
	 * Die Graphstream-Graphen werden ihren Model-Äquivalenten als Observer übergeben
	 * und initial über die Veränderung informiert.
	 * 
	 * @param pnml geöffnete PNML-Datei
	 */
	public void parse(File pnml) {
		
		// Petrinetz erstellen und die zu parsende Datei übergeben
		petrinetz = new Petrinetz(pnml);
		
		// Observer für die Graphen anmelden
		petrinetz.addObserver(pnGraph);
		petrinetz.getEG().addObserver(egGraph);
		
		// Observer zum initialisieren des Graphen veranlassen
		petrinetz.notifyObservers("init");
		petrinetz.getEG().notifyObservers("clear");
		
		// Dateinamen auf TextArea schreiben
		addTextToArea("\nPetrinetz erstellt: " + petrinetz.getDateiname() + "\n");
	}

	/**
	 * Nutzt die addText Funktion des Frames, um in die TextArea zu schreiben.
	 * Wahrscheinlich unnötig, weil nur eine Zeile Code.
	 * 
	 * @param text Zu schreibender Text
	 */
	private void addTextToArea(String text) {
		frame.addText(text);
	}

	/**
	 * Wird vom {@link ClickListener} aufgerufen und reicht den Mausklick an die
	 * entsprechenden Funktionen weiter, je nach Name.
	 * 
	 * @param id Id des Knotens, der angeklickt wurde
	 * @param name Name des aufrufenden Clicklisteners ("pn" oder "eg")
	 */
	protected void clickNode(String id, String name) {
		switch(name) {
		case "pn":
			clickInPN(id);
			break;
		case "eg":
			clickInEG(id);
			break;
		}
	}
	
	/**
	 * Wird aufgerufen, wenn im Petrinetz-Grapen geklickt wurde und ruft die
	 * entsprechende Funktion auf:
	 * Bei Klick auf eine aktivierte Transition: schalten
	 * Bei Klick auf eine Stelle: markieren
	 * @param id ID der Node auf die geklickt wurde
	 */
	private void clickInPN(String id) {
		
		Node n = pnGraph.getNode(id);

		// wenn n eine aktivierte Transition ist: schalten.
		if (n.hasAttribute("transition")) {
			if (n.getAttribute("ui.class") == "aktiv") {
				addTextToArea("Transition " + id + " schalten.\n");
				schalten(id);
			}
		}

		// wenn n eine Stelle ist: Stelle als ausgewählt merken,
		// falls sie nicht schon ausgewählt ist
		if (n.hasAttribute("stelle")) {
			if (n != pnGraph.getClicked()) {
				addTextToArea("Ausgewählte Stelle: " + n.getId() + "\n");
				pnGraph.setGeklickt(n);
			}
		}
	}

	/**
	 * Wird aufgerufen, wenn im Erreichbarkeitsgrapen geklickt wurde und
	 * setzt das Petrinetz auf die entsprechende Markierung zurück
	 * @param id ID der Node auf die geklickt wurde
	 */
	private void clickInEG(String id) {
		addTextToArea("Setze Petrinetz auf " + id + " zurück.\n");
		petrinetz.resetTo(id);
		egGraph.markiereKnoten(id);
	}

	/**
	 * Wird nach einem Klick auf eine aktivierte Transition im PN-Graphen
	 * aufgerufen.
	 * Initialisiert das Schalten einer Transition über das Petrinetz und
	 * markiert im EG-Graphen den aktuellen Übergang.
	 * 
	 * @param t ID der schaltenden Transition
	 */
	private void schalten(String t) {
		petrinetz.schalten(t);
		egGraph.markiereAktuellenUebergang(petrinetz.getEG().getAktuellerKnoten(), petrinetz.getEG().getFolgeKnoten(),
				t);
	}

	/**
	 * Wird vom {@link Hauptfenster} beim Klick auf die Buttons MarkePlus und MarkeMinus
	 * aufgerufen.
	 * Erhöht oder verringert die Anfangsmarkierung der ausgewählten Stelle um eins.
	 * 
	 * @param vorzeichen je nach Button '+' oder '-'
	 */
	public void markePlusMinus(char vorzeichen) {
		if (pnGraph.getClicked() == null)
			addTextToArea("Marke Plus: Keine Stelle ausgewählt!\n");
		else {
			// ID der geklickten Stelle und das Vorzeichen an das Petrinetz weiterreichen
			switch (vorzeichen) {
			case '+':
				petrinetz.markePlusMinus(pnGraph.getClicked().getId(), '+');
				break;
			case '-':
				petrinetz.markePlusMinus(pnGraph.getClicked().getId(), '-');
				break;
			}
			
			addTextToArea("Neue Anfangsmarkierung gesetzt: " + petrinetz.getMarkierung() + "\n");
		}
	}

	/**
	 * Wird vom {@link Hauptfenster} beim Klick auf den ResetPN Button aufgerufen.
	 * Setzt die Markierung des Petrinetzes auf die aktuelle Anfangsmarkierung
	 * zurück.
	 */
	public void resetPN() {
		if (petrinetz == null)
			addTextToArea("Reset PN: Kein Petrinetz geladen.\n");
		else {
			addTextToArea("Setze Petrinetz auf Anfangsmarkierung zurück.\n");
			petrinetz.reset();
		}
	}

	/**
	 * Wird vom {@link Hauptfenster} beim Klick auf den LöscheEG Button aufgerufen.
	 * Löscht den Erreichbarkeitsgraph und setzt das Petrinetz auf seine
	 * Anfangsmarkierung zurück.
	 */
	public void loescheEG() {
		if (petrinetz == null)
			addTextToArea("Lösche EG: Kein Petrinetz geladen.\n");
		else {
			resetPN();
			addTextToArea("Lösche Erreichbarkeitsgraphen.\n");
			petrinetz.resetEG();
		}
	}

	
	
	// ********************************************************************
	// Alles zur Beschränktheitsanalyse
	// ********************************************************************

	private Stack<String> markierungen;
	private Stack<String> transitionen;
	private Boolean beschraenkt;
	private Boolean beendet;
	private String pfadAnfang;
	private String pfadEnde;

	/**
	 * Wird vom {@link Hauptfenster} aufgerufen, um ein geladenes Petrinetz auf
	 * Beschränktheit zu überprüfen.
	 */
	public void einzelAnalyse() {
		if (petrinetz == null)
			addTextToArea("Analysiere: Kein Petrinetz geladen.\n");
		else {
			analyse();

			// Wenn unbeschränkt: Pfad visualisieren
			if(!beschraenkt)
				visualisierePfad();
			
			// Dialog aufrufen
			frame.showAnalyseErgebnis(beschraenkt);
		}
	}

	/**
	 * Startet die Beschränktheitsanalyse für das Aktuelle Netz
	 */
	private void analyse() {
		loescheEG();
		addTextToArea("Analysiere: " + petrinetz.getDateiname() + "\n");

		markierungen = new Stack<String>();
		transitionen = new Stack<String>();

		beschraenkt = true;
		beendet = false;

		// Solange das Petrinetz nicht beschränkt ist und
		// die Analyse nicht beendet ist, führe Analyse durch
		while (beschraenkt && !beendet) {
			beendet = analysiereNetz(petrinetz);
		}

		// Ausgabe ob beschränkt oder nicht
		if (beschraenkt)
			addTextToArea("Das Petrinetz ist beschränkt.\n\n");
		else
			addTextToArea("Das Petrinetz ist unbeschränkt.\n\n");
	}

	/**
	 * Analysiert rekursiv auf Beschränktheit, indem nach und nach alle
	 * aktivierten Transitionen in der Art einer Tiefensuche geschaltet werden.
	 * 
	 * @param pn zu analysierendes Petrnietz in der aktuellen Konfiguration
	 */
	private boolean analysiereNetz(Petrinetz pn) {

		// Die möglichen Transitionen werden nach und nach abgearbeitet
		// Jede Rekursionsebene hat ihren eigenen Transitions-Stack
		Stack<String> aktiveTransitionen = new Stack<String>();

		// aktuelle Markierung des PN holen
		String m = pn.getMarkierung();

		// nur weitermachen, wenn die Markierung noch nicht vorhanden ist, 
		// ansonsten Markierung nur auf Stack pushen, um Kreise korrekt zu 
		// erkennen, aber Analyse an anderer Stelle fortführen
		if (markierungen.contains(m)) {
			markierungen.push(m);
		} else {
			// wenn m neu hinzugekommen ist: Pfad auf Beschränktheit überprüfen
			beschraenkt = pfadOK(m);
			markierungen.push(m);

			// nur weitermachen, wenn noch beschränkt
			if (beschraenkt) {

				// aktivierte Transitionen auf den Stack packen
				ArrayList<String> aktivierteTransitionen = pn.getAktivierteTransitionen();
				Iterator<String> it = aktivierteTransitionen.iterator();
				while (it.hasNext())
					aktiveTransitionen.push(it.next());

				// Transitionen schalten und Analyse rekursiv fortfahren
				while (!aktiveTransitionen.empty()) {
					transitionen.push(aktiveTransitionen.peek());
					pn.schalten(aktiveTransitionen.pop());
					// rekursiver Aufruf nach der Schaltung
					analysiereNetz(pn);
					// wenn unbeschränkt, mit false beenden
					if (!beschraenkt)
						return false;
					// sonst Petrinetz und Pfad zurücksetzen auf diese Rekursionsebene
					pn.resetTo(m);
					while (!markierungen.peek().equals(m)) {
						markierungen.pop();
						transitionen.pop();
					}
				}
			}
		}
		return true;
	}

	/**
	 * Vergleicht einen Knoten mit allen seinen Vorgängern.
	 * 
	 * @param m der mit dem Pfad zu vergleichende Knoten
	 * @return true, wenn keine Unbeschränktheit festgestellt wurde, sonst false
	 */
	private Boolean pfadOK(String m) {
		Stack<String> markierungenKopie = new Stack<String>();
		markierungenKopie.addAll(markierungen);

		while (!markierungenKopie.empty()) {
			String s = markierungenKopie.pop();
			if (erfuelltUnbeschraenktKriterium(m, s)) {
				// Wenn unbeschränkt: Start- und Endknoten des Pfades setzen
				pfadAnfang = s;
				pfadEnde = m;
				// Pfad nicht OK
				return false;
			}
		}
		// Pfad OK
		return true;
	}

	/**
	 * Testet, ob ein String an jeder Stelle größer oder gleich dem anderen ist. Da
	 * ein Vorkommen des selben Knotens bereits im Vorfeld ausgeschlossen wurde,
	 * erfüllt dies dann das Unbeschränktheits-Kriterium.
	 * 
	 * @param s1 Aktuelle Markierung, die mit dem Pfad verglichen wird
	 * @param s2 Markierung aus dem Pfad, mit der die aktuelle Markierung verglichen
	 *           wird
	 * @return true, wenn Unbeschränktheits-Kriterium erfüllt ist, sonst false
	 */
	private Boolean erfuelltUnbeschraenktKriterium(String s1, String s2) {
		char[] s1Chars = s1.toCharArray();
		char[] s2Chars = s2.toCharArray();

		// Gibt false zurück, wenn s1 an einer Stelle kleiner als s2 ist
		for (int i = 0; i < s1Chars.length; i++)
			if (s1Chars[i] < s2Chars[i])
				return false;
		// gibt true zurück, wenn s1 an jeder Stelle größer oder gleich s2 ist
		return true;
	}

	/**
	 * Wird nur bei der Einzelanalyse aufgerufen, wenn das PN
	 * unbeschränkt ist. Visualisiert den Unbeschränkt-Pfad.
	 */
	private void visualisierePfad() {
		
		egGraph.getNode(pfadEnde).addAttribute("ui.class", "pfadEnde");
		
		if(egGraph.getNode(pfadAnfang).hasAttribute("anfangsknoten"))
			egGraph.getNode(pfadAnfang).addAttribute("ui.class", "anfangsknotenPfadAnfang");
		else
			egGraph.getNode(pfadAnfang).addAttribute("ui.class", "pfadAnfang");

		String aktuellerKnoten = markierungen.pop();

		// Pfad rückwärts markieren
		while(!markierungen.empty()){
			String vorgaenger = markierungen.pop();
			egGraph.getNode(aktuellerKnoten).getEdgeFrom(vorgaenger).addAttribute("ui.class", "pfad");
			aktuellerKnoten = vorgaenger;
		}
		
	}

	/**
	 * Wird vom {@link Hauptfenster} beim Auswählen der Stapelanalyse aufgerufen.
	 * Startet die Stapelanalyse für einen Datei-Stapel und gibt das Ergebnis am Ende
	 * der Stapelverarbeitung als übersichtliche Ausgabe in der TextArea aus. Dafür
	 * wird die Visualisierung ausgeschaltet, die Dateien nacheinander über den
	 * Parser geladen und mittels der Analyse-Funktion analysiert.
	 * 
	 * @param dateien zu analysierender DateiStapel
	 */
	public void stapelAnalyse(File[] dateien) {

		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);

		
		// Tabellenkopf formatieren
		String line = "---------------------------------------------------------------------";
		formatter.format("%-55.55s | %-11.11s | Knoten / Kanten bzw \n", "", "");
		formatter.format("%-55.55s | %-11.11s | Pfadlänge:Pfad; m, m' \n", "Dateiname", "beschränkt");
		formatter.format("%-55.55s | %-11.11s | %45.45s \n", line, line, line);

		// Jede Datei aus dem übergebenen Array analysieren
		for (File datei : dateien) {
			parse(datei);
			analyse();

			// Ausgabe zum Formatter hinzufügen
			if (beschraenkt) {
				int anzahlKnoten = petrinetz.getEG().getAnzahlKnoten();
				int anzahlUebergaenge = petrinetz.getEG().getAnzahlUebergaenge();
				formatter.format("%-55.55s | %-11.11s | %d / %d \n", petrinetz.getDateiname(), "ja",
						anzahlKnoten, anzahlUebergaenge);
			}
			else
				formatter.format("%-55.55s | %-11.11s | %d:%-17s %-16s %s  \n", petrinetz.getDateiname(), "nein",
						transitionen.size(), formatTransitionen(), pfadAnfang + ",", pfadEnde);

			petrinetz.resetEG();
		}

		formatter.format("\n\n");
		
		// Tabelle in TextArea ausgeben
		addTextToArea(formatter.toString());
		formatter.close();

		// Graphische Darstellung löschen
		petrinetz = null;
		pnGraph.clear();
		egGraph.clear();
	}

	/**
	 * Bringt den Transitions-Stack in das richtige Format für die Tabellenausgabe
	 * @return printfähige Auflistung des Transitions-Stacks
	 */
	private String formatTransitionen() {
		String t = "(";
		while (!transitionen.empty())
			t += transitionen.remove(0) + ",";
		t = t.substring(0, t.length() - 1);
		t += ");";
		return t;
	}

}
