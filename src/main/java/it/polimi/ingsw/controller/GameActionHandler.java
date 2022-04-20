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
import java.util.ArrayList;
import java.util.List;
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

        // I first parse all the stuff in temporary variables so that if an error occurs
        // I don't risk deleting stuff from player selection
        int selectedIsland = json.getJSONObject("actionInfo").getInt("selectedIsland");
        int selectedCloudTile = json.getJSONObject("actionInfo").getInt("selectedCloudTile");
        int selectedCharacterCard = json.getJSONObject("actionInfo").getInt("selectedCharacterCard");
        List<SchoolColor> selectedColors = new ArrayList<SchoolColor>();

        // For the selected colors i need to add a for loop
        JSONArray colors = json.getJSONObject("actionInfo").getJSONArray("selectedColors");
        for (int i = 0; i < colors.length(); i++)
            selectedColors.add(SchoolColor.valueOf(colors.getString(i)));

        // First of i clear all the player's selections
        game.getSelectedPlayer().get().clearSelections();
        // Now i assign all the selected stuff
        game.getSelectedPlayer().get().selectIsland(selectedIsland);
        game.getSelectedPlayer().get().selectCloudTile(selectedCloudTile);
        game.getSelectedPlayer().get().selectCharacterCard(selectedCharacterCard);
        for(SchoolColor color : selectedColors)
            game.getSelectedPlayer().get().selectColor(color);

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

        //Check if a character card is active and in case if the action is valid
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated() &&
                !game.getCurrentCharacterCard().get().isValidAction(ExpertGameAction.BASE_ACTION))
            throw new InvalidModuleException("[GameActionHandler] No legit action");

        //Move the student to the selected island
        game.putStudentToIsland(game.getSelectedPlayer().get()
                .getBoard()
                .removeStudentFromEntrance(game
                        .getSelectedPlayer()
                        .get().getSelectedColors().get(0)).get());

        //If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        //If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToDining(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Check if a character card is active and in case if the action is valid
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated() &&
                !game.getCurrentCharacterCard().get().isValidAction(ExpertGameAction.BASE_ACTION))
            throw new InvalidModuleException("[GameActionHandler] No legit action");

        //Move the student to dining
        game.getSelectedPlayer().get()
                .getBoard()
                .addStudentToDiningRoom(game
                        .getSelectedPlayer()
                        .get().getBoard()
                        .removeStudentFromEntrance(game
                                .getSelectedPlayer()
                                .get().getSelectedColors().get(0)).get());

        //If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        //If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveMotherNature(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Check if a character card is active and in case if the action is valid
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated() &&
                !game.getCurrentCharacterCard().get().isValidAction(ExpertGameAction.BASE_ACTION))
            throw new InvalidModuleException("[GameActionHandler] No legit action");

        //Calculate the difference from the indexed island and the current one
        int pos = game.getMotherNatureIndex()
                .orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No mother nature position, is the game setup?"));

        int wantedPos = game.getSelectedPlayer().get()
                .getSelectedIsland().orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No selected island"));

        //Based on the actual difference i move mother nature of the calculated steps
        if(wantedPos > pos && game.isValidMotherNatureMovement(wantedPos - pos))
            game.moveMotherNature(wantedPos - pos);
        else if(wantedPos < pos && game.isValidMotherNatureMovement(Game.ISLAND_TILES_NUMBER + wantedPos - pos))
            game.moveMotherNature(Game.ISLAND_TILES_NUMBER + wantedPos - pos);
        else
            throw new InvalidParameterException("[GameActionHandler] Mother nature cannot stay in the same position");

        //If all goes correctly i compute the influence
        game.computeInfluence();

        //If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        //Step the FSM
        gamePhase.onValidAction(this);
    }

    public void selectCloudTile(ActionMessage message)
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        //Check if a character card is active and in case if the action is valid
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated() &&
                !game.getCurrentCharacterCard().get().isValidAction(ExpertGameAction.BASE_ACTION))
            throw new InvalidModuleException("[GameActionHandler] No legit action");

        //I use the designed method
        game.moveStudentsFromCloudTile();

        //If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent() &&
                game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        //If all goes correctly i step the FSM
        gamePhase.onValidAction(this);
    }

    public void playCharacterCard(ActionMessage message) throws NotEnoughCoinsException
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");
        
        if (game.getCurrentCharacterCard().isPresent())
            throw new InvalidModuleException("[GameActionHandler] A character card was already played");

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

        //Get the current card
        CharacterCard currentCard = game.getCurrentCharacterCard().filter(c -> c.isActivated())
                .orElseThrow(() -> new NoSuchElementException("[GameActionHandler] No active character card"));

        //If the action is valid i execute the action
        if(currentCard.isValidAction(action))
            currentCard.applyAction();
        else
            throw new InvalidModuleException("[GameActionHandler] No legit action");
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
