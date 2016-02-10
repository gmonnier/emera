package com.gmo.network.network_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

/**
 * The CaffeineSocket acts as a normal socket but provides more functions. For
 * instance, you can directly get the writer and the reader. Moreover, this
 * socket dispatches the String sent through it to server to the other clients
 * 
 * @author Romain Guy
 */
public class ExtendedSocket implements Runnable {
	// to send String to the client

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private PrintWriter writer;
	// to receive String from the client
	private BufferedReader reader;
	// infos about the client
	private InetAddress clientInetAdress;
	// the socket
	private Socket socket;
	// the dispatching thread
	private Thread sender;
	// parent
	private BaseServer parentServ;

	/**
	 * Create a new CaffeineSocket, constructed from a standard Socket.
	 * 
	 * @param socket
	 *            The <code>Socket</code> used to construct the
	 *            <code>CaffeineSocket</code>
	 */
	public ExtendedSocket(BaseServer parentServ, Socket socket) throws SocketException {
		this.socket = socket;
		this.parentServ = parentServ;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		} catch (IOException ioe) {
			throw new SocketException(socket, "Cannot open streams to/from client.");
		}

		clientInetAdress = socket.getInetAddress();

		// the thread which handles the dispatching
		sender = new Thread(this);
		sender.setName("[BSWA SERVER] Thread listening on : " + getIP() + ": Client: " + getName());
		sender.setPriority(Thread.NORM_PRIORITY);
		sender.start();
	}

	/**
	 * Each Socket manages its messages
	 */
	public void run() {
		String request;

		while (sender != null) {
			try {
				if ((request = reader.readLine()) != null && !request.isEmpty()) {
					parentServ.fireEvent(request, this);
				}
			} catch (IOException ioe) {
				LOG.error("IOException while reading on socket " + clientInetAdress.getHostAddress(), ioe);
				parentServ.killClient(getIP());
				return;
			}

			Thread.yield();

		}

		LOG.debug("Exit run in reading thread for socket " + clientInetAdress.getHostAddress());
	}

	/**
	 * Required to receive String from the client.
	 * 
	 * @return A <code>BufferedReader</code>
	 */
	public BufferedReader getIn() {
		return reader;
	}

	/**
	 * Required to send String to the client.
	 * 
	 * @return A <code>PrintWriter</code>
	 */
	public void printOutput(String str) {
		writer.println(str);
	}

	/**
	 * Needed to compare clients.
	 * 
	 * @return The client's IP
	 */
	public String getIP() {
		return clientInetAdress.getHostAddress();
	}

	/**
	 * 
	 * @return The client's IP
	 */
	public InetAddress getInetAdress() {
		return clientInetAdress;
	}

	/**
	 * Needed to compare clients.
	 * 
	 * @return The client's computer name
	 */
	public String getName() {
		return clientInetAdress.getCanonicalHostName();// client.getHostName();
	}

	/**
	 * Kill the socket and close its streams.
	 */
	public void close() {
		LOG.debug("Closing extended socket " + getIP());
		
		sender = null;
		writer.close();
		try {
			reader.close();
		} catch (IOException ioe) {
			LOG.error("io exception when closing reader");
		}
		try {
			socket.close();
		} catch (IOException ioe) {
			LOG.error("io exception when closing socket");
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientInetAdress == null) ? 0 : clientInetAdress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedSocket other = (ExtendedSocket) obj;
		if (clientInetAdress == null) {
			if (other.clientInetAdress != null)
				return false;
		} else if (!clientInetAdress.equals(other.clientInetAdress))
			return false;
		return true;
	}

}
