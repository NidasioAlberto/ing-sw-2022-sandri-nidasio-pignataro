package it.polimi.ingsw.controller;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

        // Before instantiating the FSM i need to select the first player
        game.selectPlayer(0);

        // Instantiate the FSM (I want the initial phase to be in table order)
        gamePhase = new PlanPhase(game.getPlayerTableList());
    }

    /**
     * This method is called by the Controller object to handle an action message incoming from a
     * player.
     * 
     * @param message The command pattern message that needs to be executed
     * @throws NullPointerException When the passed message is null
     * @throws InvalidModuleException When the action is not actually valid
     */
    public void handleAction(ActionMessage message, String playerName)
            throws NullPointerException, NoSuchElementException, InvalidModuleException
    {
        if (message == null)
            throw new NullPointerException("[GameActionHandler] Null action message");

        if (game.getSelectedPlayer().isEmpty())
            throw new NoSelectedPlayerException("[GameActionHandler]");

        if (!gamePhase.isLegitAction(this, playerName, message.getBaseGameAction()))
            throw new NoLegitActionException();

        // Call the correct method (Command pattern)
        message.applyAction(this);
    }

    public void playAssistantCard(int selectedCard) throws NoSuchAssistantCardException
    {
        // The player selection at assistant card stage is about table order
        game.getSelectedPlayer().get().selectCard(selectedCard);

        // At the end i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToIsland(SchoolColor selectedColor, int selectedIsland)
            throws InvalidModuleException
    {
        checkIfCharacterCardIsStillPlayable();

        // Select the color
        game.getSelectedPlayer().get().selectColor(selectedColor);

        // Select the island
        if (selectedIsland < 0 || selectedIsland >= game.getIslands().size())
            throw new IslandIndexOutOfBoundsException("[GameActionHandler]");
        game.getSelectedPlayer().get().selectIsland(selectedIsland);

        // Move the student to the selected island
        game.putStudentToIsland(game.getSelectedPlayer().get().getBoard().removeStudentFromEntrance(
                game.getSelectedPlayer().get().getSelectedColors().get(0)).get());

        // If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent()
                && game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();

        // If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToDining(SchoolColor selectedColor)
            throws InvalidModuleException
    {
        checkIfCharacterCardIsStillPlayable();

        // Select the color
        game.getSelectedPlayer().get().selectColor(selectedColor);

        // Move the student to dining
        game.getSelectedPlayer().get().getBoard()
                .addStudentToDiningRoom(
                        game.getSelectedPlayer().get().getBoard()
                                .removeStudentFromEntrance(
                                        game.getSelectedPlayer().get().getSelectedColors().get(0))
                                .get());

        // If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent()
                && game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();

        // If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveMotherNature(int selectedIsland)
            throws InvalidModuleException, NoSuchElementException, InvalidParameterException
    {
        checkIfCharacterCardIsStillPlayable();

        // Select the island
        game.getSelectedPlayer().get().selectIsland(selectedIsland);

        int currentPosition =
                game.getMotherNatureIndex().orElseThrow(() -> new NoSuchElementException(
                        "[GameActionHandler] No mother nature position, is the game setup?"));

        int wantedPosition = game.getSelectedPlayer().get().getSelectedIsland()
                .orElseThrow(() -> new NoSelectedIslandException("[GameActionHandler]"));

        // Calculate the difference from the indexed island and the current one
        // Based on the actual difference i move mother nature of the calculated steps
        if (wantedPosition > currentPosition
                && game.isValidMotherNatureMovement(wantedPosition - currentPosition))
            game.moveMotherNature(wantedPosition - currentPosition);
        else if (wantedPosition < currentPosition && game.isValidMotherNatureMovement(
                Game.ISLAND_TILES_NUMBER + wantedPosition - currentPosition))
            game.moveMotherNature(Game.ISLAND_TILES_NUMBER + wantedPosition - currentPosition);
        else
            throw new InvalidMovementException(
                    "[GameActionHandler] Mother nature cannot stay in the same position");

        // If all goes correctly i compute the influence
        game.computeInfluence();

        // If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent()
                && game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();

        // Step the FSM
        gamePhase.onValidAction(this);
    }

    public void selectCloudTile(int selectedCloudTile) throws InvalidModuleException
    {
        checkIfCharacterCardIsStillPlayable();

        // Select the cloud tile
        game.getSelectedPlayer().get().selectCloudTile(selectedCloudTile);

        // I use the designed method
        game.moveStudentsFromCloudTile();

        // If the current card is activated i can apply the action
        if (game.getCurrentCharacterCard().isPresent()
                && game.getCurrentCharacterCard().get().isActivated())
            game.getCurrentCharacterCard().get().applyAction();

        // If all goes correctly i step the FSM
        gamePhase.onValidAction(this);
    }

    public void playCharacterCard(int selectedCharacterCard)
            throws InvalidModuleException, NoSuchElementException, NotEnoughCoinsException
    {
        if (game.getCurrentCharacterCard().isPresent())
            throw new InvalidCharacterCardException(
                    "[GameActionHandler] A character card was already played");

        // Select the card
        game.getSelectedPlayer().get().selectCharacterCard(selectedCharacterCard);

        // I select the character card if the card is playable and no card has already been played
        if (game.getCharacterCards().get(selectedCharacterCard).isPlayable()
                && game.getCurrentCharacterCard().isEmpty())
        {
            game.setCurrentCharacterCard(selectedCharacterCard);
            game.getCharacterCards().get(selectedCharacterCard).activate();
        }
        // IMPORTANT: I DON'T STEP THE FSM BECAUSE THIS IS A CHARACTER CARD'S PLAY
    }

    public void characterCardAction(ExpertGameAction action, Optional<Integer> selectedIsland,
            Optional<List<SchoolColor>> selectedColors)
            throws NullPointerException, NoSuchElementException
    {
        if (action == null)
            throw new NullPointerException("[GameActionHandler] Null expert game action");

        // Get the current card if activated
        CharacterCard currentCard = game.getCurrentCharacterCard().filter(c -> c.isActivated())
                .orElseThrow(() -> new NoSelectedCharacterCardException("[GameActionHandler]"));

        // Select the island
        selectedIsland.ifPresent((island) -> game.getSelectedPlayer().get().selectIsland(island));

        // Select the colors
        selectedColors.ifPresent((colors) -> colors.stream()
                .forEach((color) -> game.getSelectedPlayer().get().selectColor(color)));

        // If the action is valid i execute the action
        if (currentCard.isValidAction(action))
            currentCard.applyAction();
        else
            throw new NoLegitActionException();

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();
    }

    public void endTurn() throws InvalidModuleException
    {
        checkIfCharacterCardIsStillPlayable();

        // Clear the selections and disable any character card
        game.getSelectedPlayer().get().clearSelectionsEndTurn();
        game.clearTurn();
        for (CharacterCard card : game.getCharacterCards())
            card.deactivate();

        // If all the players have done their turn, I fill up the clouds
        if (game.getSelectedPlayerIndex().get() == game.getPlayerTableList().size() - 1)
            game.fillClouds();

        // If all goes correctly i step the FSM
         gamePhase.onValidAction(this);
    }

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

    private void checkIfCharacterCardIsStillPlayable() throws InvalidModuleException
    {
        // If there is still an active character card which is has not been played the turn can't
        // end
        if (game.getCurrentCharacterCard().isPresent()
                && game.getCurrentCharacterCard().get().isActivated()
                && !game.getCurrentCharacterCard().get()
                        .isValidAction(ExpertGameAction.BASE_ACTION))
            throw new InvalidModuleException(
                    "[GameActionHandler] The turn can't end, a character card is still active and not played yet");
    }
}
