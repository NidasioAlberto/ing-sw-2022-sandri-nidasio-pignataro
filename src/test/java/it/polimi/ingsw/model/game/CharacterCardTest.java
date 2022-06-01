package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the CharacterCard class
 */
public class CharacterCardTest
{
    /**
     * Test all the methods in CharacterCard that simply call the respective method in game.
     */
    @Test
    public void test()
    {
        // Set up a game and a random card
        CharacterCard thief;
        Game game;
        Player player1 = new Player("Player1", TowerColor.WHITE, GameMode.CLASSIC);
        Player player2 = new Player("Player2", TowerColor.BLACK, GameMode.CLASSIC);

        game = new Game(2, GameMode.EXPERT);
        thief = CharacterCard.createCharacterCard(CharacterCardType.THIEF, game);

        // Too many players are added to the game
        try
        {
            game.addPlayer(player1);
            thief.addPlayer(player2);
            game.addPlayer(new Player("Player3", TowerColor.GREY, GameMode.CLASSIC));
        } catch (TooManyPlayersException e)
        {
            assertEquals(2, e.getCurrentPlayersNumber());
        }

        thief.setupGame();

        // Check that the players have been added accurately
        assertEquals(2, thief.getPlayerTableList().size());
        assertTrue(thief.getPlayerTableList().contains(player1));
        assertEquals(player2, thief.getPlayerTableList().get(1));

        // Check the cost
        assertEquals(3, thief.getCost());

        // Select a player
        thief.selectPlayer(0);
        assertEquals(player1, thief.getSelectedPlayer().get());
        assertEquals(0, thief.getSelectedPlayerIndex().get());

        // Pick a student from the entrance
        SchoolColor color = player1.getBoard().getStudentsInEntrance().get(0).getColor();
        player1.selectColor(color);
        Student student = thief.pickStudentFromEntrance();
        assertEquals(color, student.getColor());

        // Put student to an island
        int islandIndex = 0;
        if (thief.getIslands().get(islandIndex).getStudents().size() == 0)
            islandIndex++;
        player1.selectIsland(islandIndex);
        thief.putStudentToIsland(new Student(color));
        assertEquals(color, game.getIslands().get(islandIndex).getStudents().get(1).getColor());
        assertEquals(2, game.getIslands().get(islandIndex).getStudents().size());

        // Check that nobody has a professor
        assertEquals(0, player1.getBoard().getProfessors().size());
        assertEquals(0, player2.getBoard().getProfessors().size());
        // Put a student to dining
        assertEquals(0, player1.getBoard().getStudentsNumber(color));
        thief.putStudentToDining(student);
        assertEquals(1, player1.getBoard().getStudentsNumber(color));

        // Conquer professors
        assertEquals(1, player1.getBoard().getProfessors().size());
        assertEquals(0, player2.getBoard().getProfessors().size());
        assertEquals(color, player1.getBoard().getProfessors().get(0).getColor());

       // Move mother nature
        player1.selectCard(9);
        player2.selectCard(10);
        assertEquals(player1, thief.getSortedPlayerList().get(0));
        assertEquals(player2, thief.getSortedPlayerList().get(1));
        assertTrue(((CharacterCard) thief).isValidMotherNatureMovement(3));
        int index = game.getMotherNatureIndex().get();
        thief.moveMotherNature(3);
        assertEquals((index + 3) % game.getIslands().size(), game.getMotherNatureIndex().get());

        // Compute player influence
        if (game.getIslands().get(islandIndex).getStudents().get(0).getColor() == color)
            assertEquals(2, thief.computePlayerInfluence(player1, islandIndex));
        else
            assertEquals(1, thief.computePlayerInfluence(player1, islandIndex));

        // Compute influence
        assertEquals(0, game.getIslands().get(islandIndex).getTowers().size());
        thief.computeInfluence(islandIndex);
        assertEquals(1, game.getIslands().get(islandIndex).getTowers().size());
        assertEquals(TowerColor.WHITE,
                game.getIslands().get(islandIndex).getTowers().get(0).getColor());
        game.motherNatureIndex = Optional.of(islandIndex);
        thief.computeInfluence();
        assertEquals(1, game.getIslands().get(islandIndex).getTowers().size());

        // Conquer professor
        if (color == SchoolColor.GREEN)
            thief.getPlayerTableList().get(0).getBoard().addStudentToDiningRoom(new Student(SchoolColor.PINK));
        else thief.getPlayerTableList().get(0).getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        thief.conquerProfessors();
        assertEquals(2, player1.getBoard().getProfessors().size());
        assertEquals(0, player2.getBoard().getProfessors().size());

        // Move students from cloud tile
        CloudTile cloud = game.getCloudTiles().get(0);
        assertEquals(0, cloud.getStudents().size());
        cloud.addStudent(new Student(SchoolColor.GREEN));
        assertEquals(6, player1.getBoard().getStudentsInEntrance().size());
        player1.selectCloudTile(0);
        thief.moveStudentsFromCloudTile();
        assertEquals(7, player1.getBoard().getStudentsInEntrance().size());

        // Fill clouds
        thief.fillClouds();
        assertEquals(3, thief.getCloudTiles().get(0).getStudents().size());

        // The game is in expert mode so there are 3 character cards
        assertEquals(3, thief.getCharacterCards().size());
        assertDoesNotThrow(() -> thief.clearTurn());
        assertDoesNotThrow(() -> thief.clearCharacterCard());
        assertDoesNotThrow(() -> thief.setCurrentCharacterCard(0));

        // Add a student to bag
        int bagSize = thief.getStudentBag().size();
        thief.addStudentToBag(new Student(SchoolColor.BLUE));
        assertEquals(bagSize + 1, thief.getStudentBag().size());

        // Remove a professor
        int professorSize = thief.getProfessors().size();
        thief.removeProfessor(0);
        assertEquals(professorSize - 1, thief.getProfessors().size());

        // Check cloud tiles
        assertEquals(2, thief.getCloudTiles().size());

        // Check mother nature index
        int motherNatureIndex = thief.getMotherNatureIndex().get();
        assertEquals(motherNatureIndex, thief.getIslands().indexOf(thief.getCurrentIsland()));

        // Check notify players
        assertDoesNotThrow(() -> thief.notifyPlayers());

        // Check to string
        thief.activated = true;
        assertNotEquals(null, thief.toString());

        // Check game mode
        assertEquals(GameMode.EXPERT, thief.getGameMode());
    }

    @Test
    public void cloneTest()
    {
        Game game = new Game();
        CharacterCard card = CharacterCard.createCharacterCard(CharacterCardType.THIEF, game);

        // Tweak a little the card values
        card.activated = true;
        card.cost = 1000;

        // Now i clone the card
        CharacterCard cloned = card.clone();

        // Verify that nothing changed except the instance pointer
        assertEquals(cloned.cost, card.cost);
        assertEquals(cloned.activated, card.activated);
        assertEquals(cloned.firstUsed, card.firstUsed);
        assertEquals(cloned.instance, null);

        // Verify that it isn't the same object
        card.firstUsed = true;

        assertEquals(cloned.firstUsed, false);
    }
}
