package it.polimi.ingsw.client.cli.utils;

public enum GamePieces
{
    //Original ● ⬣ ☗ ◉ ⊠ 💰
    STUDENT("●", "●"), PROFESSOR("⯂", "⬣"), TOWER("♜", "☗"), MOTHER_NATURE("⭗", "◉"), NO_ENTRY_TILE("⮽", "⊠"), COINS_MARKER("€", "\uD83D\uDCB0");

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
        else return piece;
    }
}
