package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PrincessTest
{
    Game game;
    CharacterCard princess;
    Player player1;
    Player player2;
    SchoolBoard board1;
    SchoolBoard board2;
    List<Student> studentsOnCard;

    // Number of students before the character card decorator
    List<Student> originalBagStudents;

    @BeforeEach
    public void init()
    {
        studentsOnCard = new ArrayList<Student>();
        // I have to initialize a Game, a Player and a School Board to ensure
        // the character card can behave correctly
        board1 = new SchoolBoard(TowerColor.BLACK, GameMode.EXPERT);
        board2 = new SchoolBoard(TowerColor.WHITE, GameMode.EXPERT);
        player1 = new Player("pippo", board1);
        player2 = new Player("peppo", board2);
        game = new Game();

        // Add the player to the game
        try
        {
            game.addPlayer(player1);
            game.addPlayer(player2);
        } catch (Exception e)
        {
        }

        // Setup the game
        game.setupGame();

        // Memorize the original bag
        originalBagStudents = game.getStudentBag();

        // Now i can instantiate the character card
        princess = CharacterCard.createCharacterCard(CharacterCardType.PRINCESS, game);
        princess.init();

        // I add the student to the students on card list if it is not present anymore
        for (Student student : originalBagStudents)
        {
            if (!princess.getStudentBag().contains(student))
                studentsOnCard.add(student);
        }
    }

    @Test
    public void constructorTest()
    {
        // Null pointers inside the factory method
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(null, game));
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(CharacterCardType.KNIGHT, null));

        // Type confirmation
        assertEquals(CharacterCardType.PRINCESS, princess.getCardType());

        // Cost
        assertEquals(2, princess.cost);

        // Not already used
        assertEquals(false, princess.firstUsed);
        assertEquals(false, princess.isActivated());

        assertNotEquals(null, princess.toString());

        assertEquals(4, ((Princess) princess).getStudents().size());
    }

    @Test
    public void generalCardTest()
    {
        // If we don't select a player we expect noSuchElementException
        assertThrows(NoSuchElementException.class, () -> princess.activate());

        // Player selection and card activation
        game.selectPlayer(0);

        // If i activate the card with no coins i should get an error
        assertThrows(NotEnoughCoinsException.class, () -> princess.activate());

        // Add the coins to activate the card
        player1.getBoard().addCoins(10);

        try
        {
            princess.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 2 coin less
        assertEquals(9, player1.getBoard().getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, princess.firstUsed);
        assertEquals(true, princess.isActivated());
        // The card should cost now 3 coins
        assertEquals(3, princess.cost);

        // If i try to activate the card while already activated nothing should happen
        try
        {
            princess.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, princess.firstUsed);
        assertEquals(true, princess.isActivated());
        assertEquals(3, princess.cost);
        assertEquals(9, player1.getBoard().getCoins());

        // Finally if I deactivate the card the cost will be 3 coins
        princess.deactivate();

        try
        {
            princess.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, princess.firstUsed);
        assertEquals(true, princess.isActivated());
        assertEquals(3, princess.cost);
        assertEquals(6, player1.getBoard().getCoins());
    }

    @Test
    public void isPlayableTest()
    {
        // This test should always be true
        assertEquals(true, princess.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        // This card should only allow MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING
        for (ExpertGameAction action : ExpertGameAction.values())
        {
            if (action == ExpertGameAction.MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING)
                assertEquals(true, princess.isValidAction(action));
            else
                assertEquals(false, princess.isValidAction(action));
        }
    }

    @Test
    public void applyActionTest()
    {
        // If i call apply action without selecting a player, it should throw an exception
        princess.activated = true;
        assertThrows(NoSuchElementException.class, () -> princess.applyAction());

        // Now i select a player but not a color
        game.selectPlayer(0);

        assertThrows(NoSuchElementException.class, () -> princess.applyAction());

        // Now i look for a non-present color on the card to select
        player1.selectColor(Arrays.stream(SchoolColor.values())
                .filter(c -> studentsOnCard.stream().filter(s -> s.getColor() == c).findFirst().isEmpty()).findFirst().get());

        assertThrows(NoSuchElementException.class, () -> princess.applyAction());

        // Now i test the correct case
        SchoolColor selected = studentsOnCard.get(0).getColor();

        player1.clearSelections();
        player1.selectColor(selected);
        princess.applyAction();

        Optional<Student> removed;
        int count = 0;
        // Verify that the student of that color was on the card
        while ((removed = player1.getBoard().removeStudentFromDining(selected)).isPresent())
        {
            if (studentsOnCard.contains(removed.get()))
                count++;
        }

        assertEquals(1, count);

        // Remove all the students from the bag to test the princess when the game is ending
        while (princess.getStudentBag().size() > 0)
            try
            {
                princess.getStudentFromBag();
            } catch (EndGameException e)
            {
            }

        // Apply the action when the game is ending
        princess.activated = true;
        SchoolColor selected1 = ((Princess) princess).getStudents().get(0).getColor();
        player1.clearSelections();
        player1.selectColor(selected1);
        assertDoesNotThrow(() -> princess.applyAction());
        assertNotEquals(null, princess.toString());
    }
}
