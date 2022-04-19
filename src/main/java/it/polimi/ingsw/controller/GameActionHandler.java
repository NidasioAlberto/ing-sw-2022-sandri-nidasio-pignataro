package it.polimi.ingsw.controller;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.game.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.NoSuchElementException;

/**
 * This class represents the collection of methods and game finite state machine
 * that allow the actual controller in MVC pattern to execute methods and
 * requests by the player and verify that these requests are actually correct.
 * In this class, all possible user actions are translated and verified
 */
public class GameActionHandler
{
    /**
     * The model instance that needs to be controlled
     */
    private Game game;

    /**
     * The FSM that checks whether the action is legit.
     */
    private Phase gamePhase;

    /**
     * Constructor
     * @param game The game instance to be controlled
     * @throws NullPointerException When the parameter is null
     */
    public GameActionHandler(Game game)
    {
        if(game == null)
            throw new NullPointerException("[GameActionHandler] Null game pointer");

        this.game = game;

        //Instantiate the FSM
        gamePhase = new PlanPhase();
    }

    /**
     * This method is called by the Controller object to handle an action
     * message incoming from a player
     * @param message The command pattern message that needs to be executed
     * @throws NullPointerException When the passed message is null
     * @throws InvalidModuleException When the action is not actually valid
     */
    public void handleAction(ActionMessage message)
    {
        if(message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        if(!gamePhase.isLegitAction(message))
            throw new InvalidModuleException("[GameActionHandler] No legit action");

        if(game.getSelectedPlayer().isEmpty())
            throw new NoSuchElementException("[GameActionHandler] No selected player");

        //The action is legit, so i can set the player's parameters and call the method
        //Information parsing
        JSONObject json = new JSONObject(message.getJson());
        String playerName = json.getJSONObject("playerInfo").getString("playerName");

        //If the player is different from the selected one i reject
        if(!playerName.equals(game.getSelectedPlayer().get().getNickname()))
            throw new InvalidModuleException("[GameActionHandler] Wrong player");

        //First of i clear all the player's selections
        game.getSelectedPlayer().get().clearSelections();

        //Now i parse all the selected stuff
        game.getSelectedPlayer().get().selectIsland(json.getJSONObject("actionInfo").getInt("selectedIsland"));

        //For the selected colors i need to add a for loop
        JSONArray selectedColors = json.getJSONObject("actionInfo").getJSONArray("selectedColors");
        for(int i = 0; i < selectedColors.length(); i++)
            game.getSelectedPlayer().get().selectColor(SchoolColor.valueOf(selectedColors.getString(i)));

        game.getSelectedPlayer().get().selectCloudTile(json.getJSONObject("actionInfo").getInt("selectedCloudTile"));
        game.getSelectedPlayer().get().selectCard(json.getJSONObject("actionInfo").getInt("selectedCard"));
        game.getSelectedPlayer().get().selectCharacterCard(json.getJSONObject("actionInfo").getString("selectedCharacterCard"));

        //Call the correct method (Command pattern)
        message.applyAction(this);
    }

    public void playAssistantCard()
    {

    }

    public void moveStudentFromEntranceToIsland()
    {

    }

    public void moveStudentFromEntranceToDining()
    {

    }

    public void moveMotherNature()
    {

    }

    public void selectCloudTile()
    {

    }

    public void playCharacterCard()
    {

    }

    public void characterCardAction()
    {

    }

    public void endTurn()
    {

    }

    /**
     * Getters and setters
     */
    public Phase getGamePhase() { return gamePhase; }
    public void setGamePhase(Phase phase)
    {
        if(phase == null)
            throw new NullPointerException("[GameActionHandler] Null phase");
        this.gamePhase = phase;
    }
}
