package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.SchoolColor;

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

        rep += "╤═╦═══╗\n";

        return rep;
    }

    private String drawBottomRow()
    {
        return "╚═══╩═══════════════════╧═╩═══╝";
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

        for (int i = 0; i < 10 - board.getStudentsNumber(color); i++)
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

        rep += drawTopRow();
        rep += "║" + drawStudent(0) + " " + drawStudent(1) + "║" + drawDiningRoom(SchoolColor.GREEN) + "│" + drawProfessor(SchoolColor.GREEN) + "║"
                + drawTower(0) + " " + drawTower(1) + "║\n";
        rep += "║" + drawStudent(2) + " " + drawStudent(3) + "║" + drawDiningRoom(SchoolColor.RED) + "│" + drawProfessor(SchoolColor.RED) + "║"
                + drawTower(2) + " " + drawTower(3) + "║\n";
        rep += "║" + drawStudent(4) + " " + drawStudent(5) + "║" + drawDiningRoom(SchoolColor.YELLOW) + "│" + drawProfessor(SchoolColor.YELLOW) + "║"
                + drawTower(4) + " " + drawTower(5) + "║\n";
        rep += "║" + drawStudent(6) + " " + drawStudent(7) + "║" + drawDiningRoom(SchoolColor.PINK) + "│" + drawProfessor(SchoolColor.PINK) + "║"
                + drawTower(6) + " " + drawTower(7) + "║\n";
        rep += "║" + drawStudent(8) + "  ║" + drawDiningRoom(SchoolColor.BLUE) + "│" + drawProfessor(SchoolColor.BLUE) + "║   ║\n";
        rep += drawBottomRow();

        return rep;
    }
}
