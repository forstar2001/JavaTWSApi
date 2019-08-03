/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package Client.JBClient;

import Client.*;
import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    // This method is called to start the application
    public static void main (String args[]) {
        StartPanel startPanel = new StartPanel();
        startPanel.setVisible(true);
    }

    static public void inform( final Component parent, final String str) {
        if( SwingUtilities.isEventDispatchThread() ) {
        	showMsg( parent, str, JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					showMsg( parent, str, JOptionPane.INFORMATION_MESSAGE);
				}
			});
        }
    }

    static private void showMsg( Component parent, String str, int type) {
        // this function pops up a dlg box displaying a message
        JOptionPane.showMessageDialog( parent, str, "IB Client", type);
    }
}
