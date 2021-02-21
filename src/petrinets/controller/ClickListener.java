package petrinets.controller;

import org.graphstream.ui.view.ViewerListener;

/**
 * Dieser Listener reagiert auf Klicks in der Anzeige der Graphen.
 * Er wird für beide Graphen erzeugt und erhält den Namen des jeweiligen
 * Graphen als Attribut, zur besseren Zuordnung im Controller.
 */
public class ClickListener implements ViewerListener {

	private Controller controller;
	private String name;

	/**
	 * Im Konstruktor wird der Controller und der Name des zugehörigen
	 * Graphen gesetzt.
	 * 
	 * @param controller Controller der Anwendung
	 * @param name Name des Graphen auf den der Listener hört
	 */
	public ClickListener(Controller controller, String name) {
		this.controller = controller;
		this.name = name;
	}

	@Override
	public void viewClosed(String viewName) {
		System.out.println("ClickListener - viewClosed: " + viewName);
		// wird nicht verwendet
	}

	@Override
	public void buttonPushed(String id) {
		System.out.println("ClickListener - buttonPushed: " + id);

		if (controller != null) {
			controller.clickNode(id, name);
		}
	}

	@Override
	public void buttonReleased(String id) {
		System.out.println("ClickListener - buttonReleased: " + id);
		// wird nicht verwendet
	}
}