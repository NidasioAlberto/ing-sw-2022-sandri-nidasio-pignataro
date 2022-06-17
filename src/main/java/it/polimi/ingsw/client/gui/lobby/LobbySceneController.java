package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;
import it.polimi.ingsw.protocol.answers.SetNameAnswer;
import it.polimi.ingsw.protocol.commands.GetMatchesListCommand;
import it.polimi.ingsw.protocol.commands.JoinMatchCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbySceneController implements Controllable, Initializable
{
    @FXML
    private Label nicknameLabel;
    @FXML
    private TableView<MatchLine> matchesTableView;
    @FXML
    private TableColumn<MatchLine, String> nameColumn;
    @FXML
    private TableColumn<MatchLine, GameMode> modeColumn;
    @FXML
    private TableColumn<MatchLine, String> playersColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        matchesTableView.setPlaceholder(new Label("There aren't matches at the moment"));
        SceneController.sendCommand(new GetMatchesListCommand());
    }

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
        }
        else matchesTableView.setPlaceholder(new Label("There aren't matches at the moment"));
    }

    /**
     * Set the nickname of the player in order to visualize it.
     *
     * @param answer contains the player's nickname.
     */
    public void displayName(SetNameAnswer answer)
    {
        nicknameLabel.setText("Your nickname is " + answer.getName());
    }

    /**
     * When the player does a double click on a row can join the selected match.
     */
    public void joinSelectedMatch(MouseEvent event)
    {
        // Check the event was a double click
        if (event.getClickCount() == 2)
        {
            // Send a joinMatchCommand with the name of the selected match
            String gameName = matchesTableView.getSelectionModel().getSelectedItem().getName();
            SceneController.sendCommand(new JoinMatchCommand(gameName));
        }
    }
}
