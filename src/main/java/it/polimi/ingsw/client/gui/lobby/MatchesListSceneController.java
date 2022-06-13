package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchesListSceneController implements Controllable, Initializable
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

        for (String matchName : answer.getNumPlayers().keySet())
            list.add(new MatchLine(matchName, answer.getGameModes().get(matchName),
                    answer.getNumPlayers().get(matchName) + "/" + answer.getMaxNumPlayers().get(matchName)));

        Platform.runLater(() -> matchesTableView.setItems(list));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO usare per settare la tabella quando vuota?
    }
}


