package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.protocol.answers.SetNameAnswer;
import it.polimi.ingsw.protocol.commands.GetMatchesListCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class LobbySceneController implements Controllable
{
    @FXML
   private Label nicknameLabel;

    /**
     * Method executed when the player is in the lobby scene and presses create match button,
     * the scene moves to createMatch.
     */
    public void createMatch(ActionEvent event)
    {
        // Move to createMatch scene
        SceneController.setRoot("/Lobby/createMatch.fxml");
    }

    /**
     * Method executed when the player is in the lobby scene and presses join match button,
     * the scene moves to joinMatch.
     */
    public void joinMatch(ActionEvent event)
    {
        // Move to joinMatch scene
        SceneController.setRoot("/Lobby/joinMatch.fxml");
    }

    /**
     * Method executed when the player is in the lobby scene and presses matches' list button,
     * a GetMatchesList command is sent to the server and the scene moves to matchesList.
     */
    public void matchesList(ActionEvent event)
    {
        // Move to matchesList scene
        SceneController.setRoot("/Lobby/matchesList.fxml");
        SceneController.sendCommand(new GetMatchesListCommand());
    }

    public void displayName(SetNameAnswer answer)
    {
        Platform.runLater(() -> nicknameLabel.setText("Your nickname is " + answer.getName()));
    }
}
