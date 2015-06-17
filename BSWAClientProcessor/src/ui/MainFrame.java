package ui;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import client.ClientStatus;

public class MainFrame extends JFrame implements IUIInterface {

	private PanelTop top;

	private PanelMiddle middle;

	public MainFrame() {
		setSize(500, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());

		top = new PanelTop();
		getContentPane().add(top, BorderLayout.NORTH);

		middle = new PanelMiddle();
		getContentPane().add(middle, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void clientStatusChanged(ClientStatus status) {
		top.setStatus(status);
	}

	@Override
	public void clientConnectionChanged(boolean isConnectedToServer, Info serverInfo) {
		if (isConnectedToServer) {
			top.setServerInfo(serverInfo);
		} else {
			top.setServerInfo(null);
		}
	}

	@Override
	public void clearDisplayedInfoChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				middle.setCurrentInfos(new DisplayInfo());
			}
		});
	}

	@Override
	public void addDisplayedInfoChanged(final Info info) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				middle.getCurrentInfos().getListinfos().add(info);
				middle.repaint();
			}
		});

	}

	@Override
	public void replaceDisplayInfo(final Info info) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				List<Info> infos = middle.getCurrentInfos().getListinfos();

				boolean found = false;
				for (Iterator<Info> iterator = infos.iterator(); iterator.hasNext();) {
					Info currentInfo = (Info) iterator.next();
					if (currentInfo.getProperty().equals(info.getProperty())) {
						currentInfo.setValue(info.getValue());
						found = true;
						break;
					}
				}
				if (!found) {
					infos.add(info);
				}
				middle.repaint();
			}
		});
	}

}
