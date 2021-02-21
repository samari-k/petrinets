package petrinets.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import petrinets.controller.ClickListener;
import petrinets.controller.Controller;

/**
 * Diese Klasse stellt das Hauptfenster der Anwendung dar.
 */
public class Hauptfenster extends JFrame {

	/** default serial version ID */
	private static final long serialVersionUID = 1L;

	private static Controller pnController;

	/** Panel zur Anzeige des Petrnietzes mittels GraphStream */
	private ViewPanel pnViewPanel;
	private JPanel pnJPanel;

	/** Panel zur Anzeige des Erreichbarkeitsgraphen mittels GraphStream */
	private ViewPanel egViewPanel;
	private JPanel egJPanel;

	/** Textbereich für die Ausgabe */
	private JTextArea textArea;

	/** Startverzeichnis */
	private static File verzeichnis;

	/** zuletzt geöffnete Datei */
	private static File datei;

	private Viewer egViewer;
	private boolean autoLayoutAn;

	/**
	 * Im Konstruktor wird der Haupt-Frame erzeugt und angezeigt.
	 * 
	 * @param titel Titel des Frames
	 */
	public Hauptfenster(String titel) {
		super(titel);

		// Petrinetz-Controller erzeugen und verbinden
		pnController = new Controller(this);
		// Beispielverzeichnis setzen
		verzeichnis = new File("resources/examples/");
		// Renderer mit Unterstützung für Multigraphen und aller CSS Attribute verwenden
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		// ****************************
		// Menü erstellen
		// ****************************
		JMenuBar menu = new JMenuBar();

		JMenu datei = new JMenu("Datei");

		JMenuItem oeffnen = new JMenuItem("Öffnen...");
		oeffnen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

		JMenuItem neuladen = new JMenuItem("Neu laden");
		neuladen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		neuladen.setEnabled(false); // Zu Beginn ausgeschaltet

		JMenuItem analyseStapel = new JMenuItem("Analyse mehrerer Dateien...");
		analyseStapel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

		JMenuItem beenden = new JMenuItem("Beenden");
		beenden.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

		// ActionListener hinzufügen
		oeffnen.addActionListener((event) -> oeffnen(neuladen));
		neuladen.addActionListener((event) -> neuladen());
		beenden.addActionListener((event) -> System.exit(0));
		analyseStapel.addActionListener((event) -> stapelAnalyse());

		datei.add(oeffnen);
		datei.add(neuladen);
		datei.add(analyseStapel);
		datei.add(beenden);

		menu.add(datei);
		menu.add(Box.createHorizontalGlue());

		this.setJMenuBar(menu);

		// ****************************
		// Toolbar erstellen
		// ****************************

		JToolBar toolbar = new JToolBar("ToolBar");

		JButton loescheEG = new JButton(new ImageIcon("resources/images/loescheeg.png"));
		loescheEG.setToolTipText("<html>Erreichbarkeitsgraph löschen und Petrinetz-<br>"
				+ "Markierung auf Anfangsmarkierung zurück setzen. [Alt+L]</html>");
		loescheEG.setMnemonic('L');
		loescheEG.addActionListener((event) -> pnController.loescheEG());

		JButton resetPN = new JButton(new ImageIcon("resources/images/resetpn.png"));
		resetPN.setToolTipText("Petrinetz auf aktuelle Anfangsmarkierung zurück setzen. [Alt+R]");
		resetPN.setMnemonic('R');
		resetPN.addActionListener((event) -> pnController.resetPN());

		JButton markeMinus = new JButton(new ImageIcon("resources/images/markeminus.png"));
		markeMinus.setToolTipText(
				"<html>Erniedrigt die Anzahl der Marken der <br>" + "ausgewählten Stelle um eins. [Alt+M]</html>");
		markeMinus.setMnemonic('M');
		markeMinus.addActionListener((event) -> pnController.markePlusMinus('-'));

		JButton markePlus = new JButton(new ImageIcon("resources/images/markeplus.png"));
		markePlus.setToolTipText(
				"<html>Erhöht die Anzahl der Marken der <br>" + "ausgewählten Stelle um eins. [Alt+P]</html>");
		markePlus.setMnemonic('P');
		markePlus.addActionListener((event) -> pnController.markePlusMinus('+'));

		JButton analyse = new JButton(new ImageIcon("resources/images/analyse.png"));
		analyse.setToolTipText("Überprüft das aktuelle Petrinetz auf Bechränktheit. [Alt+A]");
		analyse.setMnemonic('A');
		analyse.addActionListener((event) -> pnController.einzelAnalyse());

		// Button zum erzeugen eines Screenshots des PN
		JButton pnScreenshot = new JButton(new ImageIcon("resources/images/pnSave.png"));
		pnScreenshot.setToolTipText("Speichert einen Screenshot des PN als png");
		pnScreenshot.addActionListener((event) -> screenshot(pnJPanel));

		// Button zum erzeugen eines Screenshots des EG
		JButton egScreenshot = new JButton(new ImageIcon("resources/images/egSave.png"));
		egScreenshot.setToolTipText("Speichert einen Screenshot des EG als png");
		egScreenshot.addActionListener((event) -> screenshot(egJPanel));

		// ToggleButton zum Ausschalten des AutoLayouts im EG
		JButton autolayoutToggleButton = new JButton(new ImageIcon("resources/images/layoutOff.png"));
		autolayoutToggleButton.setToolTipText("Schaltet das AutoLayout im EG aus und ein. [Alt-T]");
		autolayoutToggleButton.setMnemonic('T');
		autolayoutToggleButton.addActionListener((event) -> autoLayoutUmschalten(autolayoutToggleButton));

		toolbar.add(resetPN);
		toolbar.add(loescheEG);
		toolbar.add(markeMinus);
		toolbar.add(markePlus);
		toolbar.add(analyse);
		toolbar.add(pnScreenshot);
		toolbar.add(egScreenshot);
		toolbar.add(autolayoutToggleButton);

		add(toolbar, BorderLayout.PAGE_START);

		// *****************************
		// Layout des Fensters erstellen
		// *****************************

		// Graph Panels erstellen
		initPNGraphPanel();
		initEGGraphPanel();

		// ViewPanels in die JPanels packen
		pnJPanel = new JPanel(new BorderLayout());
		pnJPanel.add(BorderLayout.CENTER, pnViewPanel);

		egJPanel = new JPanel(new BorderLayout());
		egJPanel.add(BorderLayout.CENTER, egViewPanel);

		textArea = new JTextArea();
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		// Einbetten der JPanels in eine horizontale SplitPane
		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnJPanel, egJPanel);
		horizontalSplitPane.setResizeWeight(0.5);

