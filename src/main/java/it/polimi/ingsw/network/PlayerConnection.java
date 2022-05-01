package it.polimi.ingsw.network;

import java.net.Socket;
import java.util.Optional;
import it.polimi.ingsw.protocol.answers.Answer;
import it.polimi.ingsw.protocol.answers.ErrorAnswer;
import it.polimi.ingsw.protocol.commands.Command;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayerConnection implements Runnable
{
    private Socket playerSocket;

    private ObjectInputStream inputStream;

    private ObjectOutputStream outputStream;

    private Server server;

    private Optional<String> playerName = Optional.empty();

    private boolean active = true;

    PlayerConnection(Server server, Socket playerSocket) throws IOException
    {
        this.server = server;
        this.playerSocket = playerSocket;

        try
        {
            inputStream = new ObjectInputStream(playerSocket.getInputStream());
            outputStream = new ObjectOutputStream(playerSocket.getOutputStream());
        } catch (IOException e)
        {
            System.err.println("[PlayerConnection] Error during initialization of the client: "
                    + e.getMessage());
        }
    }

    public synchronized boolean isActive()
    {
        return active;
    }

    public synchronized void setActive(boolean active)
    {
        this.active = active;
    }

    public Socket getPlayerSocket()
    {
        return playerSocket;
    }

    public Server getServer()
    {
        return server;
    }

    public Optional<String> getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = Optional.of(playerName);
    }

    public void close()
    {
        server.removePlayer(this);

        try
        {
            playerSocket.close();
        } catch (IOException e)
        {
            System.err.println(
                    "[PlayerConnection] An error occurred while closing the player's connection");
            System.err.println("[PlayerConnection] " + e.getMessage());
        }
    }

    @Override
    public void run()
    {
        System.out.println("[PlayerConnection] New player connected");
        try
        {
            while (isActive())
                handlePacket(inputStream.readObject());
        } catch (IOException e)
        {
            // The player suddenly disconnected, remove it from the server
            server.removePlayer(this);
        } catch (ClassNotFoundException e)
        {
            System.out.println("SERVER ERROR!");
            System.out.println(e.getMessage());
        }
    }

    public void handlePacket(Object rawPacket)
    {
        System.out.println(
                "[PlayerConnection] New packet received: " + rawPacket.getClass().getName());

        try
        {
            // If the packet contains an action handle it
            if (rawPacket instanceof Command)
                ((Command) rawPacket).applyCommand(this);

            // If the packet contains an action handle it
            else if (rawPacket instanceof ActionMessage)
                server.applyAction((ActionMessage) rawPacket, this);

            // If the packet isn't recognized, this is a major error
            else
                System.err.println("[PlayerConnection] Packet not recognized!");
        } catch (Exception e)
        {
            sendAnswer(new ErrorAnswer(e.getMessage()));
        }
    }

    public void sendAnswer(Answer answer)
    {
        try
        {
            outputStream.writeObject(answer);
        } catch (IOException e)
        {
            System.err.println("[PlayerConnection] Error while writing answer: " + e.getMessage());
            close();
        }
    }
}
