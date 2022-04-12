package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JokerTest
{
    Game game;
    CharacterCard joker;
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
        board1 = new SchoolBoard(TowerColor.BLACK);
        board2 = new SchoolBoard(TowerColor.WHITE);
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

        originalBagStudents = game.getStudentBag();

        // Now i can instanciate the character card
        joker = CharacterCard.createCharacterCard(CharacterCardType.JOKER, game);

        // I add the student to the students on card list if it is not present anymore
        for (Student student : originalBagStudents)
        {
            if (!joker.getStudentBag().contains(student))
                studentsOnCard.add(student);
        }
    }

    @Test
    public void constructorTest()
    {
        // Null decorated instance
        assertThrows(NullPointerException.class, () -> new Joker(null));

        // I need to verify if 6 students have been subtracted from game
        assertEquals(originalBagStudents.size() - 6, game.getStudentBag().size());

        // Verify the cost
        assertEquals(1, joker.cost);
    }

    @Test
    public void generalCardTest()
    {
        // Check the type
        assertEquals(CharacterCardType.JOKER, joker.getCardType());

        // If we don't select a player we expect noSuchElementException
        assertThrows(NoSuchElementException.class, () -> joker.activate());

        // Player selection and joker activation
        game.selectPlayer(0);

        // If i activate the card with no coins i should get an error
        assertThrows(NotEnoughCoinsException.class, () -> joker.activate());

        // Add the coins to activate the card
        player1.addCoins(10);

        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 1 coin less
        assertEquals(9, player1.getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, joker.firstUsed);
        assertEquals(true, joker.activated);
        // The card should cost now 2 coins
        assertEquals(2, joker.cost);

        // If i try to activate the card while already activated nothing should happen
        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, joker.firstUsed);
        assertEquals(true, joker.activated);
        assertEquals(2, joker.cost);
        assertEquals(9, player1.getCoins());

        // Finally if I deactivate the card the cost will be 2 coins
        joker.deactivate();

        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, joker.firstUsed);
        assertEquals(true, joker.activated);
        assertEquals(2, joker.cost);
        assertEquals(7, player1.getCoins());
    }

    @Test
    public void isPlayableTest()
    {
        // This card should be always playable
        assertEquals(true, joker.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        // Player selection
        game.selectPlayer(0);

        //TODO non viene lanciata

        // Check if null is rejected
        //assertThrows(NullPointerException.class, () -> joker.isValidAction(null));

        /*
        // If i don't activate the card isValidAction should always be the same as Game
        // TODO delete this check when isValidAction will be removed
        for (ExpertGameAction action : ExpertGameAction.values())
            assertEquals(game.isValidAction(action), joker.isValidAction(action));
        */

        // So that i don't have any problem with the card activation
        player1.addCoins(10);

        // Enable the card
        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        // Theoretically if i don't perform any action, any number of swap student from character
        // card to entrance should be allowed
        for (int i = 0; i < 100; i++)
            assertEquals(true,
                    joker.isValidAction(ExpertGameAction.SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE));

        /*
        // For all the other actions the answer should be the same as game
        // TODO delete this check when isValidAction will be removed
        for (ExpertGameAction action : ExpertGameAction.values())
        {
            if (action != ExpertGameAction.SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE)
                assertEquals(game.isValidAction(action), joker.isValidAction(action));
        }

        // After that move the card should be deactivated
        //assertEquals(false, joker.activated);
        */

        // I need to activate the card another time
        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        // When i apply the action 3 times the card should not accept anymore and deactivate
        for (int i = 0; i < 3; i++)
        {
            // Select the two students to be swapped
            player1.selectColor(player1.getBoard().getStudentsInEntrance().get(i).getColor());
            player1.selectColor(studentsOnCard.get(i).getColor());
            // Apply the card effect
            joker.applyAction();
            // After the action i reset the selected colors
            player1.clearSelections();
        }
        //TODO problema perchè quando viene chiamato deactivate in applyAction
        // exchangeCounter viene riportato a 0 e quindi in isValidAction ritorna true invece che false

        // After the 3 apply action the card should be deactivated
        // assertEquals(false,
        //joker.isValidAction(ExpertGameAction.SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE));
        assertEquals(false, joker.activated);
    }

    @Test
    public void applyActionTest()
    {
        // Backup of contained students before any action
        List<Student> previousStudentPlayer = player1.getBoard().getStudentsInEntrance();

        // Select the first player
        game.selectPlayer(0);

        // So that i don't worry about that
        player1.addCoins(100);

        // Test with not activated card
        player1.selectColor(player1.getBoard().getStudentsInEntrance().get(0).getColor());
        player1.selectColor(studentsOnCard.get(0).getColor());

        // I "apply" the action
        joker.applyAction();

        // Verify that nothing happened
        for (Student student : player1.getBoard().getStudentsInEntrance())
        {
            assertEquals(true, previousStudentPlayer.contains(student));
        }

        // Now i activate the joker but with no selected colors
        player1.clearSelections();

        // Activate the joker
        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        // Nothing selected
        assertThrows(NoSuchElementException.class, () -> joker.applyAction());

        // Select only one color
        player1.selectColor(player1.getBoard().getStudentsInEntrance().get(0).getColor());

        // Only one color selected
        assertThrows(NoSuchElementException.class, () -> joker.applyAction());

        // Clear selections to create a new scenario
        player1.clearSelections();

        // Delete the color RED from the player entrance so that i can choose a non-present color in
        // the entrance
        while (player1.getBoard().removeStudentFromEntrance(SchoolColor.RED).isPresent())
        {
            // Fill the RED removed with a GREEN
            player1.getBoard().addStudentToEntrance(new Student(SchoolColor.GREEN));
        }

        // Select the RED and the first color on the card
        player1.selectColor(SchoolColor.RED);
        player1.selectColor(studentsOnCard.get(0).getColor());

        assertThrows(NoSuchElementException.class, () -> joker.applyAction());

        // Now i select a color that doesn't exist on the card
        // First i fill the card with all the player cards
        // This for proves also the correct functioning of the swap
        for (int i = 0; i < 6; i++)
        {
            player1.clearSelections();
            player1.selectColor(player1.getBoard().getStudentsInEntrance().get(i).getColor());
            player1.selectColor(studentsOnCard.get(i).getColor());

            // Activate the card
            try
            {
                joker.activate();
            } catch (Exception e)
            {
            }

            // Swap
            joker.applyAction();

            // Deactivate
            joker.deactivate();
        }

        // Now i select a color that doesn't exist on the card at the moment
        // Activate the card
        try
        {
            joker.activate();
        } catch (Exception e)
        {
        }

        player1.clearSelections();
        player1.selectColor(player1.getBoard().getStudentsInEntrance().get(0).getColor());
        player1.selectColor(SchoolColor.RED);

        assertThrows(NoSuchElementException.class, () -> joker.applyAction());
    }
}
