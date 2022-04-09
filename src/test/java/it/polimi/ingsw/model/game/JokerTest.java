package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoins;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    // Number of students before the character card decorator
    int originalBagStudents;

    @BeforeEach
    public void init()
    {
        //I have to initialize a Game, a Player and a School Board to ensure
        //the character card can behave correctly
        board1 = new SchoolBoard(TowerColor.BLACK);
        board2 = new SchoolBoard(TowerColor.WHITE);
        player1 = new Player("pippo", board1);
        player2 = new Player("peppo", board2);
        game = new Game();

        //Add the player to the game
        try
        {
            game.addPlayer(player1);
            game.addPlayer(player2);
        }
        catch(Exception e){}

        //Select the player
        game.selectPlayer(0);

        //Setup the game
        game.setupGame();

        originalBagStudents = game.getStudentBag().size();

        //Now i can instanciate the character card
        joker = CharacterCard.createCharacterCard(CharacterCardType.JOKER, game);
    }

    @Test
    public void constructorTest()
    {
        //Null decorated instance
        assertThrows(NullPointerException.class, () -> new Joker(null));

        //I need to verify if 6 students have been subtracted from game
        assertEquals(originalBagStudents - 6, game.getStudentBag().size());

        //Verify the cost
        assertEquals(1, joker.cost);
    }

    @Test
    public void generalCardTest()
    {
        //Check the type
        assertEquals(CharacterCardType.JOKER, joker.getCardType());

        //If i activate the card with no coins i should get an error
        assertThrows(NotEnoughCoins.class, () -> joker.activate());

        //Add the coins to activate the card
        player1.addCoins(10);

        try { joker.activate(); }
        catch(Exception e) {}

        //After activation the player should have 1 coin less
        assertEquals(9, player1.getCoins());

        //The card should have the activation flag to true and also the first used flag
        assertEquals(true, joker.firstUsed);
        assertEquals(true, joker.activated);
        //The card should cost now 2 coins
        assertEquals(2, joker.cost);

        //If i try to activate the card while already activated nothing should happen
        try { joker.activate(); }
        catch(Exception e){}

        assertEquals(true, joker.firstUsed);
        assertEquals(true, joker.activated);
        assertEquals(2, joker.cost);
        assertEquals(9, player1.getCoins());

        //Finally if I deactivate the card the cost will be 2 coins
        joker.deactivate();

        try { joker.activate();}
        catch(Exception e){}

        assertEquals(true, joker.firstUsed);
        assertEquals(true, joker.activated);
        assertEquals(2, joker.cost);
        assertEquals(7, player1.getCoins());
    }

    @Test
    public void isPlayableTest()
    {
        //This card should be always playable
        assertEquals(true, joker.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        //Check if null is rejected
        assertThrows(NullPointerException.class, () -> joker.isValidAction(null));

        //If i don't activate the card isValidAction should always be false
        assertEquals(false, joker.isValidAction(GameAction.SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE));

        //So that i don't have any problem with the card activation
        player1.addCoins(10);

        //Enable the card
        try { joker.activate(); }
        catch(Exception e){}

        //Theoretically if i don't perform any action, any number of swap student from character
        //card to entrance should be allowed
        for(int i = 0; i < 100; i++)
            assertEquals(true, joker.isValidAction(GameAction.SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE));
    }
}
