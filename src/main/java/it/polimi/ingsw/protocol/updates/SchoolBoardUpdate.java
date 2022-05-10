package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.TowerColor;

public class SchoolBoardUpdate extends ModelUpdate {
    /**
     * The modified school board
     */
    private SchoolBoard board;

    /**
     * The player string name to which the board is assigned
     */
    private String player;

    /**
     * Constructor
     * 
     * @param board  The modified board
     * @param player The player that owns the board
     */
    public SchoolBoardUpdate(SchoolBoard board, String player) {
        if (board == null)
            throw new NullPointerException("[SchoolBoardUpdate] Null board");
        if (player == null)
            throw new NullPointerException("[SchoolBoardUpdate] Null player name");

        this.board = board;
        this.player = player;
    }

    public SchoolBoard getBoard() {
        return board;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public void handleUpdate(Object handler) {

    }

    // TUI

    private String drawTopRow() {
        String rep = "";

        rep += "╔═══╦═" + player;

        for (int i = 0; i < 18 - player.length(); i++)
            rep += "═";

        rep += "╤═╦═══╗\n";

        return rep;
    }

    private String drawColor(SchoolColor color, String content) {
        String rep = "\u001B[";

        switch (color) {
            case BLUE:
                rep += "34";
                break;
            case GREEN:
                rep += "32";
                break;
            case PINK:
                rep += "35";
                break;
            case RED:
                rep += "31";
                break;
            case YELLOW:
                rep += "33";
                break;
        }

        rep += "m" + content;

        // Reset the color
        rep += "\u001B[97m";

        return rep;
    }

    private String drawColor(TowerColor color, String content) {
        String rep = "\u001B[";

        switch (color) {
            case BLACK:
                rep += "30";
                break;
            case GREY:
                rep += "90";
                break;
            case WHITE:
                rep += "97";
                break;
        }

        rep += "m" + content;

        // Reset the color
        rep += "\u001B[97m";

        return rep;
    }

    private String drawStudent(Student student) {
        return drawColor(student.getColor(), "▪");
    }

    private String drawStudent(int index) {
        if (board.getStudentsInEntrance().size() > index)
            return drawStudent(board.getStudentsInEntrance().get(index));
        else
            return " ";
    }

    private String drawDiningRoom(SchoolColor color) {
        String rep = "";

        for (int i = 0; i < board.getStudentsNumber(color); i++) {
            rep += drawStudent(new Student(color));

            if (i < board.getStudentsNumber(color) - 1)
                rep += " ";
        }

        for (int i = 0; i < 10 - board.getStudentsNumber(color); i++)
            rep += "  ";

        return rep;
    }

    public String drawProfessor(SchoolColor color) {
        if (board.hasProfessor(color))
            return drawColor(color, "◉");
        else
            return " ";
    }

    public String drawTower(int index) {
        if (board.getTowers().size() > index)
            return drawColor(board.getTowerColor(), "☗");
        else
            return " ";
    }

    @Override
    public String toString() {
        String rep = "";

        // rep += "\u001B[40m";
        rep += drawTopRow();
        rep += "║" + drawStudent(0) + " " + drawStudent(1) + "║" + drawDiningRoom(SchoolColor.GREEN) + "│"
                + drawProfessor(SchoolColor.GREEN) + "║" + drawTower(0) + " " + drawTower(1) + "║\n";
        rep += "║" + drawStudent(2) + " " + drawStudent(3) + "║" + drawDiningRoom(SchoolColor.RED) + "│"
                + drawProfessor(SchoolColor.RED) + "║" + drawTower(2) + " " + drawTower(3) + "║\n";
        rep += "║" + drawStudent(4) + " " + drawStudent(5) + "║" + drawDiningRoom(SchoolColor.YELLOW) + "│"
                + drawProfessor(SchoolColor.YELLOW) + "║" + drawTower(4) + " " + drawTower(5) + "║\n";
        rep += "║" + drawStudent(6) + " " + drawStudent(7) + "║" + drawDiningRoom(SchoolColor.PINK) + "│"
                + drawProfessor(SchoolColor.PINK) + "║" + drawTower(6) + " " + drawTower(7) + "║\n";
        rep += "║" + drawStudent(8) + "  ║" + drawDiningRoom(SchoolColor.BLUE) + "│" + drawProfessor(SchoolColor.BLUE)
                + "║   ║\n";
        rep += "╚═══╩═══════════════════╧═╩═══╝\n";

        return rep;
    }
}
