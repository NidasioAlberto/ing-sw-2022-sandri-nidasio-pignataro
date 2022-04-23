package it.polimi.ingsw.network;

import java.net.Socket;
import org.json.JSONObject;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.network.commands.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlayerConnection implements Runnable
{
    private Socket playerSocket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    private Server server;

    private String playerName;

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

    /**
     * Tells whether the PlayerConnection is listening over the socket.
     * 
     * @throws IOException When the client is not connected anymore.
     */
    public synchronized boolean isActive() throws IOException
    {
        return active;
    }

    public Socket getPlayerSocket()
    {
        return playerSocket;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    /**
     * Terminates the connection with the client,.
     */
    public void close() throws IOException
    {
        // TODO: Remove the player from the server

        playerSocket.close();
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
            // TODO: Handle what happens when a player suddenly disconnects
        } catch (ClassNotFoundException e)
        {
            System.out.println("SEVERE ERROR!");
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
        ActionMessage action = ActionMessage.buildActionMessage(actionJson);
        server.actionCall(action, this);
    }
}
