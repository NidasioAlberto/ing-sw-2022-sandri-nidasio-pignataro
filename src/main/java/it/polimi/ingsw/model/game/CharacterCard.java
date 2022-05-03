package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * This class defines the abstraction of a Character card. A Character card can be played by a
 * player in every moment of his turn paying a price (this price increases the first time the card
 * is played). The card affects the game flow, the game mechanics or both of them, so it is
 * implemented using a decorator pattern of the Game class.
 */
public abstract class CharacterCard extends Game
{
    /**
     * Card cost (increased once the card is first used)
     */
    protected int cost;

    /**
     * Boolean that indicates the first use of the card. It is used to distinguish the first usage
     * from the others.
     */
    protected boolean firstUsed;

    /**
     * The game instance to be wrapped using the decorator pattern
     */
    protected Game instance;

    protected boolean activated;

    /**
     * Constructor
     * 
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    protected CharacterCard(Game game) throws NullPointerException
    {
        if (game == null)
            throw new NullPointerException("[CharacterCard] null game instance");

        this.instance = game;
        this.firstUsed = false;
        this.activated = false;
    }

    /**
     * Method that vary based on the actual card. It accesses the Game instance to understand using
     * the current game state if it is proper to play the card.
     * 
     * @return boolean that indicates the result
     */
    public abstract boolean isPlayable();

    /**
     * This is a critical method for the entire game. Based on the actual game state it decides
     * whether the argument action should be allowed or not for the game flow. This method, combined
     * with the decorator pattern, allows the played card to modify the usual game flow applying the
     * card effect to the match.
     * 
     * @param action game action to be verified
     * @return the decision's result
     */
    public abstract boolean isValidAction(ExpertGameAction action);

    /**
     * Method to apply the card action to the Game model. IMPORTANT: This method has to be called
     * after the corresponding action is thrown. It acts with the ALREADY selected objects in the
     * player instance. IMPORTANT2: THIS METHOD IS CALLED BY REFERENCING TO THE ARRAY OF CHARACTER
     * CARDS AND NOT WITH THE GAME INSTANCE. IMPORTANT3: THIS METHOD (WITH THE CARD ACTIVE) SHOULD
     * BE CALLED AT EVERY CONTROLLER OR GAME STEP, SO THAT THE CARD ITSELF DECIDES WERTHER
     * DEACTIVATE ITSELF.
     */
    public abstract void applyAction() throws NoSuchElementException;

    /**
     * Method to activate the card effect. Activated => the methods are not pass through.
     *
     * @throws NotEnoughCoinsException if the player hasn't got enough coins to activate the
     *         selected card.
     * @throws NoSelectedPlayerException if there isn't a selected player.
     */
    public void activate() throws NotEnoughCoinsException, NoSelectedPlayerException
    {
        // When we activate the card we subtract the coins cost
        if (instance.getSelectedPlayer().isEmpty())
            throw new NoSelectedPlayerException("[CharacterCard]");

        // If already activated I don't have to activate it another time
        if (activated)
            return;

        // I subtract the coins only if the player can pay
        if (instance.getSelectedPlayer().get().getBoard().getCoins() >= cost)
        {
            instance.getSelectedPlayer().get().getBoard().removeCoins(cost);
            this.activated = true;
            // Set the card to first used
            if (!firstUsed)
            {
                // If used for the first time i set it already used and increment the cost
                this.firstUsed = true;
                this.cost++;
            }
        } else
        {
            throw new NotEnoughCoinsException();
        }
    }

    /**
     * Method to deactivate the card effect. Deactivated => the methods are pass through
     */
    public void deactivate()
    {
        this.activated = false;
    }

    /**
     * This method is used to make the cards notify the subscriber about their payload
     */
    public void notifySubscriber()
    {}

    /**
     * This method clones the current object avoiding Game instance clone
     * 
     * @return
     */
    @Override
    public CharacterCard clone()
    {
        // Create a new card
        CharacterCard cloned = createCharacterCard(this.getCardType(), instance);

        // Null the instance
        cloned.instance = null;
        // Copy the properties
        cloned.cost = this.cost;
        cloned.activated = this.activated;
        cloned.firstUsed = this.firstUsed;

        return cloned;
    }

