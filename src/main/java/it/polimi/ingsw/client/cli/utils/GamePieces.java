package it.polimi.ingsw.client.cli.utils;

public enum GamePieces
{
    //Original ● ⬣ ☗ ◉ ⊠ 💰
    STUDENT("●"), PROFESSOR("⯂"), TOWER("♜"), MOTHER_NATURE("⭗"), NO_ENTRY_TILE("⮽"), COINS_MARKER("€");

    private String piece;

    GamePieces(String piece)
    {
        this.piece = piece;
    }

    @Override
    public String toString()
    {
        return piece;
    }
}
