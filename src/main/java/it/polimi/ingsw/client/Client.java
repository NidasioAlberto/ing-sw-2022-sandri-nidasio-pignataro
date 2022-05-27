package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.commands.Command;
import it.polimi.ingsw.protocol.commands.PingCommand;
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

        // Start a ping thread
        Executors.newCachedThreadPool().submit(() -> {
            while (true)
                try
                {
                    outputStream.writeObject(new PingCommand());
                    Thread.sleep(1000);
                } catch (Exception e)
                {
                    break;
                }
        });

        try
        {
            while (isActive())
            {
                Object input = inputStream.readObject();

                // Updates
                if (input instanceof ModelUpdate)
                    ((ModelUpdate) input).handleUpdate(visualizer);

                // Answers
                else if (input instanceof Answer)
                    ((Answer) input).handleAnswer(visualizer);

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
