package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.*;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Game
{
    public static final int MAX_PLAYERS = 4;

    /**
     * List of all the players in the game in table order (as they are added to the game).
     */
    private List<Player> players;

    private List<Island> islands;

    private List<CloudTile> cloudTiles;

    private List<Student> studentBag;

    private List<CharacterCard> characterCards;

    private Optional<Integer> currentPlayerIndex;

    private int motherNatureIndex;

    private GameAction state;

    private int playerNumber;


    public Game()
    {
        players = new ArrayList<Player>();
        islands = new ArrayList<Island>();
        cloudTiles = new ArrayList<CloudTile>();
        studentBag = new ArrayList<Student>();
        characterCards = new ArrayList<CharacterCard>();
        currentPlayerIndex = Optional.of(null);
    }

    /**
     * Adds a player to the current game. If the game already contains the maximum number of players
     * possible, an exception is thrown.
     */
    public void addPlayer(Player player) throws TooManyPlayersException
    {
        if (players.size() > MAX_PLAYERS)
            throw new TooManyPlayersException(players.size());
        players.add(player);
    }

    /**
     * Changes the selected player. If the index is invalid, and exception is thrown.
     */
    public void selectPlayer(Integer index) throws IndexOutOfBoundsException
    {
        try
        {
            players.get(index);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IndexOutOfBoundsException("[Game] The specified player index is not valid");
        }

        currentPlayerIndex = Optional.of(index);
    }

    /**
     * Returns the currently selected player, if any.
     * 
     * TODO: forse non è il caso di esporre il player direttamente, e una copia non mi sembra il
     * caso. Forse dobbiamo ritornare il nickname?
     */
    public Optional<Player> getSelectedPlayer()
    {
        return currentPlayerIndex.map(index -> players.get(index));
    }

    /**
     * TODO: Cosa dovrebbe fare?
     */
    public void setupTiles()
    {}

    /**
     * Return the players list sorted by their turn order based on the played assistant cards. TODO:
     * Ci deve essere un modo più bello per fare il sort e ritornare una nuova lista.
     */
    public List<Player> getSortedPlayerList()
    {
        List<Player> sortedList = new ArrayList<>(players);

        sortedList.sort((a, b) -> a.getCardsList().get(a.getSelectedCard()).getTurnOrder()
                - b.getCardsList().get(b.getSelectedCard()).getTurnOrder());
        return sortedList;
    }

    /**
     * Returns the players list sorted by table order.
     */
    public List<Player> getPlayerTableList()
    {
        return players;
    }

    /**
     * Moves a student of the specified color from the bag to the specified island. If the student
     * or the island are not found, an exception is thrown.
     */
    public void moveStudentToIsland(SchoolColor color, int islandIndex)
            throws NoSuchElementException
    {
        Student student;

        // Extract a student with the specified color from the bag
        try
        {
            student = studentBag.stream().filter(s -> s.getColor().equals(color)).findFirst().get();
        } catch (NoSuchElementException e)
        {
            throw new NoSuchElementException(
                    "[Game] There is not student in the bag with color " + color.toString());
        }

        // Move the student to the island
        try
        {

            islands.get(islandIndex).addStudent(student);
        } catch (IndexOutOfBoundsException e)
        {
            throw new NoSuchElementException("[Game] There is no island with index " + islandIndex);
        }
    }

    public void moveStudentToDining(SchoolColor color)
    {}

    public void moveMotherNature(int steps)
    {}

    public void conquer()
    {}

    public void moveStudentsFromCloudTile(int tileIndex)
    {}

    public boolean isValidAction(GameAction action)
    {}

    public void setupGame()
    {}

    public int getStudentsFromCloudTile(int tileIndex)
    {}

    public GameAction getGameAction()
    {}

    public List<CharacterCard> getCharacterCards()
    {}
}
