package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.Test;

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
        Player player1 = new Player("Player1", TowerColor.WHITE);
        Player player2 = new Player("Player2", TowerColor.BLACK);

        game = new Game();
        thief = CharacterCard.createCharacterCard(CharacterCardType.THIEF, game);

        // Too many players are added to the game
        try
        {
            game.addPlayer(player1);
            thief.addPlayer(player2);
            game.addPlayer(new Player("Player3", TowerColor.GREY));
        } catch (TooManyPlayersException e)
        {
            assertEquals(2, e.getCurrentPlayersNumber());
        }

        thief.setupGame();

        // Check that the players have been added accurately
        assertEquals(2, thief.getPlayerTableList().size());
        assertTrue(thief.getPlayerTableList().contains(player1));
        assertEquals(player2, thief.getPlayerTableList().get(1));

        // Select a player
        thief.selectPlayer(0);
        assertEquals(player1, thief.getSelectedPlayer().get());

        // Pick a student from the entrance
        SchoolColor color = player1.getBoard().getStudentsInEntrance().get(0).getColor();
        player1.selectColor(color);
        Student student = thief.pickStudentFromEntrance();
        assertEquals(color, student.getColor());

        // Put student to an island
        int islandIndex = 0;
        if (game.getIslands().get(islandIndex).getStudents().size() == 0)
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
        thief.computeInfluence();
        if (game.getMotherNatureIndex().get() == islandIndex)
            assertEquals(1,
                    game.getIslands().get(game.getMotherNatureIndex().get()).getTowers().size());
        else
            assertEquals(0,
                    game.getIslands().get(game.getMotherNatureIndex().get()).getTowers().size());

        // Move students from cloud tile
        CloudTile cloud = game.getCloudTiles().get(0);
        assertEquals(0, cloud.getStudents().size());
        cloud.addStudent(new Student(SchoolColor.GREEN));
        assertEquals(6, player1.getBoard().getStudentsInEntrance().size());
        player1.selectCloudTile(0);
        thief.moveStudentsFromCloudTile();
        assertEquals(7, player1.getBoard().getStudentsInEntrance().size());

        // The game is in classic mode so there are no character cards
        assertEquals(0, thief.getCharacterCards().size());
    }
}
