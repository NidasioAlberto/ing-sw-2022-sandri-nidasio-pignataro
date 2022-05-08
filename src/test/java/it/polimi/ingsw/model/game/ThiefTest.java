package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ThiefTest
{
    Game game;
    CharacterCard thief;
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
        try {
            game.addPlayer(player1);
            game.addPlayer(player2);
        } catch (Exception e) {
        }

        // Setup the game
        game.setupGame();

        // Now i can instantiate the character card
        thief = CharacterCard.createCharacterCard(CharacterCardType.THIEF, game);
    }

    @Test
    public void constructorTest()
    {
        //Null pointers inside the factory method
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(null, game));
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(CharacterCardType.THIEF, null));

        //Type confirmation
        assertEquals(CharacterCardType.THIEF, thief.getCardType());

        //Cost
        assertEquals(3, thief.cost);

        //Not already used
        assertEquals(false, thief.firstUsed);
        assertEquals(false, thief.isActivated());
    }

    @Test
    public void generalCardTest()
    {
        // If we don't select a player we expect noSuchElementException
        assertThrows(NoSuchElementException.class, () -> thief.activate());

        // Player selection and card activation
        game.selectPlayer(0);

        // If i activate the card with no coins i should get an error
        assertThrows(NotEnoughCoinsException.class, () -> thief.activate());

        // Add the coins to activate the card
        player1.getBoard().addCoins(10);

        try
        {
            thief.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 3 coin less
        assertEquals(7, player1.getBoard().getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, thief.firstUsed);
        assertEquals(true, thief.isActivated());
        // The card should cost now 4 coins
        assertEquals(4, thief.cost);

        // If i try to activate the card while already activated nothing should happen
        try
        {
            thief.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, thief.firstUsed);
        assertEquals(true, thief.isActivated());
        assertEquals(4, thief.cost);
        assertEquals(7, player1.getBoard().getCoins());

        // Finally if I deactivate the card the cost will be 3 coins
        thief.deactivate();

        try
        {
            thief.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, thief.firstUsed);
        assertEquals(true, thief.isActivated());
        assertEquals(4, thief.cost);
        assertEquals(3, player1.getBoard().getCoins());
    }

    @Test
    public void isPlayableTest()
    {
        //This card should always be playable
        assertEquals(true, thief.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        //The only valid action is the select color action
        for(ExpertGameAction action : ExpertGameAction.values())
        {
            if(action == ExpertGameAction.SELECT_COLOR)
                assertEquals(true, thief.isValidAction(action));
            else
                assertEquals(false, thief.isValidAction(action));
        }
    }

    @Test
    public void applyActionTest()
    {
        //Set up the environment
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.BLUE));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.RED));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.RED));

        //Call the apply action without having selected a player
        thief.activated = true;
        assertThrows(NoSuchElementException.class, () -> thief.applyAction());
        assertEquals(true, thief.activated);
        //Check that nothing changed
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(1, player1.getBoard().getStudentsNumber(SchoolColor.BLUE));
        assertEquals(4, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.RED));

        //Select the player but not the color
        game.selectPlayer(0);
        assertThrows(NoSuchElementException.class, () -> thief.applyAction());
        assertEquals(true, thief.activated);
        //Check that nothing changed
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(1, player1.getBoard().getStudentsNumber(SchoolColor.BLUE));
        assertEquals(4, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.RED));

        //Select a non present color
        player1.selectColor(SchoolColor.PINK);
        thief.applyAction();
        assertEquals(false, thief.activated);
        //Check that nothing changed
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(1, player1.getBoard().getStudentsNumber(SchoolColor.BLUE));
        assertEquals(4, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.RED));

        //Select an existing color
        player1.clearSelections();
        player1.selectColor(SchoolColor.GREEN);
        thief.activated = true;
        thief.applyAction();
        assertEquals(false, thief.activated);
        //Check that something changed
        assertEquals(0, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(1, player1.getBoard().getStudentsNumber(SchoolColor.BLUE));
        assertEquals(1, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.RED));
    }
}
