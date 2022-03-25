package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;

import java.util.List;
import java.util.Optional;

/**
 * This class defines the abstraction of a Character card. A Character card can be played by a player
 * in every moment of his turn paying a price (this price increases the first time the card is played).
 * The card affects the game flow, the game mechanics or both of them, so it is implemented using
 * a decorator patter of the Game class.
 */
public abstract class CharacterCard extends Game
{
    /**
     * Card cost (increased once the card is first used)
     */
    protected int cost;

    /**
     * Boolean that indicates the first use of the card. It is used to distinguish
     * the first usage from the others.
     */
    protected boolean firstUsed;

    /**
     * The game instance to be wrapped using the decorator pattern
     */
    protected Game instance;

    /**
     * Constructor
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public CharacterCard(Game game) throws NullPointerException
    {
        if(game == null)
            throw new NullPointerException("[CharacterCard] null game instance");

        this.instance   = game;
        this.firstUsed  = false;
    }

    /**
     * Method that vary based on the actual card. It accesses the Game instance
     * to understand using the current game state if it is proper to play the card.
     * @return boolean that indicates the result
     */
    public abstract boolean isPlayable();

    /**
     * This is a critical method for the entire game. Based on the actual
     * game state it decides whether the argument action should be
     * allowed or not for the game flow. This method, combined with the decorator
     * pattern, allows the played card to modify the usual game flow applying the
     * card effect to the match.
     * @param action game action to be verified
     * @return the decision's result
     */
    public abstract boolean isValidAction(GameAction action);

    /**
     * Method to apply the card action to the Game model
     */
    public abstract Game applyAction();

    /**
     * Method that vary based on the actual card.
     * @return the enumeration of the card type
     */
    public abstract CharacterCardType getCardType();

    /**
     * Override of all the Game methods
     */
    public void addPlayer(Player player) throws TooManyPlayersException { instance.addPlayer(player); }
    public void selectPlayer(int index) { instance.selectPlayer(index); }
    public Optional<Player> getSelectedPlayer() { return instance.getSelectedPlayer(); }
    public void setupTiles() { instance.setupTiles(); }
    public List<Player> getSortedPlayerList() { return instance.getSortedPlayerList(); }
    public List<Player> getPlayerTableList() { return instance.getPlayerTableList(); }
    public void moveStudentToIsland(SchoolColor color, int islandIndex) { instance.moveStudentToIsland(color, islandIndex); }
    public void moveStudentToDining(SchoolColor color) { instance.moveStudentToDining(color); }
    public void moveMotherNature(int steps) { instance.moveMotherNature(steps); }
    //TODO TALK ABOUT WHAT THIS METHOD CAN AND SHOULD DO
    public void conquer() { instance.conquer(); }
    public void moveStudentsFromCloudTile(int tileIndex) { instance.moveStudentsFromCloudTile(tileIndex); }
    //TODO TALK ABOUT WHAT THIS METHOD SHOULD DO
    public void setupGame() { instance.setupGame(); }
    //TODO WHAT DOES THIS METHOD DO?
    //public int getStudentsToMove() { return instance.getStudentsToMove(); }

    public GameAction getGameAction() { return instance.getGameAction(); }
    public List<CharacterCard> getCharacterCards() { return instance.getCharacterCards(); }
}
