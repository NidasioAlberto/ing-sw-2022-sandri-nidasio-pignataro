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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;


public class JoinMatchSceneController implements Controllable
{
    @FXML
    TableView<MatchLine> matchesTableView;
    @FXML
    TableColumn<MatchLine, String> nameColumn;
    @FXML
    TableColumn<MatchLine, GameMode> modeColumn;
    @FXML
    TableColumn<MatchLine, String> playersColumn;

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
        }
        else matchesTableView.setPlaceholder(new Label("There aren't matches at the moment"));
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
