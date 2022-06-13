package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.protocol.commands.SetNameCommand;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginSceneController implements Controllable
{
    @FXML
    private TextField nicknameTextField;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;

    /**
     * Pressing enter in portTextField is like clicking the button submit.
     */
    public void checkEnter(KeyEvent event)
    {
        if (event.getCode().equals(KeyCode.ENTER))
            submit(new ActionEvent());
    }

    /**
     * Method executed when the player is in the entrance scene and presses Submit button,
     * if has inserted name, ip and port the client tries to connect to the server and send
     * a SetNameCommand.
     */
    public void submit(ActionEvent event)
    {
        // Check that the data inserted from the player are valid
        if (nicknameTextField.getText().isBlank())
        {
            System.out.println("You must insert a valid nickname");
        }
        else if (ipTextField.getText().isBlank())
        {
            System.out.println("You must insert a valid IP");
        }
        else if (portTextField.getText().isBlank())
        {
            System.out.println("You must insert a valid port");
        }
        else {
            try {
                // Set the ip and port of the client and try to connect
                String ip = ipTextField.getText();
                int port = Integer.parseInt(portTextField.getText());
                SceneController.clientConnect(ip, port);

                // If all goes well set the name
                SceneController.sendCommand(new SetNameCommand(nicknameTextField.getText()));
            } catch (NumberFormatException e) {
                // If port is not a number
                System.out.println("You must insert a valid port");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
