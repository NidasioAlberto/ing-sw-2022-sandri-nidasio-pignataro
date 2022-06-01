package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.Server;
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
        Match match = new Match(new Server(), "test", 2, GameMode.CLASSIC);
        game.subscribe(match);
        game.setupGame();
        shaman = CharacterCard.createCharacterCard(CharacterCardType.SHAMAN, game);
        shaman.init();
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
        // A player must be selected to activate the card
        assertThrows(NoSuchElementException.class, () -> shaman.activate());

        // Select a player
        game.selectPlayer(0);

        // If the player doesn't have enough coins an exception is thrown
        assertThrows(NotEnoughCoinsException.class, () -> shaman.activate());

        try
        {
            // Activate the card
            player1.getBoard().addCoins(2);
            shaman.activate();
            assertEquals(1, player1.getBoard().getCoins());

            // The card doesn't intercept any action, so it accepts only ACTION_BASE
            for (ExpertGameAction action: ExpertGameAction.values())
            {
                if (action == ExpertGameAction.BASE_ACTION)
                    assertTrue(shaman.isValidAction(action));
                else assertFalse(shaman.isValidAction(action));
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
            player1.getBoard().addCoins(3);
            shaman.activate();
            assertEquals(1, player1.getBoard().getCoins());
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
    public void applyActionTest()
    {
        try
        {
            // Activate the card
            game.selectPlayer(0);
            player1.getBoard().addCoins(2);
            shaman.activate();
            assertEquals(1, player1.getBoard().getCoins());
            assertTrue(shaman.activated);
        }
        catch (NotEnoughCoinsException e)
        {
            e.printStackTrace();
        }

        // Mother nature hasn't moved so the card remains active
        game.motherNatureMoved = false;
        shaman.applyAction();
        assertTrue(shaman.activated);

        // Mother nature has moved so the card deactivates itself
        game.motherNatureMoved = true;
        shaman.applyAction();
        assertFalse(shaman.activated);
    }

    @Test
    public void conquerProfessorsTest()
    {
        // Select a player
        game.selectPlayer(0);
        player1.getBoard().addCoins(2);

        // Player2 has 2 green students
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // The card isn't active
        shaman.conquerProfessors();
        // The player2 gets the green professor
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player2.getBoard().getProfessors().get(0).getColor());

        // Player1 has 2 green students too
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(0, player1.getBoard().getProfessors().size());
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // The card isn't active so if I apply the action, nothing changes
        shaman.conquerProfessors();
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player2.getBoard().getProfessors().get(0).getColor());
        assertEquals(0, player1.getBoard().getProfessors().size());

        try {
            // Activate the card
            shaman.activate();
            assertEquals(1, player1.getBoard().getCoins());
            assertTrue(shaman.activated);
        } catch (NotEnoughCoinsException e) {
            e.printStackTrace();
        }

        // Player1 adds a red student
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.RED));
        assertEquals(0, player1.getBoard().getProfessors().size());
        assertEquals(1, player1.getBoard().getStudentsNumber(SchoolColor.RED));

        // The card is active so if I apply the action player gets the green and red professors
        shaman.conquerProfessors();
        assertEquals(0, player2.getBoard().getProfessors().size());
        assertEquals(2, player1.getBoard().getProfessors().size());

        // Deactivate the card
        shaman.deactivate();
        assertFalse(shaman.activated);

        // Select Player2
        game.selectPlayer(1);
        player2.getBoard().addCoins(3);

        try {
            // Activate the card
            shaman.activate();
            assertEquals(1, player2.getBoard().getCoins());
            assertTrue(shaman.activated);
        } catch (NotEnoughCoinsException e) {
            e.printStackTrace();
        }

        // Player2 gets back the green professor
        shaman.conquerProfessors();
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player2.getBoard().getProfessors().get(0).getColor());
        assertEquals(1, player1.getBoard().getProfessors().size());
        assertEquals(SchoolColor.RED, player1.getBoard().getProfessors().get(0).getColor());

        // Deactivate the card
        shaman.deactivate();
        assertFalse(shaman.activated);

        // Select Player1
        game.selectPlayer(1);
        player2.getBoard().addCoins(3);

        try {
            // Activate the card
            shaman.activate();
            assertEquals(1, player1.getBoard().getCoins());
            assertTrue(shaman.activated);
        } catch (NotEnoughCoinsException e) {
            e.printStackTrace();
        }

        // Player1 adds a green student
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(3, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // Player1 gets back the green professor
        shaman.conquerProfessors();
        assertEquals(0, player2.getBoard().getProfessors().size());
        assertEquals(2, player1.getBoard().getProfessors().size());
    }

    @Test
    public void conquerProfessorsTest2()
    {
        // Player2 has 2 green students
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(2, player2.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // The card isn't active
        shaman.conquerProfessors();
        // The player2 gets the green professor
        assertEquals(1, player2.getBoard().getProfessors().size());
        assertEquals(SchoolColor.GREEN, player2.getBoard().getProfessors().get(0).getColor());

        // Player1 has 2 green students too
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        assertEquals(0, player1.getBoard().getProfessors().size());
        assertEquals(2, player1.getBoard().getStudentsNumber(SchoolColor.GREEN));

        // An exception is thrown if I call the method without a selected player
        shaman.activated = true;
        NoSuchElementException e1 = assertThrows(NoSuchElementException.class, () -> shaman.conquerProfessors());
        assertEquals("[Shaman] No player selected", e1.getMessage());

        shaman.activated = false;
    }

    @Test
    public void putStudentToDiningTest()
    {
        // Now there is still not a selected player
        assertThrows(NoSuchElementException.class,
                () -> game.putStudentToDining(new Student(SchoolColor.GREEN)));

        // Select a player
        shaman.selectPlayer(1);

        // Put accurately a student to the dining
        shaman.putStudentToDining(new Student(SchoolColor.BLUE));
        assertEquals(1, player2.getBoard().getStudentsNumber(SchoolColor.BLUE));
    }
}