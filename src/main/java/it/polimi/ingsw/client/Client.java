package it.polimi.ingsw.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import it.polimi.ingsw.client.cli.utils.PrintHelper;
import java.net.SocketException;
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

    private Visualizable visualizer;

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

    /**
     * Set a visualizer either a CLI or a GameView.
     *
     * @param visualizer to use.
     */
    public void setVisualizer(Visualizable visualizer)
    {
        this.visualizer = visualizer;
    }

    /**
     * Set the name of the player using this client.
     *
     * @param playerName
     */
    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    /**
     * Tries to connect to the server.
     *
     * @throws IOException if there is a problem establishing connection to server.
     */
    public void connect() throws IOException
    {
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), 2000);

        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Send a Command to the server.
     *
     * @param command to send.
     * @throws IOException if there ia a problem while sending.
     */
    public void sendCommand(Command command) throws IOException
    {
        try
        {
            synchronized (outputStream)
            {
                outputStream.writeObject(command);
                outputStream.flush();
                outputStream.reset();
            }
        } catch (SocketException e)
        {
            // This exception is thrown when the server goes down
            visualizer.displayConnectionError(new ErrorAnswer("The server is currently down, the match ends here."));
        } catch (IOException e)
        {
            PrintHelper.printMessage("Error while writing: " + e.getMessage());
            stop();
        }
    }

    /**
     * Send an ActionMessage to the server.
     *
     * @param action to send.
     * @throws IOException if there ia a problem while sending.
     */
    public void sendAction(ActionMessage action) throws IOException
    {
        try
        {
            synchronized (outputStream)
            {
                outputStream.writeObject(action);
                outputStream.flush();
                outputStream.reset();
            }
        } catch (SocketException e)
        {
            // This exception is thrown when the server goes down
            visualizer.displayConnectionError(new ErrorAnswer("The server is currently down, the match ends here."));
        } catch (IOException e)
        {
            PrintHelper.printMessage("Error while writing: " + e.getMessage());
            stop();
        }
    }

    public String getPlayerName()
    {
        return playerName;
    }

    /**
     * Close the connection with the server closing the socket.
     *
     * @throws IOException
     */
    public synchronized void stop() throws IOException
    {
        active = false;
        socket.close();
    }

    /**
     * Returns if the client is active or not.
     *
     * @return client status.
     */
    public synchronized boolean isActive()
    {
        return active;
    }

    @Override
    /**
     * Starts a thread that sends PingCommand to the server every second, so that the server knows if this
     * client is still connected.
     * In this method the client receives messages from the server and sort Answer and ModelUpdate.
     */
    public void run()
    {
        active = true;

        // Start a ping thread
        Executors.newCachedThreadPool().submit(() -> {
            while (true)
                try
                {
                    synchronized (outputStream)
                    {
                        outputStream.writeObject(new PingCommand());
                        outputStream.flush();
                        outputStream.reset();
                    }
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
        } catch (SocketException e)
        {
            // This exception is thrown when the server goes down
            visualizer.displayConnectionError(new ErrorAnswer("The server is currently down, the match ends here."));
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

    /**
     * Set the IP address of the server.
     *
     * @param ip of the server.
     */
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /**
     * Set the port of the server.
     *
     * @param port of the server.
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    public String getIp()
    {
        return ip;
    }

    public int getPort()
    {
        return port;
    }
}
