package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.gui.lobby.Controllable;
import it.polimi.ingsw.client.gui.lobby.JoinMatchSceneController;
import it.polimi.ingsw.client.gui.lobby.LobbySceneController;
import it.polimi.ingsw.protocol.answers.EndMatchAnswer;
import it.polimi.ingsw.protocol.answers.ErrorAnswer;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;
import it.polimi.ingsw.protocol.answers.SetNameAnswer;
import it.polimi.ingsw.protocol.commands.Command;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class SceneController
{
    private static Scene mainScene;

    private static Client client;

    private static GameView view;

    private static Controllable currentController;

    private static Parent currentRoot;

    // TODO popup per errori

    public SceneController(GameView view, Client client, Scene mainScene)
    {
        if (view != null && client != null && mainScene != null)
        {
            this.view = view;
            this.client = client;
            this.mainScene = mainScene;
        } else
            throw new NullPointerException();
    }

    public void displayError(ErrorAnswer answer)
    {}

    /**
     * If I receive a SetNameAnswer, the nickname inserted by the player is valid, so I change scene to lobby.
     */
    public void displaySetName(SetNameAnswer answer)
    {
        setRoot("/Lobby/lobby.fxml");
        ((LobbySceneController) currentController).displayName(answer);
    }

    /**
     * If I receive a JoinedMatchAnswer, I change scene to the one related to the match.
     */
    public void displayJoinedMatch()
    {
        view.matchBegin();
    }

    public void displayEndMatch(EndMatchAnswer answer)
    {

    }

    /**
     * If I receive a MatchesListAnswer and I am in the matchesList scene I display the list of matches.
     */
    public void displayMatchesList(MatchesListAnswer answer)
    {
        if (currentController instanceof JoinMatchSceneController)
        {
            ((JoinMatchSceneController) currentController).displayMatchesList(answer);
        }
    }

    /**
     * Sets the ip and port of the server so that the client can connect.
     *
     * @param ip of the server.
     * @param port of the server.
     */
    public static void clientConnect(String ip, int port)
    {
        try
        {
            client.setIp(ip);
            client.setPort(port);
            client.connect();
            new Thread(() -> client.run()).start();
        } catch (Exception e)
        {
            System.out.println("Connection error");
            e.printStackTrace();
        }
    }

    /**
     * Asks the client to send the given command to the server.
     *
     * @param command to send.
     */
    public static void sendCommand(Command command)
    {
        try
        {
            if (command != null)
                client.sendCommand(command);
            else
                System.out.println("sei scemo");
        } catch (Exception e)
        {
            System.out.println("Error while sending a command");
            e.printStackTrace();
        }
    }

    /**
     * Change the root of the scene.
     *
     * @param fxml of the new scene.
     */
    public static void setRoot(String fxml)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(SceneController.class.getResource(fxml));
            currentRoot = loader.load();
            currentController = loader.getController();
            mainScene.setRoot(currentRoot);
        } catch (IOException e)
        {
            System.out.println("Error while changing root");
            e.printStackTrace();
        }
    }
}
