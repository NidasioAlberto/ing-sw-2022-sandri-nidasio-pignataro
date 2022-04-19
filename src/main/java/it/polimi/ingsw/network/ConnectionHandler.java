package it.polimi.ingsw.network;

import java.net.ServerSocket;

public class ConnectionHandler
{
    private ServerSocket serverSocket;

    private Server server;

    ConnectionHandler(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void listenConnection()
    {
        // ...
    }
}
