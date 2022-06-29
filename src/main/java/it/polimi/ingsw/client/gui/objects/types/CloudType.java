package it.polimi.ingsw.client.gui.objects.types;

import javafx.geometry.Point2D;

public enum CloudType
{
    CLOUD_3("cloud3.png", new Point2D(-0.30, 0.05), new Point2D(0.22, 0.22), new Point2D(0.1, -0.3)), CLOUD_4("cloud4.png", new Point2D(-0.31, 0.071),
            new Point2D(0.158, 0.248), new Point2D(0.317, -0.139), new Point2D(-0.058, -0.316));

    /**
     * Filename
     */
    private String filename;

    /**
     * Array of student positions
     */
    private final Point2D positions[];

    /**
     * Private constructor
     * 
     * @param filename Name of texture file
     * @param points Array of points
     */
    private CloudType(String filename, Point2D... points)
    {
        this.filename = filename;
        this.positions = points.clone();
    }

    /**
     * Getter and setter
     */
    public String getFilename()
    {
        return filename;
    }

    public int getPositionsNumber()
    {
        return positions.length;
    }

    public Point2D getPositionAt(int index)
    {
        if (index < 0 || index > getPositionsNumber())
            return null;
        return positions[index];
    }
}
