package it.polimi.ingsw.network;

import java.net.Socket;

public class PlayerConnection
{
    private Socket playerSocket;

    private String playerName;

    private Server server;

    public void listenForCommands()
    {
        // ...

    }

    public void setPlayerName(String name)
    {
        // ...
    }

    public Server getServer()
    {
        return server;
    }
}
