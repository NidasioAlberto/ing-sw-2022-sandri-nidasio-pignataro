package it.polimi.ingsw.client.cli.utils;

import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.TowerColor;

public class PrintHelper
{
    static public String ERASE_FROM_CURSOR_TILL_END_OF_SCREEN = "\u001B[0J";

    static public String ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN = "\u001B[1J";

    static public String ERASE_ENTIRE_SCREEN = "\u001B[2J";

    static public String ERASE_FROM_CURSOR_TILL_END_OF_LINE = "\u001B[0K";

    static public String ERASE_ENTIRE_LINE = "\u001B[2K";

    static public String drawColor(SchoolColor color, String content)
    {
        String rep = "\u001B[";

        switch (color)
        {
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

    static public String drawColor(TowerColor color, String content)
    {
        String rep = "\u001B[38;5;";

        switch (color)
        {
            case BLACK:
                rep += "235";
                break;
            case GREY:
                rep += "248";
                break;
            case WHITE:
                rep += "255";
                break;
        }

        rep += "m" + content;

        // Reset the color
        rep += "\u001B[97m";

        return rep;
    }

    static public String drawStudentsNumber(int count, SchoolColor color)
    {
        if (count == 0)
            return "  ";
        else
            return count + drawColor(color, GamePieces.STUDENT.toString());
    }

    /**
     * Moves the cursor relative to the current position.
     * 
     * @param up Up positions where to move, can be negative to go down.
     * @param right Right position where to move, can be negative to go left.
     */
    static public String moveCursorRelative(int up, int right)
    {
        String movement = "";

        if (up > 0)
            movement += "\u001B[" + up + "A";
        else if (up < 0)
            movement += "\u001B[" + (-up) + "B";

        if (right > 0)
            movement += "\u001B[" + right + "C";
        else if (right < 0)
            movement += "\u001B[" + (-right) + "D";

        return movement;
    }

    static public String moveCursorAbsolute(int row, int column)
    {
        String movement = "";

        movement += "\u001B[" + row + ";" + column + "H";

        return movement;
    }

    static public String moveToBeginningOfLine(int up)
    {
        if (up > 0)
            return "\u001B[" + up + "F";
        else if (up < 0)
            return "\u001B[" + (-up) + "E";
        else
            return "";
    }

    static public synchronized void print(String msg)
    {
        System.out.print(msg);
    }

    static public void printM(int row, int column, String msg)
    {
        String toPrint = "";

        // Move the cursor
        toPrint += moveCursorAbsolute(row, column);

        // Print the message
        toPrint += msg;

        print(toPrint);
    }

    static public void printMR(int row, int column, String msg)
    {
        String toPrint = "";

        // Save the cursor position
        toPrint += "\u001B[s";

        // Move the cursor
        toPrint += moveCursorAbsolute(row, column);

        // Print the message
        toPrint += msg;

        // Restore cursor position
        toPrint += "\u001B[u";

        print(toPrint);
    }

    static public void printMessage(String error)
    {
        printMR(24, 2, moveCursorRelative(-1, 0) + ERASE_ENTIRE_LINE + moveCursorRelative(1, 0) + ERASE_ENTIRE_LINE + error);
    }
}
