package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.gui.lobby.Controllable;
import it.polimi.ingsw.client.gui.lobby.LobbySceneController;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;
import it.polimi.ingsw.protocol.answers.SetNameAnswer;
import it.polimi.ingsw.protocol.commands.Command;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;

import java.io.IOException;

public class SceneController
{
    private Scene mainScene;

    private Client client;

    private GameView view;

    private Controllable currentController;

    private Parent currentRoot;

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

    /**
     * If I receive a MatchesListAnswer and I am in the matchesList scene I display the list of matches.
     */
    public void displayMatchesList(MatchesListAnswer answer)
    {
        if (currentController instanceof LobbySceneController)
        {
            ((LobbySceneController) currentController).displayMatchesList(answer);
        }
    }

    /**
     * Sets the ip and port of the server so that the client can connect.
     *
     * @param ip of the server.
     * @param port of the server.
     * @return true if the connection is established.
     */
    public boolean clientConnect(String ip, int port)
    {
        try
        {
            client.setIp(ip);
            client.setPort(port);
            client.connect();
            new Thread(() -> client.run()).start();

            return true;
        } catch (Exception e)
        {
            displayError("Unable to connect to the server");

            return false;
        }
    }

    /**
     * Asks the client to send the given command to the server.
     *
     * @param command to send.
     */
    public void sendCommand(Command command)
    {
        try
        {
            if (command != null)
                client.sendCommand(command);
            else
                displayError("The command is null");
        } catch (Exception e)
        {
            displayError("Error while sending a command");
        }
    }

    /**
     * Change the root of the scene.
     *
     * @param fxml of the new scene.
     */
    public void setRoot(String fxml)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(SceneController.class.getResource(fxml));
            currentRoot = loader.load();
            currentController = loader.getController();
            mainScene.setRoot(currentRoot);
            currentController.initialize(this);
        } catch (IOException e)
        {
            displayError("Error while changing root");
            e.printStackTrace();
        }
    }

    /**
     * Display an alert with the given message.
     * @param errorMessage to display.
     */
    public void displayError(String errorMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(errorMessage);
        alert.show();
    }

    /**
     * Display an alert with the given message and close the game.
     * @param errorMessage to display.
     */
    public void displayConnectionError(String errorMessage)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(errorMessage);

        // Terminate the application if the player closes the alert
        alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent e) {
                System.exit(0);
            }
        });

        // Terminate the application if the player presses OK
        Platform.runLater(() ->
                alert.showAndWait().ifPresent(response -> {
                     if (response == ButtonType.OK) {
                         System.exit(0);
                     }
                })
        );
    }
}
