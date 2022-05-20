package it.polimi.ingsw.controller;

import com.sun.jdi.InvalidModuleException;
import it.polimi.ingsw.controller.fsm.Phase;
import it.polimi.ingsw.controller.fsm.PlanPhase;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.game.CharacterCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import java.security.InvalidParameterException;
import java.util.ArrayList;
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
        gamePhase = new PlanPhase();
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

        if(game.getGameMode() == GameMode.EXPERT)
        {
            // Before calling the action, if the current card is activated i substitute
            // the game instance with it, else i take the instance of one of the character cards
            if (game.getCurrentCharacterCard().isPresent() &&
                    game.getCurrentCharacterCard().get().isActivated())
                game = game.getCurrentCharacterCard().get();
            else
                game = game.getCharacterCards().get(0).getInstance();
        }

        // Call the correct method (Command pattern)
        message.applyAction(this);
    }

    public void playAssistantCard(int selectedCard) throws NoSuchAssistantCardException
    {
        // Check that the current player doesn't play a card that another player
        // has already played in this turn, unless obligated

        // Get the current player
        Player currentPlayer = game.getPlayerTableList().get(game.getSelectedPlayerIndex().get());

        // Get the cards that the current player can still play
        List<AssistantCard> usableCards = new ArrayList<AssistantCard>(currentPlayer.getCards()).
                stream().filter((card) -> !card.isUsed()).toList();

        // Flag to check if the current player has played a card already played by another player
        boolean sameCard = false;

        // For each player that has already played a card during this round
        for (int i = 0; i < ((PlanPhase) gamePhase).getCount(); i++)
        {
            // Get a previous player
            Player previousPlayer = game.getPlayerTableList().get(
                    game.getSelectedPlayerIndex().get() - i - 1 < 0 ?
                    game.getPlayersNumber() + game.getSelectedPlayerIndex().get() - i - 1 :
                    game.getSelectedPlayerIndex().get() - i - 1);

            // Remove from usableCards the card with the same turnOrder as the one
            // played by the previous player
            usableCards = usableCards.stream().filter((card) ->
                    card.getTurnOrder() != previousPlayer.getSelectedCard().get().getTurnOrder()).toList();

            // Check if the current player has played a card with the same turnOrder
            // as the card played by the previous player
            if (selectedCard == previousPlayer.getSelectedCard().get().getTurnOrder())
                sameCard = true;
        }

        // If the current player has played a card already played in this turn and
        // has at least one usable card, an exception is thrown
        if (sameCard && usableCards.size() > 0)
            throw new InvalidAssistantCardException("[GameActionHandler]");

        // The player selection at assistant card stage is about table order
        game.getPlayerTableList().get(game.getSelectedPlayerIndex().get()).selectCard(selectedCard);

        // At the end i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToIsland(SchoolColor selectedColor, int selectedIsland)
            throws InvalidModuleException
    {
        // If the current card is activated i can apply the action
        checkIfCharacterCardIsStillApplicable();

        // Select the color
        game.getSelectedPlayer().get().selectColor(selectedColor);

        // Select the island
        if (selectedIsland < 0 || selectedIsland >= game.getIslands().size())
            throw new IslandIndexOutOfBoundsException("[GameActionHandler]");
        game.getSelectedPlayer().get().selectIsland(selectedIsland);

        // Move the student to the selected island
        game.putStudentToIsland(game.pickStudentFromEntrance());

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();

        // If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveStudentFromEntranceToDining(SchoolColor selectedColor)
            throws InvalidModuleException
    {
        // If the current card is activated i can apply the action
        checkIfCharacterCardIsStillApplicable();

        // Select the color
        game.getSelectedPlayer().get().selectColor(selectedColor);

        // Move the student to dining
        game.putStudentToDining(game.pickStudentFromEntrance());

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();

        // If the action goes well i trigger the FSM
        gamePhase.onValidAction(this);
    }

    public void moveMotherNature(int selectedIsland)
            throws InvalidModuleException, NoSuchElementException, InvalidParameterException
    {
        // If the current card is activated i can apply the action
        checkIfCharacterCardIsStillApplicable();

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
                game.getIslands().size() + wantedPosition - currentPosition))
            game.moveMotherNature(Game.ISLAND_TILES_NUMBER + wantedPosition - currentPosition);
        else
            throw new InvalidMovementException(
                    "[GameActionHandler] Mother nature cannot move there");

        // If all goes correctly i compute the influence
        game.computeInfluence();

        // Clear the selections
        game.getSelectedPlayer().get().clearSelections();

        // Step the FSM
        gamePhase.onValidAction(this);
    }

    public void selectCloudTile(int selectedCloudTile) throws InvalidModuleException
    {
        // If the current card is activated i can apply the action
        checkIfCharacterCardIsStillApplicable();

        // Check if the index of the selectedCloudTile is valid
        if (selectedCloudTile < 0 || selectedCloudTile >= game.getCloudTiles().size() ||
            game.getCloudTiles().get(selectedCloudTile).getStudents().size() !=
            game.getCloudTiles().get(selectedCloudTile).getType().getStudentCapacity())
            throw new InvalidCloudTileException("[GameActionHandler]");

        // Select the cloud tile
        game.getSelectedPlayer().get().selectCloudTile(selectedCloudTile);

        // I use the designed method
        game.moveStudentsFromCloudTile();

        // If all goes correctly i step the FSM
        gamePhase.onValidAction(this);
    }

    public void playCharacterCard(int selectedCharacterCard)
            throws InvalidModuleException, NoSuchElementException, NotEnoughCoinsException
    {
        if(game.getGameMode() != GameMode.EXPERT)
            throw new NoLegitActionException();

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
        if(game.getGameMode() != GameMode.EXPERT)
            throw new NoLegitActionException();

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
        // If the current card is activated i can apply the action
        checkIfCharacterCardIsStillApplicable();

        // Clear the selections and disable any character card
        game.getSelectedPlayer().get().clearSelectionsEndTurn();
        game.clearTurn();
        for (CharacterCard card : game.getCharacterCards())
            card.deactivate();

        // Save the previous index of the last player
        int previousIndex = game.getSelectedPlayerIndex().get();

        // If all goes correctly i step the FSM
        gamePhase.onValidAction(this);

        // If all the players have done their turn, I fill up the clouds
        if (previousIndex == game.getPlayerTableList().size() - 1)
        {
            try {game.fillClouds();}
            catch (Exception e){}
        }
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

    private void checkIfCharacterCardIsStillApplicable()
    {
        // If there is still an active character card I have to apply its action
        if (game.getCurrentCharacterCard().isPresent()
                && game.getCurrentCharacterCard().get().isActivated()
                && game.getCurrentCharacterCard().get()
                        .isValidAction(ExpertGameAction.BASE_ACTION))
            game.getCurrentCharacterCard().filter(c -> c.isActivated()).get().applyAction();
    }
}
