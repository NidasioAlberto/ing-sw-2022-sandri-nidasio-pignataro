package it.polimi.ingsw.network;

import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.protocol.answers.Answer;
import it.polimi.ingsw.protocol.answers.EndMatchAnswer;
import it.polimi.ingsw.protocol.answers.ErrorAnswer;
import it.polimi.ingsw.protocol.answers.SetNameAnswer;
import it.polimi.ingsw.protocol.commands.Command;
import it.polimi.ingsw.protocol.commands.PingCommand;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.updates.ModelUpdate;
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

    private Future<?> watchdogTask = null;

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
            System.err.println("[PlayerConnection] Error during initialization of the client: " + e.getMessage());
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
        // Max length is 18 because otherwise it would be too long respect the string representation of the SchoolBoard
        if (playerName.length() >= 18)
        {
            sendAnswer(new ErrorAnswer("The name is too long"));
            return;
        } else
        {
            // Check the playerName isn't empty or composed only by empty characters
            int i;
            for (i = 0; i < playerName.length(); i++)
            {
                if (playerName.charAt(i) != ' ')
                    break;
            }
            if (playerName.length() == 0 || i == playerName.length())
            {
                sendAnswer(new ErrorAnswer("The name is empty"));
                return;
            }
        }

        // Check if there is already a player with this name
        System.out.println("[PlayerConnection] Checking if there is already a player with the name \"" + playerName + "\"");
        for (PlayerConnection playerConnection : server.getLobby())
            if (playerConnection.getPlayerName().isPresent())
                if (playerConnection.getPlayerName().isPresent() && playerConnection.getPlayerName().get().equals(playerName))
                {
                    sendAnswer(new ErrorAnswer("This name is already in use"));
                    return;
                }
        for (Match match : server.getAllMatches().values())
            for (PlayerConnection playerConnection : match.getPlayers())
                if (playerConnection.getPlayerName().isPresent() && playerConnection.getPlayerName().get().equals(playerName))
                {
                    sendAnswer(new ErrorAnswer("This name is already in use"));
                    return;
                }

        this.playerName = Optional.of(playerName);
        sendAnswer(new SetNameAnswer(playerName));

        System.out.println("[PlayerConnection] Checking if the player was in a game");
        for (Map.Entry<String, Match> match : server.getAllMatches().entrySet())
        {
            if (match.getValue().getMissingPlayers().contains(playerName))
            {
                try
                {
                    server.addPlayerToMatch(match.getKey(), this);
                    break;
                } catch (TooManyPlayersException e)
                {
                }
            }

        }
    }

    public void restartWatchdog()
    {
        if (watchdogTask != null)
            watchdogTask.cancel(true);

        watchdogTask = Executors.newCachedThreadPool().submit(() -> {
            try
            {
                Thread.sleep(3000);
                System.out.println("Player connection timed out!");
                sendAnswer(new EndMatchAnswer("Connection timed out"));
                close();
            } catch (InterruptedException e)
            {
            }
        });
    }

    public boolean isInAMatch()
    {
        return server.isPlayerInAMatch(this);
    }

    public void close()
    {
        server.removePlayerFromServer(this);

        try
        {
            playerSocket.close();
        } catch (IOException e)
        {
            System.err.println("[PlayerConnection] An error occurred while closing the player's connection");
            System.err.println("[PlayerConnection] " + e.getMessage());
        }
    }

    @Override
    public void run()
    {
        // Register the player into the server
        server.addPlayerToLobby(this);

        // Start the first watchdog
        restartWatchdog();

        try
        {
            while (isActive())
                handlePacket(inputStream.readObject());
        } catch (IOException e)
        {
            // The player suddenly disconnected, remove it from the server
            server.removePlayerFromServer(this);
        } catch (ClassNotFoundException e)
        {
            System.out.println("SERVER ERROR!");
            System.out.println(e.getMessage());
        }
    }

    public void handlePacket(Object rawPacket)
    {
        if (!(rawPacket instanceof PingCommand))
            System.out.println("[PlayerConnection] New packet received: " + rawPacket.getClass().getSimpleName());

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
        sendObject(answer);
    }

    public void sendModelUpdate(ModelUpdate update)
    {
        sendObject(update);
    }

    private synchronized void sendObject(Object object)
    {
        System.out.println("[PlayerConnection] Sending " + object.getClass().getSimpleName() + " to player " + playerName.orElse(""));
        try
        {
            outputStream.writeObject(object);
            outputStream.flush();
            outputStream.reset();
        } catch (IOException e)
        {
            System.err.println("[PlayerConnection] Error while writing: " + e.getMessage());
            close();
        }
    }
}
