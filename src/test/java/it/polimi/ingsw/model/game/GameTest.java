package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughPlayersException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class GameTest
{
    Game game;

    @BeforeEach
    public void init()
    {
        game = new Game(2, GameMode.CLASSIC);
    }

    @Test
    public void constructorTest()
    {
        assertThrows(NullPointerException.class, () -> game = new Game(null, GameMode.CLASSIC));
        assertThrows(IllegalArgumentException.class, () -> game = new Game(1, GameMode.CLASSIC));
        assertThrows(IllegalArgumentException.class, () -> game = new Game(5, GameMode.CLASSIC));
    }

    @Test
    public void addPlayerTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);
        Player player3 = new Player("Player3", TowerColor.WHITE);
        Player player4 = new Player("Player4", TowerColor.BLACK);
        Player player5 = new Player("Player5", TowerColor.GREY);

        // The game should accept 2 players
        game = new Game(2, GameMode.CLASSIC);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player3));

        // The game should accept 3 players
        game = new Game(3, GameMode.CLASSIC);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertDoesNotThrow(() -> game.addPlayer(player3));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player4));

        // The game should accept 4 players
        game = new Game(4, GameMode.CLASSIC);
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));
        assertDoesNotThrow(() -> game.addPlayer(player4));
        assertDoesNotThrow(() -> game.addPlayer(player5));
        assertThrows(TooManyPlayersException.class, () -> game.addPlayer(player3));
    }

    @Test
    /**
     * Covers both selectPlayer and getSelectedPlayer functions.
     */
    public void selectPlayerTest()
    {
        // Create the two players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Now there should not be any selected players
        assertTrue(() -> game.getSelectedPlayer().isEmpty());

        // Invalid indexes should not be accepted
        assertThrows(IndexOutOfBoundsException.class, () -> game.selectPlayer(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> game.selectPlayer(2));

        // Select the first player
        assertDoesNotThrow(() -> game.selectPlayer(0));
        assertDoesNotThrow(() -> game.getSelectedPlayer().get());
        assertEquals(game.getSelectedPlayer().get(), player1);

        // Select the second player
        assertDoesNotThrow(() -> game.selectPlayer(1));
        assertDoesNotThrow(() -> game.getSelectedPlayer().get());
        assertEquals(game.getSelectedPlayer().get(), player2);
    }

    @Test
    /**
     * Covers both getSortedPlayerList and getPlayerTableList functions.
     */
    public void getPlayerListTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // The table list order should correspond to the players order
        List<Player> tableList = game.getPlayerTableList();
        assertEquals(player1, tableList.get(0));
        assertEquals(player2, tableList.get(1));

        // Select a card only for one player
        player1.selectCard(10);

        // This should fail
        assertThrows(NoSuchElementException.class, () -> game.getSortedPlayerList());

        // Select a card for other players
        player2.selectCard(1);
        List<Player> sortedList = game.getSortedPlayerList();
        assertEquals(player1, sortedList.get(1));
        assertEquals(player2, sortedList.get(0));
    }

    @Test
    public void putStudentToIslandTest()
    {
        // Now there isn't a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.BLUE)));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Now there is still not a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.GREEN)));

        // Select the a player
        game.selectPlayer(1);

        // The selected player has not selected an island
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.RED)));

        // Select an island
        player2.selectIsland(3);

        // This should still fail because the game is not setup
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.YELLOW)));

        // Setup the game
        game.setupGame();

        int initialStudents = game.getIslands().get(3).getStudents().size();

        // Now it should work
        assertDoesNotThrow(() -> game.putStudentToIsland(new Student(SchoolColor.YELLOW)));

        // There should be a student in the selected island
        assertEquals(game.getIslands().get(3).getStudents().size(), initialStudents + 1);
    }

    @Test
    public void putStudentToDiningTest()
    {
        // Now there isn't a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToIsland(new Student(SchoolColor.BLUE)));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Now there is still not a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToDining(new Student(SchoolColor.GREEN)));

        // Select the a player
        game.selectPlayer(1);

        // Now it should work (even if the game is not setup)
        assertDoesNotThrow(() -> game.putStudentToDining(new Student(SchoolColor.YELLOW)));

        // There should be a student in the selected island
        assertEquals(player2.getBoard().getStudentsNumber(SchoolColor.YELLOW), 1);
    }

    @Test
    public void pickStudentFromEntrance()
    {
        // Now there isn't a selected player
        assertThrows(NoSuchElementException.class, () -> game.pickStudentFromEntrance());

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // This should fail because there isn't a selected player
        assertThrows(NoSuchElementException.class, () -> game.pickStudentFromEntrance());

        game.selectPlayer(1);

        // This should still fail because the selected player has not selected a color
        assertThrows(NoSuchElementException.class, () -> game.pickStudentFromEntrance());

        // Setup the game
        game.setupGame();

        // Select the first student's color in the player's entrance
        player2.selectColor(player2.getBoard().getStudentsInEntrance().get(0).getColor());

        // This should work
        assertDoesNotThrow(() -> game.pickStudentFromEntrance());
    }

    @Test
    public void conquerProfessorsTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Give a player a student and selected him
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.PINK));

        // Now this should not throw
        assertDoesNotThrow(() -> game.conquerProfessors());

        // The player should have the professor in his board
        assertEquals(1, player1.getBoard().getProfessors().size());
        assertEquals(SchoolColor.PINK, player1.getBoard().getProfessors().get(0).getColor());

        // Now give the other player the same number of students
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.PINK));

        // Now this should not throw
        assertDoesNotThrow(() -> game.conquerProfessors());

        // The professor should still be in the previous player
        assertEquals(1, player1.getBoard().getProfessors().size());
        assertEquals(0, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.PINK, player1.getBoard().getProfessors().get(0).getColor());

        // Now give the other player more students
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.PINK));

        // Now this should not throw
        assertDoesNotThrow(() -> game.conquerProfessors());

        // The professor should have been moved
        assertEquals(0, player1.getBoard().getProfessors().size());
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.PINK, player2.getBoard().getProfessors().get(0).getColor());
    }

    @Test
    public void moveMotherNatureTest()
    {
        // The game is not set up, this should throw and error
        assertThrows(NoSuchElementException.class, () -> game.moveMotherNature(3));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Now it should work and I check the mother nature index after the movement
        int index = game.getMotherNatureIndex().get();
        assertDoesNotThrow(() -> game.moveMotherNature(3));
        assertEquals((index + 3) % game.getIslands().size(), game.getMotherNatureIndex().get());
    }

    @Test
    public void isValidMotherNatureMovementTest()
    {
        // The game is not set up, this should throw
        assertThrows(NoSuchElementException.class, () -> game.isValidMotherNatureMovement(3));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Select the current player
        game.selectPlayer(0);

        // This should fail because the current player has not selected any card
        assertThrows(NoSuchElementException.class, () -> game.isValidMotherNatureMovement(2));

        // Select a card for the current player
        player1.selectCard(6); // Card number 6 with 3 movements

        assertFalse(() -> game.isValidMotherNatureMovement(0));
        assertTrue(() -> game.isValidMotherNatureMovement(1));
        assertTrue(() -> game.isValidMotherNatureMovement(2));
        assertTrue(() -> game.isValidMotherNatureMovement(3));
        assertFalse(() -> game.isValidMotherNatureMovement(4));
    }

    @Test
    public void computeInfluenceTest()
    {
        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // This should not accept a null student and invalid island indexes
        assertThrows(NullPointerException.class, () -> game.computePlayerInfluence(null, 0));
        assertThrows(IndexOutOfBoundsException.class,
                () -> game.computePlayerInfluence(player1, -1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> game.computePlayerInfluence(player2, 0));

        // The game is not set up, an exception should occur
        assertThrows(NoSuchElementException.class, () -> game.computeInfluence());
        assertThrows(IndexOutOfBoundsException.class, () -> game.computeInfluence(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> game.computeInfluence(0));

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        Island island = game.getCurrentIsland();
        int islandIndex = game.getIslands().indexOf(island);

        // Save the current status
        List<Tower> towers = island.getTowers();

        // The game is set up and mother nature has been positioned
        // Each player should have the same influence, hence no tower movement has to be performed
        assertDoesNotThrow(() -> game.computeInfluence());
        for (int i = 0; i < towers.size(); i++)
            assertEquals(towers.get(i), game.getCurrentIsland().getTowers().get(i));

        // Now put a tower on the island
        game.getCurrentIsland().addTower(new Tower(TowerColor.BLACK));
        towers = island.getTowers();

        // Check the influences
        assertEquals(1, game.computePlayerInfluence(player1, islandIndex));
        assertEquals(0, game.computePlayerInfluence(player2, islandIndex));

        // The player should have more influence but still nothing should move
        assertDoesNotThrow(() -> game.computeInfluence());
        for (int i = 0; i < towers.size(); i++)
            assertEquals(towers.get(i).getColor(),
                    game.getCurrentIsland().getTowers().get(i).getColor());
    }

    @Test
    public void moveStudentsFromCloudTileTest()
    {
        // No player is selected, this should fail
        assertThrows(NoSuchElementException.class, () -> game.moveStudentsFromCloudTile());

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // This should still fail because no player is still selected
        assertThrows(NoSuchElementException.class, () -> game.moveStudentsFromCloudTile());

        // Select a player
        game.selectPlayer(1);

        // This should still fail because the current player has not selected a cloud tile
        assertThrows(NoSuchElementException.class, () -> game.moveStudentsFromCloudTile());

        // Make the player select the second cloud tile
        player2.selectCloudTile(1);

        // This should still fail because the game is not set up and there are no cloud tiles
        assertThrows(IndexOutOfBoundsException.class, () -> game.moveStudentsFromCloudTile());

        // Setup the game
        game.setupGame();

        // Get the current status
        List<Student> studentsOnTheCloudTile = game.getCloudTiles().get(1).getStudents();
        List<Student> prevEntrance = player2.getBoard().getStudentsInEntrance();

        // This should finally succeed
        assertDoesNotThrow(() -> game.moveStudentsFromCloudTile());

        // The second cloud tile should now be empty
        assertEquals(0, game.getCloudTiles().get(1).getStudents().size());

        // The student must have moved to the current player's entrance
        assertEquals(prevEntrance.size() + studentsOnTheCloudTile.size(),
                player2.getBoard().getStudentsInEntrance().size());
    }

    @Test
    public void getStudentFromBagTest()
    {
        // When the game is not set up this should fail, the bag is empty
        assertThrows(NoSuchElementException.class, () -> game.getStudentFromBag());

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Now set up the game
        game.setupGame();

        // This should not fail
        assertDoesNotThrow(() -> game.getStudentFromBag());
    }

    @Test
    public void addStudentToBagTest()
    {
        // A null student should not be added to the bag
        assertThrows(NullPointerException.class, () -> game.addStudentToBag(null));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Save the current bag status
        List<Student> prevBag = game.getStudentBag();

        // Add a student to the bag
        Student student = new Student(SchoolColor.GREEN);
        game.addStudentToBag(student);

        // Check if the student is in the new bag
        assertDoesNotThrow(() -> game.getStudentBag().indexOf(student));

        // All the other students should still be there
        assertEquals(prevBag.size() + 1, game.getStudentBag().size());
    }

    @Test
    public void removeProfessorTest()
    {
        // Invalid indexes should make the method fail
        assertThrows(IndexOutOfBoundsException.class, () -> game.removeProfessor(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> game.removeProfessor(0));

        // Create the players
        Player player1 = new Player("Player1", TowerColor.BLACK);
        Player player2 = new Player("Player2", TowerColor.GREY);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(player1));
        assertDoesNotThrow(() -> game.addPlayer(player2));

        // Setup the game
        game.setupGame();

        // Save the current status
        List<Professor> professors = game.getProfessors();

        // Now removing a professor should succeed
        assertDoesNotThrow(() -> game.removeProfessor(0));

        // Check if the professor has been removed
        assertEquals(professors.size() - 1, game.getProfessors().size());
        for (int i = 0; i < game.getProfessors().size(); i++)
            assertEquals(professors.get(1 + i), game.getProfessors().get(i));
    }

    @Test
    public void getCharacterCardsTest()
    {
        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(new Player("Player1", TowerColor.BLACK)));
        assertDoesNotThrow(() -> game.addPlayer(new Player("Player2", TowerColor.GREY)));

        // Setup the game
        game.setupGame();

        // With a classic game the character cards should not be present
        assertEquals(0, game.getCharacterCards().size());

        // Setup an expert game
        game = new Game(2, GameMode.EXPERT);

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(new Player("Player1", TowerColor.BLACK)));
        assertDoesNotThrow(() -> game.addPlayer(new Player("Player2", TowerColor.GREY)));

        // Setup the game
        game.setupGame();

        // Now the game should have 3 random character cards
        List<CharacterCard> cards = game.getCharacterCards();
        assertEquals(3, cards.size());
        for (CharacterCard characterCard : cards)
            assertNotNull(characterCard);
        for (CharacterCard card1 : cards)
            for (CharacterCard card2 : cards)
                if (card1 != card2)
                    assertNotEquals(card1.getCardType(), card2.getCardType());

    }

    @Test
    public void getCurrentCharacterCardTest()
    {
        // Whit out the game set up this should return an empty optional
        assertThrows(NoSuchElementException.class, () -> game.getCurrentCharacterCard());

        // Setup an expert game
        game = new Game(2, GameMode.EXPERT);

        // This should still be true
        assertThrows(NoSuchElementException.class, () -> game.getCurrentCharacterCard());

        // Add the players to the game
        assertDoesNotThrow(() -> game.addPlayer(new Player("Player1", TowerColor.BLACK)));
        assertDoesNotThrow(() -> game.addPlayer(new Player("Player2", TowerColor.GREY)));

        // Setup the game
        game.setupGame();

        // Select the character card
        game.setCurrentCharacterCard(1);

        // Get the current card
        Optional<CharacterCard> card = game.getCurrentCharacterCard();

        // The card should be present and the expected one
        assertTrue(card.isPresent());
        assertEquals(game.getCharacterCards().get(1), card.get());

        // Clear selection
        assertFalse(game.currentCharacterCardIndex.isEmpty());
        game.clearTurn();
        assertTrue(game.currentCharacterCardIndex.isEmpty());

        // Select an out of bound card
        game.setCurrentCharacterCard(42);

        // This should error out
        assertTrue(game.getCurrentCharacterCard().isEmpty());
    }

    @Test
    public void getCurrentIslandTest()
    {
        // When the game is not set up this should throw
        assertThrows(NoSuchElementException.class, () -> game.getCurrentIsland());
    }

    @Test
    public void setupGameTest()
    {
        // Not all the expected players have been added to the game
        assertThrows(NotEnoughPlayersException.class, () -> game.setupGame());

        // Check the players number and the game mode
        assertEquals(2, game.getPlayersNumber());
        assertEquals(GameMode.CLASSIC, game.getGameMode());
    }
}
