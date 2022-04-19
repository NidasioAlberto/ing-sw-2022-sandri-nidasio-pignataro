package it.polimi.ingsw.controller;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

/**
 * This class represents the collection of methods and game finite state machine that allow the
 * actual controller in MVC pattern to execute methods and requests by the player and verify that
 * these requests are actually correct. In this class, all possible user actions are translated and
 * verified
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
     * 
     * @param game The game instance to be controlled
     * @throws NullPointerException When the parameter is null
     */
    public GameActionHandler(Game game)
    {
        if (game == null)
            throw new NullPointerException("[GameActionHandler] Null game pointer");

        this.game = game;

        // Instantiate the FSM
        gamePhase = new PlanPhase();
    }

    /**
     * This method is called by the Controller object to handle an action message incoming from a
     * player
     * 
     * @param message The command pattern message that needs to be executed
     * @throws NullPointerException When the passed message is null
     * @throws InvalidModuleException When the action is not actually valid
     */
    public void handleAction(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        if (!gamePhase.isLegitAction(message))
            throw new InvalidModuleException("[GameActionHandler] No legit action");

        if (game.getSelectedPlayer().isEmpty())
            throw new NoSuchElementException("[GameActionHandler] No selected player");

        // The action is legit, so i can set the player's parameters and call the method
        // Information parsing
        JSONObject json = new JSONObject(message.getJson());
        String playerName = json.getJSONObject("playerInfo").getString("playerName");

        // If the player is different from the selected one i reject
        if (!playerName.equals(game.getSelectedPlayer().get().getNickname()))
            throw new InvalidModuleException("[GameActionHandler] Wrong player");

        // First of i clear all the player's selections
        game.getSelectedPlayer().get().clearSelections();

        // Now i parse all the selected stuff
        game.getSelectedPlayer().get()
                .selectIsland(json.getJSONObject("actionInfo").getInt("selectedIsland"));

        // For the selected colors i need to add a for loop
        JSONArray selectedColors = json.getJSONObject("actionInfo").getJSONArray("selectedColors");
        for (int i = 0; i < selectedColors.length(); i++)
            game.getSelectedPlayer().get()
                    .selectColor(SchoolColor.valueOf(selectedColors.getString(i)));

        game.getSelectedPlayer().get()
                .selectCloudTile(json.getJSONObject("actionInfo").getInt("selectedCloudTile"));
        game.getSelectedPlayer().get().selectCharacterCard(
                json.getJSONObject("actionInfo").getInt("selectedCharacterCard"));

        // Call the correct method (Command pattern)
        message.applyAction(this);
    }

    public void playAssistantCard(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Parse the card
        JSONObject json = new JSONObject(message.getJson());
        game.getSelectedPlayer().get()
                .selectCard(json.getJSONObject("actionInfo").getInt("selectedCard"));

        //At the end i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToIsland(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Move the student to the selected island
        game.putStudentToIsland(game.getSelectedPlayer().get()
                .getBoard()
                .removeStudentFromEntrance(game
                        .getSelectedPlayer()
                        .get().getSelectedColors().get(0)).get());

        //If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToDining(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Move the student to dining
        game.getSelectedPlayer().get()
                .getBoard()
                .addStudentToDiningRoom(game
                        .getSelectedPlayer()
                        .get().getBoard()
                        .removeStudentFromEntrance(game
                                .getSelectedPlayer()
                                .get().getSelectedColors().get(0)).get());

        //If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveMotherNature(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Calculate the difference from the indexed island and the current one
        int pos = game.getMotherNatureIndex()
                .orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No mother nature position, is the game setup?"));

        int wantedPos = game.getSelectedPlayer().get()
                .getSelectedIsland().orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No selected island"));

        //Based on the actual difference i move mother nature of the calculated steps
        if(wantedPos > pos)
            game.moveMotherNature(wantedPos - pos);
        else if(wantedPos < pos)
            game.moveMotherNature(Game.ISLAND_TILES_NUMBER + wantedPos - pos);
        else
            throw new InvalidParameterException("[GameActionHandler] Mother nature cannot stay in the same position");

        //If all goes correctly i compute the influence
        game.computeInfluence();

        //Step the FSM
        gamePhase.onValidAction(this);
    }

    public void selectCloudTile(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //I use the designed method
        game.moveStudentsFromCloudTile();

        //If all goes correctly i step the FSM
        gamePhase.onValidAction(this);
    }

    public void playCharacterCard(ActionMessage message) throws NotEnoughCoinsException
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        int index = game.getSelectedPlayer().get().getSelectedCharacterCard()
                .orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No Character Card selected"));

        //I select the character card if the card is playable and no card has already been played
        if(game.getCharacterCards().get(index).isPlayable() && game.getCurrentCharacterCard().isEmpty())
        {
            game.setCurrentCharacterCard(index);
            game.getCharacterCards().get(index).activate();
        }
        //IMPORTANT: I DON'T STEP THE FSM BECAUSE THIS IS A CHARACTER CARD PLAY
    }

    public void characterCardAction(ActionMessage message, ExpertGameAction action)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");
        if (action == null)
            throw new NullPointerException("[GameActionHandler] Null action enum");

        //Get the current card
        CharacterCard currentCard = game.getCurrentCharacterCard()
                .orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No active character card"));

        //If the action is valid i execute the action
        if(currentCard.isValidAction(action) && currentCard.isActivated())
            currentCard.applyAction();
    }

    public void endTurn(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");
    }

    /**
     * Getters and setters
     */
    public Phase getGamePhase()
    {
        return gamePhase;
    }

    public void setGamePhase(Phase phase)
    {
        if (phase == null)
            throw new NullPointerException("[GameActionHandler] Null phase");
        this.gamePhase = phase;
    }

    public Game getGame()
    {
        return game;
    }
}
