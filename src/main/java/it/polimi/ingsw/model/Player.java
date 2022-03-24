package it.polimi.ingsw.model;

import java.util.List;
import java.util.ArrayList;

/**
 * This class represent one of the game's player
 */
public class Player
{
    /**
     * The nickname of the player
     */
    private String nickname;

    /**
     * The school board of the player
     */
    private SchoolBoard board;

    /**
     * The list of assistant card that the player can still play
     */
    private List<AssistantCard> cards;

    /**
     * The player's color is associated with the color of the towers in his school board
     */
    // volendo si può ricavare dalla schoolBoard chiamando getTowers e poi getColor
    private TowerColor color;

    /**
     * The number of coins that player has in order to play character cards
     */
    private int coins;

    /**
     * The assistant card selected by the player during the planning phase
     */
    private int selectedCard;

    /**
     * The island selected by the player during an action
     */
    private int selectedIsland;

    /**
     * The student color selected by the player during an action
     */
    private SchoolColor selectedColor;

    /**
     * The cloud tile selected by the player at the end of the action phase
     */
    private int selectedCloudTile;

    /**
     * Constructor
     */
    // TODO
    // viene già passata la board?
    // bisogna costruire in questo costruttore le 10 carte assistente? in tal caso serve lo wizard
    public Player(String nickname, SchoolBoard board)
    {
        this.nickname = nickname;
        coins = 0;
        this.board = board;
        cards = new ArrayList<AssistantCard>();
    }

    /**
     * Method to add coins
     * 
     * @param coins The number of coins to be added
     * @throws IllegalArgumentException Thrown if the parameter is negative
     */
    public void addCoins(int coins) throws IllegalArgumentException
    {
        if (coins < 0)
            throw new IllegalArgumentException("[Player] The number of coins must be positive");

        this.coins += coins;
    }

    /**
     * Method to remove coins
     * 
     * @param coins The number of coins to remove
     * @throws IllegalArgumentException Thrown if the parameter is higher than the player's coins
     */
    public void removeCoins(int coins) throws IllegalArgumentException
    {
        if (this.coins < coins)
            throw new IllegalArgumentException("[Player] There aren't enough coins");

        this.coins -= coins;
    }

    /**
     * Method to select an AssistantCard during the planning phase
     * 
     * @param turnOrder The number of turn of the card
     * @throws IllegalArgumentException Thrown if the player hasn't got the selected card
     */
    // TODO si potrebbe mettere removeCard direttamente in selectCard
    public void selectCard(int turnOrder) throws IllegalArgumentException
    {
        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getTurnOrder() == turnOrder)
            {
                selectedCard = i;
                return;
            }
        }
        throw new IllegalArgumentException("[Player] There isn't a card with such turnOrder");
    }

    /**
     * Method to remove an AssistantCard from the player's list of cards
     * 
     * @param turnOrder The number of turn of the card
     * @throws IllegalArgumentException Thrown if the player hasn't got the selected card
     */
    public void removeCard(int turnOrder) throws IllegalArgumentException
    {
        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getTurnOrder() == turnOrder)
            {
                cards.remove(i);
                return;
            } else
            {
                ///
            }
        }
        throw new IllegalArgumentException("[Player] There isn't a card with such turnOrder");
    }

    /**
     * Method to select an island
     * 
     * @param island The index of the island
     */
    public void selectIsland(int island)
    {
        selectedIsland = island;
    }

    /**
     * Method to select a color among SchoolColor
     * 
     * @param color The color selected
     * @throws NullPointerException Thrown if the specified color is invalid
     */
    public void selectColor(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Player] A null color was provided");

        selectedColor = color;
    }

    /**
     * Method to select a CloudTile during the last phase of the action phase
     * 
     * @param tile The index of the CloudTile
     */
    public void selectCloudTile(int tile)
    {
        selectedCloudTile = tile;
    }

    /**
     * Getters
     */
    public String getNickname()
    {
        return nickname;
    }

    public SchoolBoard getBoard()
    {
        return board;
    }

    public int getSelectedCard()
    {
        return selectedCard;
    }

    public int getSelectedIsland()
    {
        return selectedIsland;
    }

    public SchoolColor getSelectedColor()
    {
        return selectedColor;
    }

    public int getSelectedCloudTile()
    {
        return selectedCloudTile;
    }

    public List<AssistantCard> getCardsList()
    {
        return new ArrayList<AssistantCard>(cards);
    }

    public AssistantCard[] getCards()
    {
        List<AssistantCard> list = getCardsList();
        AssistantCard[] result = new AssistantCard[list.size()];
        list.toArray(result);
        return result;
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
