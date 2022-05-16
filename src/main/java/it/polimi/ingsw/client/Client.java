package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.commands.Command;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.updates.*;

public class Client implements Runnable
{
    private String playerName;

    private String ip;

    private int port;

    private Socket socket;

    private ObjectOutputStream outputStream;

    private ObjectInputStream inputStream;

    private Visualizer visualizer;

    private boolean active = false;

    public Client()
    {
        this("127.0.0.1");
    }

    public Client(String ip)
    {
        this(ip, 2345);
    }

    public Client(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }

    public void setVisualizer(Visualizer visualizer)
    {
        this.visualizer = visualizer;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public void connect() throws IOException
    {
        socket = new Socket(ip, port);

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void sendCommand(Command command) throws IOException
    {
        outputStream.writeObject(command);
    }

    public void sendAction(ActionMessage action) throws IOException
    {
        outputStream.writeObject(action);

    }

    public String getPlayerName()
    {
        return playerName;
    }

    public synchronized void stop() throws IOException
    {
        active = false;
        socket.close();
    }

    public synchronized boolean isActive()
    {
        return active;
    }

    @Override
    public void run()
    {
        active = true;

        try
        {
            while (isActive())
            {
                Object input = inputStream.readObject();

                // Updates
                if (input instanceof AssistantCardsUpdate)
                    visualizer.displayAssistantCards((AssistantCardsUpdate) input);
                else if (input instanceof CharacterCardPayloadUpdate)
                    visualizer.displayCharacterCardPayload((CharacterCardPayloadUpdate) input);
                else if (input instanceof CharacterCardsUpdate)
                    visualizer.displayCharacterCards((CharacterCardsUpdate) input);
                else if (input instanceof CloudTilesUpdate)
                    visualizer.displayCloudTiles((CloudTilesUpdate) input);
                else if (input instanceof IslandsUpdate)
                    visualizer.displayIslands((IslandsUpdate) input);
                else if (input instanceof PlayedAssistantCardUpdate)
                    visualizer.displayPlayedAssistantCard((PlayedAssistantCardUpdate) input);
                else if (input instanceof SchoolBoardUpdate)
                    visualizer.displaySchoolboard((SchoolBoardUpdate) input);

                // Answers
                else if (input instanceof EndMatchAnswer)
                    visualizer.displayEndMatch((EndMatchAnswer) input);
                else if (input instanceof ErrorAnswer)
                    visualizer.displayError((ErrorAnswer) input);
                else if (input instanceof JoinedMatchAnswer)
                    visualizer.displayJoinedMatch((JoinedMatchAnswer) input);
                else if (input instanceof MatchesListAnswer)
                    visualizer.displayMatchesList((MatchesListAnswer) input);
                else if (input instanceof SetNameAnswer)
                    visualizer.displaySetName((SetNameAnswer) input);
                else if (input instanceof StartMatchAnswer)
                    visualizer.displayStartMatch((StartMatchAnswer) input);

                // Unrecognized
                else
                    visualizer.displayError(new ErrorAnswer("[Client] Unable to recognize the received object: " + input.getClass().getName()));
            }
        } catch (IOException e)
        {
            visualizer.displayError(new ErrorAnswer("[Client] Error while reading an object: " + e.getMessage()));
        } catch (ClassNotFoundException e)
        {
            visualizer.displayError(new ErrorAnswer("SEVERE ERROR! " + e.getMessage()));
        } catch (Error e)
        {
            visualizer.displayError(new ErrorAnswer("[Client] Generic error: " + e.getMessage()));
        }
    }
}
