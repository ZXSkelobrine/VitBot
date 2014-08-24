package com.github.zxskelobrine.networking.irc.bots.store.managers.external;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.github.zxskelobrine.networking.irc.bots.store.windows.StatusWindow;

public class TrayManager {

	public static String iconPath = "/icons/icon.png";
	private static TrayIcon trayIcon;
	private static SystemTray tray;

	/**
	 * This method will enable the tray icon for the program.
	 * 
	 * @param window
	 *            - The frame that is enabling the tray icon.
	 * @throws IOException
	 */
	public static void initializeTray(final StatusWindow window) throws IOException {
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			Image image = ImageIO.read(TrayManager.class.getResource(iconPath));
			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					window.setVisible(true);
					window.toFront();
					tray.remove(trayIcon);
				}
			};
			trayIcon = new TrayIcon(image, "VitBot");
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(listener);
		}
		window.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				if (e.getNewState() == JFrame.ICONIFIED) {
					try {
						window.dispose();
						tray.add(trayIcon);
					} catch (AWTException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

}
