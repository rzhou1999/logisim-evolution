/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/

package com.cburch.logisim.gui.start;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

//MAC import com.apple.eawt.Application;
import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.proj.ProjectActions;
import com.cburch.logisim.util.Icons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.roydesign.event.ApplicationEvent;
import net.roydesign.mac.MRJAdapter;

class MacOsAdapter {

	final static Logger logger = LoggerFactory.getLogger(MacOsAdapter.class);

	private static class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ApplicationEvent event2 = (ApplicationEvent) event;
			int type = event2.getType();
			switch (type) {
			case ApplicationEvent.ABOUT:
				About.showAboutDialog(null);
				break;
			case ApplicationEvent.QUIT_APPLICATION:
				ProjectActions.doQuit();
				break;
			case ApplicationEvent.OPEN_DOCUMENT:
				Startup.doOpen(event2.getFile());
				break;
			case ApplicationEvent.PRINT_DOCUMENT:
				Startup.doPrint(event2.getFile());
				break;
			case ApplicationEvent.PREFERENCES:
				PreferencesFrame.showPreferences();
				break;
			}
		}
	}

	static void addListeners(boolean added) {
		MyListener myListener = new MyListener();
		if (!added)
			MRJAdapter.addOpenDocumentListener(myListener);
		if (!added)
			MRJAdapter.addPrintDocumentListener(myListener);
		MRJAdapter.addPreferencesListener(myListener);
		MRJAdapter.addQuitApplicationListener(myListener);
		MRJAdapter.addAboutListener(myListener);
	}

	private static void setDockIcon() {
		// Retrieve the Image object from the locally stored image file
		// "frame" is the name of my JFrame variable, and "filename" is the name of the image file
		java.net.URL imageURL = Icons.class.getClassLoader().getResource(
				"resources/logisim/img/logisim-icon-128.png");
		Image image = new ImageIcon(imageURL).getImage();

		// Java <= 1.8 uses Application to set icon image
		try {
			// Replace: import com.apple.eawt.Application
			String className = "com.apple.eawt.Application";
			Class<?> cls = Class.forName(className);

			// Replace: Application application = Application.getApplication();
			Object application = cls.getMethod("getApplication").invoke(null);

			// Replace: application.setDockIconImage(image);
			cls.getMethod("setDockIconImage", Image.class).invoke(application, image);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// Java >= 9 uses Taskbar to set icon image
		try {
			// Replace: import java.awt.Taskbar
			String className = "java.awt.Taskbar";
			Class<?> cls = Class.forName(className);

			// Replace: Taskbar taskbar = Taskbar.getTaskbar();
			Object taskbar = cls.getMethod("getTaskbar").invoke(null);

			// Replace: taskbar.setIconImage(image);
			cls.getMethod("setIconImage", Image.class).invoke(taskbar, image);
		}
		catch (ClassNotFoundException e) {
			logger.warn("java.awt.Taskbar not found for Java < 9");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void register() {
		setDockIcon();
	}
}
