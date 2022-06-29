package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MinstrelTest
{
    Game game;
    CharacterCard minstrel;
    Player player1;
    Player player2;
    SchoolBoard board1;
    SchoolBoard board2;

    @BeforeEach
    public void init()
    {
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
        Match match = new Match(new Server(), "test", 2, GameMode.CLASSIC);
        game.subscribe(match);
        game.setupGame();

        // Now i can instantiate the character card
        minstrel = CharacterCard.createCharacterCard(CharacterCardType.MINSTREL, game);
        minstrel.init();

        // Add to the dining room 3 green students
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));

        // Set the entrance students as yellow will be missing
        List<Student> entrance = player1.getBoard().getStudentsInEntrance();
        for (int i = 0; i < entrance.size(); i++)
            player1.getBoard().removeStudentFromEntrance(entrance.get(i));

        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.RED));
        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.BLUE));
        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.PINK));
        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.BLUE));
        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.PINK));
        player1.getBoard().addStudentToEntrance(new Student(SchoolColor.GREEN));
    }

    @Test
    public void constructorTest()
    {
        // Null pointers inside the factory method
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(null, game));
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(CharacterCardType.MINSTREL, null));

        // Type confirmation
        assertEquals(CharacterCardType.MINSTREL, minstrel.getCardType());

        // Cost
        assertEquals(1, minstrel.cost);

        // Not already used
        assertEquals(false, minstrel.firstUsed);
        assertEquals(false, minstrel.isActivated());
    }

    @Test
    public void generalCardTest()
    {
        // If we don't select a player we expect noSuchElementException
        assertThrows(NoSuchElementException.class, () -> minstrel.activate());

        // Player selection and card activation
        game.selectPlayer(0);

        // player1 has 1 coin because there are 3 green students in dining, so I remove it
        player1.getBoard().removeCoins(1);

        // If i activate the card with no coins i should get an error
        game.getSelectedPlayer().get().getBoard().removeCoins(1);
        assertThrows(NotEnoughCoinsException.class, () -> minstrel.activate());

        // Add the coins to activate the card
        player1.getBoard().addCoins(10);

        try
        {
            minstrel.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 1 coin less
        assertEquals(9, player1.getBoard().getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, minstrel.firstUsed);
        assertEquals(true, minstrel.isActivated());
        // The card should cost now 2 coins
        assertEquals(2, minstrel.cost);

        // If i try to activate the card while already activated nothing should happen
        try
        {
            minstrel.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, minstrel.firstUsed);
        assertEquals(true, minstrel.isActivated());
        assertEquals(2, minstrel.cost);
        assertEquals(9, player1.getBoard().getCoins());

        // Finally if I deactivate the card the cost will be 2 coins
        minstrel.deactivate();

        try
        {
            minstrel.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, minstrel.firstUsed);
        assertEquals(true, minstrel.isActivated());
        assertEquals(2, minstrel.cost);
        assertEquals(7, player1.getBoard().getCoins());
    }

    @Test
    public void isPlayableTest()
    {
        // Should be always playable
        assertEquals(true, minstrel.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        // Player selection
        game.selectPlayer(0);

        // Enable the card
        minstrel.activated = true;

        // Theoretically if i don't perform any action, any number of swap student from character
        // card to entrance should be allowed
        for (int i = 0; i < 100; i++)
            assertEquals(true, minstrel.isValidAction(ExpertGameAction.SWAP_STUDENT_FROM_ENTRANCE_TO_DINING));

        // When i apply the action 2 times the card should be deactivated
        for (int i = 0; i < 2; i++)
        {
            player1.selectColor(player1.getBoard().getStudentsInEntrance().get(0).getColor());
            player1.selectColor(SchoolColor.GREEN);

            // Swap the students
            minstrel.applyAction();

            // Reset the selection
            player1.clearSelections();
        }

        // After the 2 apply action the card should be deactivated
        assertEquals(false, minstrel.activated);

        // I need to activate the card another time
        minstrel.activated = true;

        // When i ask for a normal action, the action is not valid
        assertEquals(false, minstrel.isValidAction(ExpertGameAction.BASE_ACTION));
        assertEquals(true, minstrel.isActivated());
    }

    @Test
    public void applyActionTest()
    {
        // Activating the card without a selected player should throw a NoSuchElement
        minstrel.activated = true;
        assertThrows(NoSuchElementException.class, () -> minstrel.applyAction());

        // Selecting the player
        game.selectPlayer(0);

        // I expect some errors about missing color selection
        assertThrows(NoSuchElementException.class, () -> minstrel.applyAction());
        // Even if i select one color
        player1.selectColor(player1.getBoard().getStudentsInEntrance().get(0).getColor());
        assertThrows(NoSuchElementException.class, () -> minstrel.applyAction());

        // Now i select another color but not present in dining
        player1.selectColor(SchoolColor.RED);

        assertThrows(NoSuchElementException.class, () -> minstrel.applyAction());

        // Now i select a color but not present in entrance
        player1.clearSelections();
        player1.selectColor(SchoolColor.YELLOW);
        player1.selectColor(SchoolColor.GREEN);

        // I expect an error
        assertThrows(NoSuchElementException.class, () -> minstrel.applyAction());

        // And now the correct case
        player1.clearSelections();
        player1.selectColor(SchoolColor.RED);
        player1.selectColor(SchoolColor.GREEN);

        minstrel.applyAction();

        // I verify that the red increased by 1
        assertEquals(1, player1.getBoard().getStudentsNumber(SchoolColor.RED));
        // I verify that the greens are 2
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));
        // I verify that there is no red student in entrance
        assertEquals(true, player1.getBoard().getStudentsInEntrance().stream().filter(p -> p.getColor() == SchoolColor.RED).findFirst().isEmpty());
    }
}
