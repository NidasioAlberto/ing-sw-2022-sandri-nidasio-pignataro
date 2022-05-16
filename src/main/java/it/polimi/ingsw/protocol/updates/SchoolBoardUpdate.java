package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.TowerColor;

public class SchoolBoardUpdate extends ModelUpdate
{
    private SchoolBoard board;

    /**
     * The player string name to which the board is assigned.
     */
    private String player;

    public SchoolBoardUpdate(SchoolBoard board, String player)
    {
        if (board == null)
            throw new NullPointerException("[SchoolBoardUpdate] Null board");
        if (player == null)
            throw new NullPointerException("[SchoolBoardUpdate] Null player name");

        this.board = board;
        this.player = player;
    }

    public SchoolBoard getBoard()
    {
        return board;
    }

    public String getPlayer()
    {
        return player;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }

    // TUI

    private String drawTopRow()
    {
        String rep = "";

        rep += "╔═══╦═" + player;

        for (int i = 0; i < 18 - player.length(); i++)
            rep += "═";

        rep += "╤═╦════╗";

        return rep;
    }

    private String drawBottomRow()
    {
        return "╚═══╩═══════════════════╧═╩════╝";
    }

    private String drawStudent(int index)
    {
        if (board.getStudentsInEntrance().size() > index)
            return board.getStudentsInEntrance().get(index).toString();
        else
            return " ";
    }

    private String drawDiningRoom(SchoolColor color)
    {
        String rep = "";

        for (int i = 0; i < board.getStudentsNumber(color); i++)
        {
            rep += PrintHelper.drawColor(color, GamePieces.STUDENT.toString());

            if (i < board.getStudentsNumber(color) - 1)
                rep += " ";
        }

        for (int i = 1; i < 10 - board.getStudentsNumber(color); i++)
            rep += "  ";
        if (board.getStudentsNumber(color) == 0)
            rep += " ";
        else
            rep += "  ";

        return rep;
    }

    private String drawProfessor(SchoolColor color)
    {
        if (board.hasProfessor(color))
            return PrintHelper.drawColor(color, GamePieces.PROFESSOR.toString());
        else
            return " ";
    }

    public String drawTower(int index)
    {
        if (board.getTowers().size() > index)
            return PrintHelper.drawColor(board.getTowerColor(), GamePieces.TOWER.toString());
        else
            return " ";
    }

    /**
     * Draws a 7x31 representation of the school board.
     */
    @Override
    public String toString()
    {
        String rep = "";

        rep += drawTopRow() + PrintHelper.moveCursorRelative(-1, -32);
        rep += "║" + drawStudent(0) + " " + drawStudent(1) + "║" + drawDiningRoom(SchoolColor.GREEN) + "│" + drawProfessor(SchoolColor.GREEN) + "║"
                + drawTower(0) + "  " + drawTower(1) + "║" + PrintHelper.moveCursorRelative(-1, -32);
        rep += "║" + drawStudent(2) + " " + drawStudent(3) + "║" + drawDiningRoom(SchoolColor.RED) + "│" + drawProfessor(SchoolColor.RED) + "║"
                + drawTower(2) + "  " + drawTower(3) + "║" + PrintHelper.moveCursorRelative(-1, -32);
        rep += "║" + drawStudent(4) + " " + drawStudent(5) + "║" + drawDiningRoom(SchoolColor.YELLOW) + "│" + drawProfessor(SchoolColor.YELLOW) + "║"
                + drawTower(4) + "  " + drawTower(5) + "║" + PrintHelper.moveCursorRelative(-1, -32);
        rep += "║" + drawStudent(6) + " " + drawStudent(7) + "║" + drawDiningRoom(SchoolColor.PINK) + "│" + drawProfessor(SchoolColor.PINK) + "║"
                + drawTower(6) + "  " + drawTower(7) + "║" + PrintHelper.moveCursorRelative(-1, -32);
        rep += "║" + drawStudent(8) + "  ║" + drawDiningRoom(SchoolColor.BLUE) + "│" + drawProfessor(SchoolColor.BLUE) + "║    ║"
                + PrintHelper.moveCursorRelative(-1, -32);
        rep += drawBottomRow();

        // Draw the number of coins if the game is in expert mode
        if (board.getMode() == GameMode.EXPERT)
        {
            rep += PrintHelper.moveCursorRelative(1, -5);
            rep += GamePieces.COINS_MARKER + "" + board.getCoins();
            rep += PrintHelper.moveCursorRelative(-1, (board.getCoins() < 10 ? 3 : 2));
        }

        return rep;
    }

    public static void main(String[] args)
    {
        SchoolBoard board = new SchoolBoard(TowerColor.GREY, GameMode.EXPERT);

        board.setPlayersNumber(2);

        board.addStudentToDiningRoom(new Student(SchoolColor.BLUE));
        board.addStudentToDiningRoom(new Student(SchoolColor.BLUE));
        board.addStudentToDiningRoom(new Student(SchoolColor.YELLOW));
        board.addStudentToDiningRoom(new Student(SchoolColor.GREEN));

        board.addTower(new Tower(TowerColor.GREY));
        board.addTower(new Tower(TowerColor.GREY));
        board.addTower(new Tower(TowerColor.GREY));
        board.addTower(new Tower(TowerColor.GREY));
        board.addTower(new Tower(TowerColor.GREY));

        board.addCoins(10);


        SchoolBoardUpdate update = new SchoolBoardUpdate(board, "test");

        System.out.print(update.toString());
    }
}
