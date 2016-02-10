package com.gmo.network.network_Server;



/**
 * A server event, fired by CaffeineServer when it receive
 * a string from any client.
 * @author Romain Guy
 */

public class ServerEvent
{
  // the socket sender
  private ExtendedSocket socket;
  // the received string
  private String line;

  /**
   * Create a new ServerEvent which will be used by applications
   * to manage the server.
   * @param line The <code>String</code> received
   * @param socket The sender
   */

  public ServerEvent(String line, ExtendedSocket socket)
  {
    this.line = line;
    this.socket = socket;
  }

  /**
   * The application will be able to use the sender's attributes
   * to monitor the server.
   * @return The <code>CaffeineSocket</code> sender
   */

  public ExtendedSocket getSender()
  {
    return socket;
  }

  /**
   * The application will be able to use the received line
   * to monitor the server or to write a log.
   * @return The <code>String</code> received
   */

  public String getString()
  {
    return line;
  }
}

// End of ServerEvent.java