package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.TowerColor;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class KnightTest
{
    Game game;
    CharacterCard knight;
    Player player1;
    Player player2;
    SchoolBoard board1;
    SchoolBoard board2;

    @BeforeEach
    public void init()
    {
        // I have to initialize a Game, a Player and a School Board to ensure
        // the character card can behave correctly
        board1 = new SchoolBoard(TowerColor.BLACK);
        board2 = new SchoolBoard(TowerColor.WHITE);
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

        // Now i can instanciate the character card
        knight = CharacterCard.createCharacterCard(CharacterCardType.JOKER, game);
    }
}
