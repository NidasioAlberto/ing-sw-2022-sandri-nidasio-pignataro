package it.polimi.ingsw.protocol.updates;

public enum CardPiece
{
    TOP_ROW("╔══════════╗ "),
    MIDDLE_ROW("║          ║ "),
    BOTTOM_ROW("╚══════════╝ ");

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
