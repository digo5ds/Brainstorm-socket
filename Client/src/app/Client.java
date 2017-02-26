package app;

import java.awt.EventQueue;

import javax.swing.UIManager;

import view.ClientFrame;

public class Client {


	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}


		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientFrame frame = new ClientFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
