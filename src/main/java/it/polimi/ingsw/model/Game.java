package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game
{
    public static final int MAX_PLAYERS = 4;

    private List<Player> players;

    private List<Island> islands;

    private List<CloudTile> cloudTiles;

    private List<Student> studentBag;

    private List<CharacterCard> characterCards;

    private int currentPlayerIndex;

    private int motherNatureIndex;

    private GameAction state;

    private int playerNumber;

    public Game()
    {}

    public void addPlayer(Player player)
    {}

    public void selectPlayer(int index)
    {}

    public Player getSelectedPlayer()
    {}

    public void setupTiles()
    {}

    public List<Player> getSortedPlayerList()
    {}

    public List<Player> getPlayerTableList()
    {}

    public void moveStudentToIsland(SchoolColor color, int islandIndex)
    {}

    public void moveStudentToDining(SchoolColor color)
    {}

    public void moveMotherNature(int steps)
    {}

    public void conquer()
    {}

    public void moveStudentsFromCloudTile(int tileIndex)
    {}

    public Optional<Game> isValidAction(GameAction action)
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
