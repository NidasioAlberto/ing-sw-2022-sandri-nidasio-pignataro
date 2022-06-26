package it.polimi.ingsw.client.cli.utils;

public enum GamePieces
{
    // Commented pieces work with ConEmu terminal in Windows
    STUDENT("o", "●"), // ●
    PROFESSOR("O", "⬣"), // ⯂
    TOWER("T", "☗"), // ♜
    MOTHER_NATURE("@", "◉"), // ⭗
    NO_ENTRY_TILE("X", "⊠"), // ☒
    COINS_MARKER("$ ", "\uD83D\uDCB0"), // €, 💰
    SLASH("/", "╱"), // Used for islands
    BACKSLASH("\\", "╲"), // Used for islands
    ORIZONTAL_LINE("─", "━"), // Used for character cards
    VERTICAL_LINE("│", "┃"), // Used for character cards
    TOP_LEFT_CORNER("┌", "┏"), // Used for character cards
    TOP_RIGHT_CORNER("┐", "┓"), // Used for character cards
    BOTTOM_LEFT_CORNER("└", "┗"), // Used for character cards
    BOTTOM_RIGHT_CORNER("┘", "┛"), // Used for character cards
    TOP_LEFT_CORNER_CURVE(" ", "╭"), // Used for cloud tiles
    TOP_RIGHT_CORNER_CURVE(" ", "╮"), // Used for cloud tiles
    BOTTOM_LEFT_CORNER_CURVE(" ", "╰"), // Used for cloud tiles
    BOTTOM_RIGHT_CORNER_CURVE(" ", "╯"), // Used for cloud tiles
    ARROW("->", " →"); // Used for assistant cards


    private String pieceWindows;
    private String piece;

    GamePieces(String pieceWindows, String piece)
    {
        this.pieceWindows = pieceWindows;
        this.piece = piece;
    }

    @Override
    public String toString()
    {
        // Display the correct game piece based on the current OS
        if (System.getProperty("os.name").contains("Windows"))
            return pieceWindows;
        else
            return piece;
    }
}
