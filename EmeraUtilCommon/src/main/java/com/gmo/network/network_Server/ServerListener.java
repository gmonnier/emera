package com.gmo.network.network_Server;



/**
 * This listener is used to listen to string which
 * transit on the network through the server.
 * @author Romain Guy
 */

public interface ServerListener
{
  /**
   * Any class implemeting this will receive all the
   * messages which pass through the server.
   * @param evt A <code>ServerEvent</code> containing the received
   * String and the <code>CaffeineSocket</code> from which it arrived
   */

  public abstract void serverEvent(ServerEvent evt);
}