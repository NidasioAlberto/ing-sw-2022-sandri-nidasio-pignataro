package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;
import it.polimi.ingsw.protocol.commands.JoinMatchCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class JoinMatchSceneController implements Controllable
{
    @FXML
    private TextField gameNameJoinTextField;
    @FXML
    TableView<MatchLine> matchesTableView;
    @FXML
    TableColumn<MatchLine, String> nameColumn;
    @FXML
    TableColumn<MatchLine, GameMode> modeColumn;
    @FXML
    TableColumn<MatchLine, String> playersColumn;

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

    /**
     * Shows the list of the available matches.
     * @param answer contains the list of the matches.
     */
    public void displayMatchesList(MatchesListAnswer answer)
    {
        nameColumn.setCellValueFactory(new PropertyValueFactory<MatchLine, String>("name"));
        modeColumn.setCellValueFactory(new PropertyValueFactory<MatchLine, GameMode>("mode"));
        playersColumn.setCellValueFactory(new PropertyValueFactory<MatchLine, String>("playersNumber"));

        ObservableList<MatchLine> list = FXCollections.observableArrayList();

        if (!answer.getNumPlayers().isEmpty())
        {
            for (String matchName : answer.getNumPlayers().keySet())
                list.add(new MatchLine(matchName, answer.getGameModes().get(matchName),
                        answer.getNumPlayers().get(matchName) + "/" + answer.getMaxNumPlayers().get(matchName)));

            matchesTableView.setItems(list);
            //Platform.runLater(() -> matchesTableView.setItems(list));
        }
        else matchesTableView.setPlaceholder(new Label("There aren't matches at the moment"));
    }
}
