package it.polimi.ingsw.network;

import java.net.Socket;
import java.util.Optional;
import org.json.JSONObject;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.commands.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayerConnection implements Runnable
{
    private Socket playerSocket;

    ObjectInputStream inputStream;

    ObjectOutputStream outputStream;

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
            System.err.println("Error during initialization of the client!");
            System.err.println(e.getMessage());
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
                    "[Player connection] An error occurred while closing the player's connection");
            System.err.println("[Player connection] " + e.getMessage());
        }
    }

    @Override
    public void run()
    {
        try
        {
            while (isActive())
            {
                String rawData = (String) inputStream.readObject();
                handlePacket(rawData);
            }
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

    public void handlePacket(String rawData)
    {
        // Decode the json
        JSONObject packet = new JSONObject(rawData);

        // If the packet contains an action handle it
        if (packet.has("command"))
            handleCommand(packet.getJSONObject("command"));

        // If the packet contains an action handle it
        if (packet.has("action"))
            handleAction(packet.getJSONObject("action"));
    }

    public void handleCommand(JSONObject commandJson)
    {
        Command command = Command.buildCommand(commandJson);
        command.applyCommand(this);
    }

    public void handleAction(JSONObject actionJson)
    {
        //ActionMessage action = ActionMessage.buildActionMessage(actionJson);
        //server.actionCall(action, this);
    }
}
