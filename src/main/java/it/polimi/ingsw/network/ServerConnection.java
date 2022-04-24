package it.polimi.ingsw.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is a runnable which handles the server's socket by listening for new connection. When
 * a new client connects, a PlayerConnection is created and started. The PlayerConnection will need
 * to register itself to the Server when the client sets up the player information.
 */
public class ServerConnection implements Runnable
{
    private Server server;

    private ExecutorService executor;

    private int port = 1234;

    private boolean active = true;

    ServerConnection(Server server)
    {
        this.server = server;
        executor = Executors.newCachedThreadPool();
    }

    public synchronized boolean isActive()
    {
        return active;
    }

    public synchronized void setActive(boolean active)
    {
        this.active = active;
    }

    public void acceptConnections(ServerSocket serverSocket)
    {
        while (isActive())
        {
            try
            {
                // For now a new PlayerConnection starts running, when the user will set up it's
                // info the class will register itself to the server
                executor.submit(new PlayerConnection(server, serverSocket.accept()));
                System.out.println("[ServerConnection] Accepted new player");
            } catch (IOException e)
            {
                System.err.println("[Error] " + e.getMessage());
            }
        }
    }

    @Override
    public void run()
    {
        try
        {
            ServerSocket socket = new ServerSocket(port);
            System.out
                    .println("[ServerConnection] Server socket started, listening on port " + port);

            acceptConnections(socket);
        } catch (IOException e)
        {
            System.err
                    .println("[ServerConnection] Error during Socket initialization, quitting...");
            System.exit(-1);
        }
    }
}
