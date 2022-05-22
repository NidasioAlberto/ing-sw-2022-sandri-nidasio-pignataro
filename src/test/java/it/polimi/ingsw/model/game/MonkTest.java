package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the Monk class
 */
public class MonkTest
{
    // Setup
    CharacterCard monk;
    Game game;
    List<Student> studentsBag;
    Player player1 = new Player("Player1", TowerColor.WHITE, GameMode.EXPERT);
    Player player2 = new Player("Player2", TowerColor.BLACK, GameMode.EXPERT);

    @BeforeEach
    public void init()
    {
        game = new Game();
        try
        {
            game.addPlayer(player1);
            game.addPlayer(player2);
        }
        catch (TooManyPlayersException e)
        {
            e.printStackTrace();
        }
        game.setupGame();
        studentsBag = game.getStudentBag();
        monk = CharacterCard.createCharacterCard(CharacterCardType.MONK, game);
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Monk(null));

        // Check the card type
        assertEquals(CharacterCardType.MONK, monk.getCardType());

        // Check the initial cost
        assertEquals(1, monk.cost);

        // Check that 4 students have been removed from the bag
        assertEquals(studentsBag.size() - 4, game.getStudentBag().size());

        // At the beginning the card is not active
        assertFalse(monk.activated);

        // At the beginning the card isn't used
        assertFalse(monk.firstUsed);

        assertNotEquals(null, monk.toString());
    }

    @Test
    public void isValidActionTest()
    {
        // The card is always playable
        assertTrue(monk.isPlayable());

        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> monk.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        game.getSelectedPlayer().get().getBoard().removeCoins(1);
        assertThrows(NotEnoughCoinsException.class, () -> monk.activate());

        try
        {
            // Activate the card
            player1.getBoard().addCoins(1);
            monk.activate();
            assertEquals(0, player1.getBoard().getCoins());
            for (ExpertGameAction action: ExpertGameAction.values())
            {
                // When Monk is active the only valid action is MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND
                if (action == ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND)
                    assertTrue(monk.isValidAction(action));
                else assertFalse(monk.isValidAction(action));
            }
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(monk.firstUsed);
        assertEquals(2, monk.cost);
        assertTrue(monk.activated);

        // I deactivate the card
        monk.deactivate();
        assertFalse(monk.activated);

        try
        {
            // Activate the card
            player1.getBoard().addCoins(2);
            monk.activate();
            assertEquals(0, player1.getBoard().getCoins());
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(monk.firstUsed);
        assertEquals(2, monk.cost);
        assertTrue(monk.activated);
    }

    @Test
    public void applyActionTest()
    {
        // An exception is thrown if I call the method without a selected player
        monk.activated = true;
        NoSuchElementException e3 = assertThrows(NoSuchElementException.class, () -> monk.applyAction());
        assertEquals("[Monk] No player selected", e3.getMessage());

        monk.activated = false;

        // Select a player
        player1.getBoard().addCoins(1);
        game.selectPlayer(0);

        // Get the students on the card
        List<Student> students = ((Monk) monk).getStudents();

        // The card isn't active so nothing happens
        monk.applyAction();
        for (Student student : students)
        {
            assertTrue(((Monk) monk).getStudents().contains(student));
        }

        // Activate the card
        try {
            monk.activate();
        } catch (NotEnoughCoinsException e) {
            e.printStackTrace();
        }

        // An exception is thrown if no colors are selected
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> monk.applyAction());
        assertEquals("[Monk] No color selected", e.getMessage());

        // Search a student color not present on the card
        int counter;
        for (SchoolColor color : SchoolColor.values()) {
            counter = 0;
            for (Student student : ((Monk) monk).getStudents()) {
                if (student.getColor() != color)
                    counter++;
            }
            if (counter == ((Monk) monk).getStudents().size()) {
                player1.selectColor(color);
                break;
            }
        }

        // An exception is thrown if the player selects a student color not present on the card
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class, () -> monk.applyAction());
        assertEquals("[Monk] No such student on card", e1.getMessage());

        // Clear the selections to do another test
        player1.clearSelections();

        // Select a student color present on the card
        Student selectedStudent = students.get(0);
        player1.selectColor(selectedStudent.getColor());

        // An exception is thrown if there isn't a selected island
        NoSuchElementException e2 = assertThrows(NoSuchElementException.class, () -> monk.applyAction());
        assertEquals("[Game] No island selected", e2.getMessage());

        // The player selects an island where there is already a student
        int islandIndex = 0;
        if (game.getIslands().get(islandIndex).getStudents().size() == 0)
            islandIndex++;
        assertEquals(1, game.getIslands().get(islandIndex).getStudents().size());
        player1.selectIsland(islandIndex);

        // Check that the action is applied accurately
        monk.applyAction();
        // The selected student is on the selected island
        assertEquals(selectedStudent, game.getIslands().get(islandIndex).getStudents().get(1));
        assertEquals(2, game.getIslands().get(islandIndex).getStudents().size());
        // The selected student is no more on the card
        assertFalse(((Monk) monk).getStudents().contains(selectedStudent));
        // The non selected students are still on the card
        for (Student student : students)
        {
            if (student != selectedStudent)
                assertTrue(((Monk) monk).getStudents().contains(student));
        }
        // A new student is added on the card
        assertEquals(4, ((Monk) monk).getStudents().size());
        // At the end the card deactivates by itself
        assertFalse(monk.activated);
    }
}
