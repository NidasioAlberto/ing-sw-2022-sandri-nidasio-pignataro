package it.polimi.ingsw.protocol.updates;

public enum CardPiece
{
    //⮊ ⬢ ⬣ ⬡ ⚭ 💰 ⏣ ⎔ ⚭ ⌬ ◯
    TOP_ROW("╔══════════╗"),
    MIDDLE_ROW("║          ║"),
    MIDDLE_ROW_WITH_CIRCLE("║ ◯        ║"),
    MIDDLE_ROW_WITH_COINS("║ ⚭        ║"),
    BOTTOM_ROW("╚══════════╝");

    private String draw;

    private CardPiece(String draw)
    {
        this.draw = draw;
    }

    public String toString()
    {
        return draw;
    }
}
