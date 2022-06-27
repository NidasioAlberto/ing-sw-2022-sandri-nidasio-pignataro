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

    /**
     * Returns the given string with the color specified.
     */
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

    /**
     * Returns the given string with the color specified.
     */
    static public String drawColor(TowerColor color, String content)
    {
        String rep = "\u001B[38;5;";

        switch (color)
        {
            case BLACK:
                rep += "238";
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

    /**
     * Returns the student number nicely formatted.
     */
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

    /**
     * Move the cursor to an absolute position.
     */
    static public String moveCursorAbsolute(int row, int column)
    {
        String movement = "";

        movement += "\u001B[" + row + ";" + column + "H";

        return movement;
    }

    /**
     * Moves the cursor to the beginning of the specified line moving relatively.
     */
    static public String moveToBeginningOfLine(int up)
    {
        if (up > 0)
            return "\u001B[" + up + "F";
        else if (up < 0)
            return "\u001B[" + (-up) + "E";
        else
            return "";
    }

    /**
     * Saves the cursor current position.
     */
    static public String savePosition()
    {
        return "\u001B7";
    }

    /**
     * Restores the last saved current position.
     */
    static public String restorePosition()
    {
        return "\u001B8";
    }

    static public synchronized void print(String msg)
    {
        System.out.print(msg);
    }

    /**
     * Moves to the given absolute position and prints the message.
     */
    static public void printAbsolute(int row, int column, String msg)
    {
        String toPrint = "";

        // Move the cursor
        toPrint += moveCursorAbsolute(row, column);

        // Print the message
        toPrint += msg;

        print(toPrint);
    }

    /**
     * Moves to the given absolute position, prints the message and then resets the cursor position.
     */
    static public void printAbsoluteAndReset(int row, int column, String msg)
    {
        String toPrint = "";

        // Save the cursor position
        toPrint += savePosition();

        // Move the cursor
        toPrint += moveCursorAbsolute(row, column);

        // Print the message
        toPrint += msg;

        // Restore cursor position
        toPrint += restorePosition();

        print(toPrint);
    }

    /**
     * Moves to the given relative position and prints the message.
     */
    static public void printRelative(int row, int column, String msg)
    {
        String toPrint = "";

        // Move the cursor
        toPrint += moveCursorRelative(row, column);

        // Print the message
        toPrint += msg;

        print(toPrint);
    }

    /**
     * Moves to the given relative position, prints the message and then resets the cursor position.
     */
    static public void printRelativeAndReset(int row, int column, String msg)
    {
        String toPrint = "";

        // Save the cursor position
        toPrint += savePosition();

        // Move the cursor
        toPrint += moveCursorRelative(row, column);

        // Print the message
        toPrint += msg;

        // Restore cursor position
        toPrint += restorePosition();

        print(toPrint);
    }

    static public void printMessage(String message)
    {
        printAbsoluteAndReset(24, 2, moveCursorRelative(-1, 0) + ERASE_ENTIRE_LINE + moveCursorRelative(1, 0) + ERASE_ENTIRE_LINE + message);
    }
}
