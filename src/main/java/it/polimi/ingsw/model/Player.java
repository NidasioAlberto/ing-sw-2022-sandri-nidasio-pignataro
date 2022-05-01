package it.polimi.ingsw.model;

import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.NoSuchAssistantCardException;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * This class represent one of the game's player.
 */
public class Player
{
    /**
     * The nickname of the player.
     */
    private String nickname;

    /**
     * The school board of the player.
     */
    private SchoolBoard board;

    /**
     * The list of assistant card that the player can still play.
     */
    private List<AssistantCard> cards;

    /**
     * The player's color is associated with the color of the towers in his school board.
     */
    private TowerColor color;

    /**
     * The number of coins that player has in order to play character cards.
     */
    private int coins;

    /**
     * The assistant card selected by the player during the planning phase.
     */
    private Optional<AssistantCard> selectedCard;

    /**
     * The island selected by the player during an action.
     */
    private Optional<Integer> selectedIsland;

    /**
     * The student colors selected by the player during an action.
     */
    private List<SchoolColor> selectedColors;

    /**
     * The cloud tile selected by the player at the end of the action phase.
     */
    private Optional<Integer> selectedCloudTile;

    /**
     * The character card selected by the player.
     */
    private Optional<Integer> selectedCharacterCard;

    public Player(String nickname, TowerColor color)
    {
        this(nickname, new SchoolBoard(color));
    }

    public Player(String nickname, SchoolBoard board) throws NullPointerException
    {
        if (nickname == null)
            throw new NullPointerException("[Player] The player's nickname can't be null");
        if (board == null)
            throw new NullPointerException("[Player] The player's board can't be null");

        this.nickname = nickname;
        this.color = board.getTowerColor();
        this.board = board;
        cards = new ArrayList<AssistantCard>();
        coins = 0;
        selectedCard = Optional.empty();
        selectedIsland = Optional.empty();
        selectedColors = new ArrayList<SchoolColor>();
        selectedCloudTile = Optional.empty();
        selectedCharacterCard = Optional.empty();
    }

    /**
     * Method to add coins to the player.
     * 
     * @param coins The number of coins to be added.
     * @throws IllegalArgumentException Thrown if the parameter is negative.
     */
    public void addCoins(int coins) throws IllegalArgumentException
    {
        if (coins < 0)
            throw new IllegalArgumentException("[Player] The number of coins must be positive");

        this.coins += coins;
    }

    /**
     * Method to remove coins from the player.
     * 
     * @param coins The number of coins to remove.
     * @throws IllegalArgumentException Thrown if the parameter is higher than the player's coins.
     */
    public void removeCoins(int coins) throws IllegalArgumentException
    {
        if (coins < 0)
            throw new IllegalArgumentException(
                    "[Player] You can't remove a negative number of coins");

        if (this.coins < coins)
            throw new NotEnoughCoinsException();

        this.coins -= coins;
    }

    /**
     * Method to select an AssistantCard during the planning phase.
     * 
     * @param turnOrder The number of turn of the card.
     * @throws NoSuchAssistantCardException if the player hasn't got the selected card.
     */
    public void selectCard(int turnOrder) throws NoSuchAssistantCardException
    {
        // Set the current card to used
        if (selectedCard.isPresent())
            selectedCard.get().toggleUsed();

        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getTurnOrder() == turnOrder && !cards.get(i).isUsed())
            {
                selectedCard = Optional.of(cards.get(i));
                return;
            }
        }
        throw new NoSuchAssistantCardException("[Player]");
    }

    /**
     * Method to add an AssistantCard to the player.
     * 
     * @param card The card to be added.
     * @throws NullPointerException Thrown if the card is null.
     */
    public void addCard(AssistantCard card) throws NullPointerException
    {
        if (card == null)
            throw new NullPointerException("[Player] Null Assistant card");

        if (cards.isEmpty())
        {
            cards.add(card);
        } else if (cards.get(0).getWizard() == card.getWizard() && !cards.contains(card))
        {
            for (AssistantCard assistantCard : cards)
            {
                if (assistantCard.getTurnOrder() == card.getTurnOrder())
                    return;
            }
            cards.add(card);
        }
    }

    /**
     * Method to remove an AssistantCard from the player's list of cards.
     * 
     * @param turnOrder The number of turn of the card.
     * @throws NoSuchAssistantCardException if the player doesn't have the selected card.
     * @throws EndGameException if the last card is removed.
     */
    public void removeCard(int turnOrder) throws NoSuchAssistantCardException, EndGameException
    {
        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getTurnOrder() == turnOrder)
            {
                cards.remove(i);
                if (cards.size() == 0)
                    throw new EndGameException("Assistant cards finished");
                return;
            }
        }
        throw new NoSuchAssistantCardException("[Player]");
    }

    /**
     * Removes the currently selected card.
     */
    public void removeSelectedCard()
    {
        if (selectedCard.isPresent())
        {
            removeCard(selectedCard.get().getTurnOrder());

            selectedCard = Optional.empty();
        }
    }

    /**
     * Method to select an island.
     * 
     * @param islandIndex The index of the island.
     */
    public void selectIsland(int islandIndex)
    {
        selectedIsland = Optional.of(islandIndex);
    }

    /**
     * Method to select a color among SchoolColor.
     * 
     * @param color The color selected.
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public void selectColor(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Player] A null color was provided");

        selectedColors.add(color);
    }

    /**
     * Method to select a CloudTile during the last phase of the action phase.
     * 
     * @param tile The index of the CloudTile.
     */
    public void selectCloudTile(int tile)
    {
        selectedCloudTile = Optional.of(tile);
    }

    /**
     * Method to select a CharacterCard during the action phase.
     *
     * @param card The index of the CharacterCard.
     */
    public void selectCharacterCard(int card)
    {
        selectedCharacterCard = Optional.of(card);
    }

    /**
     * Method to call after every player's action to clear island and colors selected
     */
    public void clearSelections()
    {
        selectedIsland = Optional.empty();
        selectedColors.clear();
    }

    /**
     * Method to call at the end of the round to clear all the selections
     */
    public void clearSelectionsEndTurn()
    {
        selectedCard = Optional.empty();
        selectedIsland = Optional.empty();
        selectedColors.clear();
        selectedCloudTile = Optional.empty();
        selectedCharacterCard = Optional.empty();
    }

    public String getNickname()
    {
        return nickname;
    }

    public SchoolBoard getBoard()
    {
        return board;
    }

    public Optional<AssistantCard> getSelectedCard()
    {
        return selectedCard;
    }

    public Optional<Integer> getSelectedIsland()
    {
        return selectedIsland;
    }

    public List<SchoolColor> getSelectedColors()
    {
        return new ArrayList<>(selectedColors);
    }

    public Optional<Integer> getSelectedCloudTile()
    {
        return selectedCloudTile;
    }

    public Optional<Integer> getSelectedCharacterCard()
    {
        return selectedCharacterCard;
    }

    public List<AssistantCard> getCards()
    {
        return new ArrayList<AssistantCard>(cards);
    }

    public TowerColor getColor()
    {
        return color;
    }

    public int getCoins()
    {
        return coins;
    }
}
