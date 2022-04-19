package it.polimi.ingsw.network;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayerConnection implements Runnable
{
    private Socket playerSocket;

    private Server server;

    private String playerName;

    private boolean active;

    PlayerConnection(Socket playerSocket, Server server) throws IOException
    {
        this.playerSocket = playerSocket;
        this.server = server;

        active = true;
    }

    /**
     * Terminates the connection with the client,.
     */
    public void close() throws IOException
    {
        playerSocket.close();
    }

    /**
     * Tells whether the PlayerConnection is listening over the socket.
     * 
     * @throws IOException When the client is not connected anymore.
     */
    public synchronized boolean isActive() throws IOException
    {
        return active;
    }

    /**
     * Reads a message from the socket
     */
    public synchronized void readMessage()
    {
        // ...
    }

    @Override
    public void run()
    {
        try
        {
            while (isActive())
                readMessage();
        } catch (IOException e)
        {
            // Handle what happens when a player suddenly disconnects
        }
    }

    public void handleCommand(String command)
    {

    }

    /**
     * This is an active thread that listen to messages coming from the client.
     */
    public void listenForCommands()
    {}

    public void setPlayerName(String name)
    {
        // ...
    }

    public Socket getPlayerSocket()
    {
        return playerSocket;
    }

    public Server getServer()
    {
        return server;
    }
}
