package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.protocol.commands.JoinMatchCommand;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class JoinMatchSceneController implements Controllable
{
    @FXML
    private TextField gameNameJoinTextField;

    /**
     * Pressing enter in gameNameJoinTextField is like clicking the button join.
     */
    public void checkEnter(KeyEvent event)
    {
        if (event.getCode().equals(KeyCode.ENTER))
            join(new ActionEvent());
    }

    /**
     * Method executed when the player is in the joinMatch scene and presses Join button,
     * check that has inserted a game name, if so send a JoinMatchCommand.
     */
    public void join(ActionEvent event)
    {
        // Check that the data inserted from the player are valid
        if (gameNameJoinTextField.getText().isBlank())
        {
            System.out.println("You must insert a valid game name");
        }
        else
        {
            // If all goes well send a join match command
            String gameName = gameNameJoinTextField.getText();
            SceneController.sendCommand(new JoinMatchCommand(gameName));
        }
    }

    /**
     * Method executed when the player press the Back button, so the scene goes back to lobby.
     */
    public void back(ActionEvent event)
    {
        SceneController.setRoot("/Lobby/lobby.fxml");
    }
}
