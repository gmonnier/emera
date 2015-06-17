package network.network_Server;

import java.net.Socket;

public class SocketException extends Throwable {
    // the exception 'thrower'

    private Socket socket;
    // the error message
    private String message;

    /**
     * Create a new exception which is normally thrown by the Socket.
     * @param Socket The 'thrower'
     * @param message The error message
     */
    public SocketException(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
    }

    /**
     * If you created a multi-server manager, this can be very useful.
     * @return The <code>Socket</code> which is responsible of the exception
     */
    public Socket getThrower() {
        return socket;
    }

    /**
     * An human readable description of the error.
     * @return A <code>String</code> containing the description of the error
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(message.length());
        buf.append("The ExtendedSocket of IP ").append(socket.getInetAddress().getHostAddress());
        buf.append(" has encountered following error:\n").append(message);
        return buf.toString();
    }
}