		// Einbetten der TextArea und der horizontalSplitPane in eine vertikale
		// SplitPane
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplitPane,
				new JScrollPane(textArea));
		verticalSplitPane.setResizeWeight(0.6);

		// verticalSplitPane zum Haupt-Frame hinzufügen
		this.add(verticalSplitPane);

		// Konfiguriere Haupt-Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 700);
		this.setMinimumSize(new Dimension(350, 300));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	} // end Konstruktor
	
	
	/**
	 * Erzeuge und initialisiere ein Panel zur Anzeige des Petrinetz-Graphen
	 */
	private void initPNGraphPanel() {
		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		Viewer viewer = new Viewer(pnController.getPNGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// Auto-Layout deaktivieren: Die explizit hinzugefügten Koordinaten
		// werden genutzt
		viewer.disableAutoLayout();

		// Eine DefaultView zum Viewer hinzufügen
		pnViewPanel = viewer.addDefaultView(false);

		// Neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		// werden zu können
		ViewerPipe viewerPipe = viewer.newViewerPipe();

		// Neuen ClickListener erzeugen, der als ViewerListener auf Ereignisse
		// der View reagieren kann
		ClickListener clickListener = new ClickListener(pnController, "pn");

		// clickListener als ViewerListener bei der viewerPipe anmelden
		viewerPipe.addViewerListener(clickListener);

		// Neuen MouseListener beim viewPanel anmelden. Wenn im viewPanel ein
		// Maus-Button gedrückt oder losgelassen wird, dann wird die Methode
		// viewerPipe.pump() aufgerufen, um alle bei der viewerPipe angemeldeten
		// ViewerListener zu informieren (hier also konkret unseren
		// clickListener).
		pnViewPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent me) {
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();
			}
		});
	} // end initPNGraphPanel

	/**
	 * Erzeuge und initialisiere ein Panel zur Anzeige des Erreichbarkeits-Graphen
	 */
	private void initEGGraphPanel() {
		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		egViewer = new Viewer(pnController.getEGGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// Auto-Layout aktivieren: GraphStream generiert ein möglichst
		// übersichtliches Layout
		egViewer.enableAutoLayout();
		autoLayoutAn = true;

		// Eine DefaultView zum Viewer hinzufügen
		egViewPanel = egViewer.addDefaultView(false);

		// Neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		// werden zu können
		ViewerPipe viewerPipe = egViewer.newViewerPipe();

		// Neuen ClickListener erzeugen, der als ViewerListener auf Ereignisse
		// der View reagieren kann
		ClickListener clickListener = new ClickListener(pnController, "eg");

		// clickListener als ViewerListener bei der viewerPipe anmelden
		viewerPipe.addViewerListener(clickListener);

		// Neuen MouseListener beim viewPanel anmelden. Wenn im viewPanel ein
		// Maus-Button gedrückt oder losgelassen wird, dann wird die Methode
		// viewerPipe.pump() aufgerufen, um alle bei der viewerPipe angemeldeten
		// ViewerListener zu informieren (hier also konkret unseren
		// clickListener).
		egViewPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent me) {
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				viewerPipe.pump();
			}
		});
	}

	/**
	 * Öffnet einen Datei-Öffnen-Dialog der ausschließlich PNML-Dateien anzeigt und
	 * schaltet die "Neu laden" Option frei
	 * 
	 * @param neuladen der Menüpunkt "Neu laden"
	 */
	private void oeffnen(JMenuItem neuladen) {

		// JFileChooser-Objekt erstellen, das im aktuellen Verzeichnis geöffnet wird
		JFileChooser chooser = new JFileChooser(verzeichnis);

		// PNML-Filter setzen
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("PNML-Dateien", "pnml"));

		// Dialog zum Oeffnen von Dateien anzeigen
		int option = chooser.showOpenDialog(null);

		// falls Datei ausgewählt: an Controller zum Parsen weiterreichen
		// und Die Option "Neu laden" zur Auswahl aktivieren
		if (option == JFileChooser.APPROVE_OPTION) {
			datei = chooser.getSelectedFile();
			pnController.parse(datei);
			neuladen.setEnabled(true);
		}

		// aktuelles Verzeichnis speichern
		verzeichnis = chooser.getCurrentDirectory();
	}

	/**
	 * Lädt die zuletzt geöffnete PNML Datei erneut in den Parser und erstellt den
	 * Graph neu
	 */
	private void neuladen() {
		pnController.parse(datei);
	}

	/**
	 * Reicht eine Menge Dateien an den Controller zur Beschränktheitsanalyse weiter
	 */
	private void stapelAnalyse() {
		// JFileChooser-Objekt erstellen, das im aktuellen Verzeichnis geöffnet wird
		JFileChooser chooser = new JFileChooser(verzeichnis);

		// PNML-Filter setzen
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("PNML-Dateien", "pnml"));
		chooser.setMultiSelectionEnabled(true);

		// Dialog zum Oeffnen von Dateien anzeigen
		int option = chooser.showOpenDialog(null);

		// falls Dateien ausgewählt: an Controller zum Parsen weiterreichen
		if (option == JFileChooser.APPROVE_OPTION) {
			File[] dateien = chooser.getSelectedFiles();
			pnController.stapelAnalyse(dateien);
		}

		// aktuelles Verzeichnis speichern
		verzeichnis = chooser.getCurrentDirectory();
	}

	/**
	 * Schaltet das AutoLayout im EG an bzw aus
	 */
	private void autoLayoutUmschalten(JButton button) {
		if (autoLayoutAn) {
			egViewer.disableAutoLayout();
			autoLayoutAn = false;
			button.setIcon(new ImageIcon("resources/images/layoutOn.png"));
			button.setToolTipText("AutoLayout des EG einschalten");
		} else {
			egViewer.enableAutoLayout();
			autoLayoutAn = true;
			button.setIcon(new ImageIcon("resources/images/layoutOff.png"));
			button.setToolTipText("AutoLayout des EG ausschalten");
		}
	}

	/**
	 * Erzeugt einen Screenshot des aktuellen Erreichbarkeitsgraphen und speichert
	 * ihn
	 * 
	 * @param panel das Panel von dem ein Screenshot erzeugt werden soll
	 */
	private void screenshot(JPanel panel) {
		BufferedImage bild = new BufferedImage(panel.getSize().width, panel.getSize().height,
				BufferedImage.TYPE_INT_ARGB);
		panel.paintAll(bild.createGraphics());

		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));

		File pfad = new File(System.getProperty("user.home") + File.separator + "screenshot.png");
		chooser.setSelectedFile(pfad);
		chooser.showSaveDialog(null);

		pfad = chooser.getSelectedFile();
		try {
			datei.createNewFile();
			ImageIO.write(bild, "png", pfad);
		} catch (Exception ex) {
		}

	}
	

	/**
	 * Wird vom Controller genutzt, um Text in die TextArea zu schreiben.
	 * 
	 * @param text zu schreibender Text
	 */
	public void addText(String text) {
		textArea.append(text);
	}

	/**
	 * Öffnet nach der Analyse ein Dialogfenster, das über die (Un-)Beschränktheit
	 * des Petrinetzes informiert.
	 * 
	 * @param beschraenkt true wenn beschränkt, false wenn unbeschränkt
	 */
	public void showAnalyseErgebnis(Boolean beschraenkt) {
		String ergebnis = beschraenkt ? "beschränkt." : "unbeschränkt.";
		JOptionPane.showMessageDialog(this, "Das Petrinetz ist " + ergebnis);
	}


}
