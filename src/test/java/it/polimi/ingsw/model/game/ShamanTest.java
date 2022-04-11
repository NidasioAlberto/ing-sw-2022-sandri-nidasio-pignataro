package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Class to test the Shaman class
 */
public class ShamanTest
{
    // Setup
    CharacterCard shaman;
    Game game;
    Player player1 = new Player("Player1", TowerColor.WHITE);
    Player player2 = new Player("Player2", TowerColor.BLACK);

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
        shaman = CharacterCard.createCharacterCard(CharacterCardType.SHAMAN, game);
    }

    @Test
    public void ConstructorTest()
    {
        // Decorate a null game
        assertThrows(NullPointerException.class, () -> new Shaman(null));

        // Check the card type
        assertEquals(CharacterCardType.SHAMAN, shaman.getCardType());

        // Check the initial cost
        assertEquals(2, shaman.cost);

        // At the beginning the card is not active
        assertFalse(shaman.activated);

        // At the beginning the card isn't used
        assertFalse(shaman.firstUsed);
    }

    @Test
    public void isPlayableTest()
    {
        // This card is playable only before the movement of mother nature
        // At the beginning mother nature hasn't moved
        assertFalse(game.motherNatureMoved);
        // So the card is playable
        assertTrue(shaman.isPlayable());

        // Imagine mother nature moves
        game.motherNatureMoved = true;
        // So the card isn't playable
        assertFalse(shaman.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        // The card is not active so the isValidAction return false
        for (GameAction action: GameAction.values())
        {
            assertFalse(shaman.isValidAction(action));
        }

        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> shaman.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoinsException.class, () -> shaman.activate());

        try
        {
            // Activate the card
            player1.addCoins(2);
            shaman.activate();
            assertEquals(0, player1.getCoins());

            // The card doesn't intercept any action, so if active it returns true
            for (GameAction action: GameAction.values())
            {
                assertTrue(shaman.isValidAction(action));
            }
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card has been used the first time so its cost increases
        assertTrue(shaman.firstUsed);
        assertEquals(3, shaman.cost);
        assertTrue(shaman.activated);

        // Deactivate the card
        shaman.deactivate();
        assertFalse(shaman.activated);

        try
        {
            // Activate the card
            player1.addCoins(3);
            shaman.activate();
            assertEquals(0, player1.getCoins());
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // The card was already used so the cost doesn't increase
        assertTrue(shaman.firstUsed);
        assertEquals(3, shaman.cost);
        assertTrue(shaman.activated);
    }

    @Test
    public void conquerProfessorsTest()
    {
        // Select a player
        game.selectPlayer(0);
        player1.addCoins(2);

        // Player2 has the green professor and 2 green students
        player2.getBoard().addProfessor(new Professor(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player2.getBoard().getProfessors().get(0).getColor());
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // Player1 has 2 green students too
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(0, player1.getBoard().getProfessors().size());
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // The card isn't active so if I apply the action, nothing changes
        shaman.applyAction();
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player2.getBoard().getProfessors().get(0).getColor());
        assertEquals(0, player1.getBoard().getProfessors().size());

        try
        {
            // Activate the card
            shaman.activate();
            assertEquals(0, player1.getCoins());
            assertTrue(shaman.activated);
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // TODO conquerprofessor da sistemare
        /*
        // The card is active so if I apply the action player gains the green professor
        shaman.applyAction();
        assertEquals(0, player2.getBoard().getProfessors().size());
        assertEquals(1, player1.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player1.getBoard().getProfessors().get(0).getColor());
        */
    }
}