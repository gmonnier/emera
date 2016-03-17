package com.gmo.network.network_Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public class BaseServer implements Runnable {

	private ServerSocket serverSocket;
	// the clients seeker
	private Thread seeker;
	private volatile boolean alive = true;
	private ArrayList<ExtendedSocket> listeClients;
	
	// this array contains all the listeners linked to this class
	public ArrayList<ServerListener> listeners = new ArrayList<ServerListener>();

	public INetworkServerInfoListener networkInfoListener;
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	/**
	 * Constructor
	 * 
	 * @param numPortEcoute
	 * @param typeClient
	 */
	public BaseServer(int numPortEcoute, INetworkServerInfoListener networkInfoListener) {
		listeClients = new ArrayList<ExtendedSocket>();
		this.networkInfoListener = networkInfoListener;
		startServer(numPortEcoute);

		if (serverSocket == null) {
			LOG.debug("[Server is not running]");
			System.exit(1);
		}
	}

	/**
	 * Start the server on port portEcoute
	 * 
	 * @param portEcoute
	 */
	public void startServer(int portEcoute) {
		try {
			serverSocket = new ServerSocket(portEcoute);
			serverSocket.setSoTimeout(1000);
		} catch (IOException ioe) {
			if (networkInfoListener != null) {
				networkInfoListener.initialisationError("Error while attempting to connecting port ne" + portEcoute);
			}
		}

		startSeeker();
	}

	/**
	 * This method get a Client from a ServerSocket.
	 * 
	 * @return true if we managed to get a client
	 */
	private void getClient() {
		Socket client;
		try {
			// wait for a client to connect on our port
			client = serverSocket.accept();

			new Authorizer(client);

		} catch (IOException ioe) {
			if (seeker == null) {
				alive = false;
			}
			return;
		}
	}

	/**
	 * Start the seeker thread
	 */
	public void startSeeker() {
		seeker = new Thread(this);
		seeker.setName("[SOCK SERVER] Thread client seeker:  Client Seeker");
		seeker.setPriority(Thread.MIN_PRIORITY);
		seeker.start();
	}

	/**
	 * This resolves clients requests. If we have enough clients, we stop the
	 * thread.
	 */
	public void run() {
		while (alive) {
			getClient();
		}
	}

	/**
	 * Stop the seeker by killing the thread.
	 */
	public void stopSeeker() {
		seeker = null;
	}

	/**
	 * If the client exits, we kill it.
	 * 
	 * @param IP
	 *            The IP of the client to be killed
	 */
	public synchronized void killClient(String IP) {
		for (int i = 0; i < listeClients.size(); i++) {
			ExtendedSocket kill = (ExtendedSocket) listeClients.get(i);
			if (kill.getIP().equals(IP)) {
				LOG.debug("send kill message to " + IP);
				kill.printOutput("/kill");
				LOG.debug("close client connection");
				kill.close();
				if (networkInfoListener != null) {
					networkInfoListener.clientRemoved(kill);
				}
				listeClients.remove(kill);
			}
		}
	}

	/**
	 * Remove a client from the list of client
	 * 
	 * @param IP
	 *            IP of the client
	 */
	public synchronized void removeClientFromList(String IP) {
		for (int i = 0; i < listeClients.size(); i++) {
			ExtendedSocket kill = (ExtendedSocket) listeClients.get(i);
			if (kill.getIP().equals(IP)) {
				kill.close();
				if (networkInfoListener != null) {
					networkInfoListener.clientRemoved(kill);
				}
				listeClients.remove(kill);
				
				return;
			}
		}
	}

	/**
	 * Add a client to the list only if we haven't reached the limit.
	 * 
	 * @param client
	 *            A <code>CaffeineSocket</code> refering to a client
	 */
	public synchronized boolean addClient(ExtendedSocket client) {
		
		boolean contains = false;
		for (int i = 0; i < listeClients.size(); i++) {
			if(listeClients.get(i).getIP().equals(client.getIP())){
				contains = true;
			}
		}
		
		if(contains) {
			return false;
		} else {
			listeClients.add(client);
			if (networkInfoListener != null) {
				networkInfoListener.clientAdded(client);
			}
			return true;
		}
	}

	/**
	 * Removes all the listeners associated with this class.
	 */
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * Adds a server listener to this class.
	 * 
	 * @param l
	 *            The <code>NetworkListener</code> to add
	 */
	public void addServerListener(ServerListener l) {
		listeners.add(l);
	}

	/**
	 * Remove a specified server listener from the list.
	 * 
	 * @param l
	 *            The listener to remove
	 */
	public void removeServerListener(ServerListener l) {
		listeners.remove(l);
	}

	/**
	 * Send a network event to the listeners. This happens when we receive a
	 * string from the server through network.
	 * 
	 * @param o
	 *            The object to be embedded into the event
	 */
	public synchronized void fireEvent(String line, ExtendedSocket socket) {
		ServerEvent e = new ServerEvent(line, socket);
		// we send the event to all the listeners available
		for (int i = 0; i < listeners.size(); i++) {
			try {
				((ServerListener) listeners.get(i)).serverEvent(e);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @return the list of connected clients
	 */
	public ArrayList<ExtendedSocket> getListeClients() {
		return listeClients;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	class Authorizer extends Thread {

		private Socket client;

		Authorizer(Socket client) {
			super();

			this.client = client;

			setPriority(MIN_PRIORITY);
			start();
		}

		/**
		 * Start the thread
		 */
		public void run() {
			if (client == null) {
				return;
			}

			try {
				InputStream in = client.getInputStream();
				OutputStream out = client.getOutputStream();
				if (in == null || out == null) {
					return;
				}

				PrintWriter writer = new PrintWriter(new OutputStreamWriter(out), true);
				writer.println("You are now logged on the server ");
				writer.flush();
				
				InetAddress url = client.getInetAddress();

				try {
					boolean accepted = addClient(new ExtendedSocket(BaseServer.this, client));
					if(!accepted) {
						LOG.warn("Server did not accpet client : " + url.getHostAddress() + " because a identical connected IP adress is already presents in the list");
						writer.println("/refused");
						return;
					}
				} catch (SocketException cse) {
					LOG.error(cse.toString() + "with" + url.getHostAddress());
					return;
				}

				LOG.info("Client joined : Client IP: " + url.getHostAddress() + "; Client name: " + url.getHostName());

			} catch (NullPointerException npe) {
				return;
			} catch (IOException ioe) {
				return;
			}
		}
	}
}
