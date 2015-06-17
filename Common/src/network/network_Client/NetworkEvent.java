package network.network_Client;
/**
 * A network event, fired by CaffeineClient and used by applications
 * to receive objects from server.
 * @author Romain Guy
 */

public class NetworkEvent
{
  // the embedded object
  private Object obj;

  /**
   * Create a new NetworkEvent which will be used by applications
   * @param obj The embedded object
   */

  public NetworkEvent(Object obj)
  {
    this.obj = obj;
  }

  /**
   * The application will resolve the object for their own internal
   * work.
   * @return The embedded object
   */

  public Object getObject()
  {
    return obj;
  }

  /**
   * This can avoid applications to type-cast a string
   * @return A string representation of the object
   */

  public String getString()
  {
    return (String) obj;
  }
}