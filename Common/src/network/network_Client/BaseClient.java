package network.network_Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

public class BaseClient implements Runnable {

	private Socket server = null;
	private Thread network;
	// input Stream (From server)
	private BufferedReader reader;
	// Output Stream (From Client)
	private PrintWriter writer;
	// this array contains all the listeners linked to this class
	public List<NetworkListener> listeners;

	// Server Adress
	private InetAddress urlServer;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public BaseClient(String hostName, int numPortEnvoi) throws IOException {
		
		listeners = new ArrayList<>();

		try {
			InetAddress ipaddress = InetAddress.getByName(hostName);
			LOG.debug("IP address found for server: " + ipaddress.getHostAddress());

			server = new Socket(ipaddress, numPortEnvoi);
			reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(server.getOutputStream()), true);

		} catch (UnknownHostException e) {
			LOG.warn("Could not find IP address for: " + hostName);
		}

		urlServer = server.getInetAddress();
		LOG.debug("Server IP: " + urlServer.getHostAddress() + "; Server name: " + urlServer.getHostName());

		network = new Thread(this);
		network.setName("Listening CLient Thread - Logged on: " + urlServer.getHostAddress());
		network.setPriority(Thread.MAX_PRIORITY);
		network.start();
	}

	/**
	 * Removes all the listeners associated with this client.
	 */
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * Adds a network listener to this class.
	 * 
	 * @param l
	 *            The <code>NetworkListener</code> to add
	 */
	public void addNetworkListener(NetworkListener l) {
		listeners.add(l);
	}

	/**
	 * Remove a specified network listener from the list.
	 * 
	 * @param l
	 *            The listener to remove
	 */
	public void removeNetworkListener(NetworkListener l) {
		listeners.remove(l);
	}

	/**
	 * Send a network event to the listeners. This happens when we receive a
	 * string from the server through network.
	 * 
	 * @param o
	 *            The object to be embedded into the event
	 */
	public void fireEvent(Object o) {
		NetworkEvent e = new NetworkEvent(o);
		// we send the event to all the listeners available
		for (int i = 0; i < listeners.size(); i++) {
			try {
				((NetworkListener) listeners.get(i)).networkEvent(e);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * Send an event networkDisconnected to all listener
	 *
	 * @param string
	 *            The event to send
	 */
	public void fireDisconnectEvent(String string) {
		for (int i = 0; i < listeners.size(); i++) {
			((NetworkListener) listeners.get(i)).networkDisconnected(string);
		}
	}

	/**
	 * Start the thread of catching event of killing client
	 *
	 */
	@Override
	public void run() {
		String line;
		while (network != null) {
			try {
				if ((line = reader.readLine()) != null) {
					fireEvent(line);
				}
			} catch (IOException ioe) {
				fireDisconnectEvent("[Client error]\n" + ioe);
				stop();
				fireEvent("/kill");

			}
			Thread.yield();
		}
	}

	/**
	 * Forces the thread to stop and close server.
	 */
	public void stop() {
		network = null;

		sendString("/kill");

		// we wait .5 second to ensure our '/kill' message has
		// been sent. Otherwise, Java encounters socket problem
		try {
			Thread.sleep(500);
		} catch (InterruptedException ie) {
		}
		writer.close();
		try {
			server.close();
		} catch (IOException ioe) {
			LOG.debug("IOException on 'close server' in BaseClient");
		}
		
	}

	/**
	 * Use to send trace to Network Log File
	 */
	public void sendString(String s) {
		if (writer != null) {
			writer.println(s);
			writer.flush();
		} else {
			LOG.debug("Writter is null");
		}
	}
}
