package petrinets;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import petrinets.view.Hauptfenster;

/**
 * Diese Klasse enthält die main Methode zum Starten des Programms.
 */
public class Petrinets {

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Hauptfenster("Petrinets");
			}
		});

	}

}
