package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.protocol.commands.CreateMatchCommand;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class CreateMatchSceneController implements Controllable
{
    @FXML
    private TextField gameNameCreateTextField;

    @FXML
    private RadioButton expertRbutton, classicRbutton, twoRbutton, threeRbutton;

    private SceneController controller;

    /**
     * Method executed when the player is in the createMatch scene and presses Create button,
     * check that the parameter are valid, if so send a CreateMatchCommand.
     */
    public void create(ActionEvent event)
    {
        // Check that the data inserted from the player are valid
        if (gameNameCreateTextField.getText().isBlank())
        {
            controller.displayError("You must insert a valid game name");
        }
        else if (!classicRbutton.isSelected() && !expertRbutton.isSelected())
        {
            controller.displayError("You must choose a game mode");
        }
        else if (!twoRbutton.isSelected() && !threeRbutton.isSelected())
        {
            controller.displayError("You must choose a number of players");
        }
        else
        {
            // If all goes well send a create match command
            String gameName = gameNameCreateTextField.getText();
            GameMode mode = classicRbutton.isSelected() ? GameMode.CLASSIC : GameMode.EXPERT;
            int players = twoRbutton.isSelected() ? 2 : 3;
            controller.sendCommand(new CreateMatchCommand(gameName, players, mode));
        }
    }

    /**
     * Method executed when the player press the Back button, so the scene goes back to lobby.
     */
    public void back(ActionEvent event)
    {
        controller.setRoot("/Lobby/lobby.fxml");
    }

    @Override
    public void initialize(SceneController controller)
    {
        this.controller = controller;
    }
}