    /**
     * Method that vary based on the actual card.
     * 
     * @return the enumeration of the card type
     */
    public abstract CharacterCardType getCardType();

    public boolean isActivated()
    {
        return activated;
    }

    public int getCost()
    {
        return cost;
    }

    public void addPlayer(Player player) throws TooManyPlayersException
    {
        instance.addPlayer(player);
    }

    public void selectPlayer(int index)
    {
        instance.selectPlayer(index);
    }

    public Optional<Player> getSelectedPlayer()
    {
        return instance.getSelectedPlayer();
    }

    public Optional<Integer> getSelectedPlayerIndex()
    {
        return instance.getSelectedPlayerIndex();
    }

    public List<Player> getSortedPlayerList()
    {
        return instance.getSortedPlayerList();
    }

    public List<Player> getPlayerTableList()
    {
        return instance.getPlayerTableList();
    }

    public Student pickStudentFromEntrance()
    {
        return instance.pickStudentFromEntrance();
    }

    public void putStudentToIsland(Student student)
    {
        instance.putStudentToIsland(student);
    }

    public void putStudentToDining(Student student)
    {
        instance.putStudentToDining(student);
    }

    public void conquerProfessors()
    {
        instance.conquerProfessors();
    }

    public void moveMotherNature(int steps)
    {
        instance.moveMotherNature(steps);
    }

    public boolean isValidMotherNatureMovement(int steps)
    {
        return instance.isValidMotherNatureMovement(steps);
    }

    public void computeInfluence()
    {
        instance.computeInfluence();
    }

    public void computeInfluence(int island)
    {
        instance.computeInfluence(island);
    }

    public int computePlayerInfluence(Player player, int island)
    {
        return instance.computePlayerInfluence(player, island);
    }

    public void moveStudentsFromCloudTile()
    {
        instance.moveStudentsFromCloudTile();
    }

    public void setupGame()
    {
        instance.setupGame();
    }

    public List<CharacterCard> getCharacterCards()
    {
        return instance.getCharacterCards();
    }

    public Optional<CharacterCard> getCurrentCharacterCard()
    {
        return instance.getCurrentCharacterCard();
    }

    public List<Island> getIslands()
    {
        return instance.getIslands();
    }

    public List<Student> getStudentBag()
    {
        return instance.getStudentBag();
    }

    public Game getInstance()
    {
        return instance;
    }

    public List<Professor> getProfessors()
    {
        return instance.getProfessors();
    }

    public List<CloudTile> getCloudTiles()
    {
        return instance.getCloudTiles();
    }

    public Island getCurrentIsland() throws NoSuchElementException
    {
        return islands.get(motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[Game] No mother nature index, is the game initialized?")));
    }

    public Optional<Integer> getMotherNatureIndex()
    {
        return instance.getMotherNatureIndex();
    }

    public int getPlayersNumber()
    {
        return instance.getPlayersNumber();
    }

    public GameMode getGameMode()
    {
        return instance.getGameMode();
    }

    public static CharacterCard createCharacterCard(CharacterCardType type, Game game)
            throws NullPointerException
    {
        // Check if the parameters are not null
        if (type == null)
            throw new NullPointerException("[CharacterCard] Null character card type");

        if (game == null)
            throw new NullPointerException("[CharacterCard] Null game instance");

        // Depending on the type of Character card i return a different instance
        switch (type)
        {
            case MONK:
                return new Monk(game);
            case SHAMAN:
                return new Shaman(game);
            case HERALD:
                return new Herald(game);
            case POSTMAN:
                return new Postman(game);
            case GRANDMA_HERBS:
                return new GrandmaHerbs(game);
            case JOKER:
                return new Joker(game);
            case KNIGHT:
                return new Knight(game);
            case MUSHROOM_MAN:
                return new MushroomMan(game);
            case MINSTREL:
                return new Minstrel(game);
            case PRINCESS:
                return new Princess(game);
            case THIEF:
                return new Thief(game);
            default:
                return new Centaur(game);
        }
    }
}
