package it.polimi.ingsw.client.gui.objects;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class DrawableSchoolBoard extends DrawableObject
{
    // Scaling factor between x and y dimensions to maintain the proportions
    public static final float SCALE_FACTOR = 2.30f;

    /**
     * X Dimension constant
     */
    private final float X_DIMENSION;

    /**
     * Y Dimension constant
     */
    private final float Y_DIMENSION;

    /**
     * Box containing the SchoolBoard texture
     */
    private final Box box;

    /**
     * Constructor
     * @param x_dimension The x dimension of the board
     * @param y_dimension The y dimension of the board
     */
    public DrawableSchoolBoard(float x_dimension, float y_dimension)
    {
        if(x_dimension < 0)
            throw new IllegalArgumentException("[DrawableSchoolBoard] X dimension less than 0");
        if(y_dimension < 0)
            throw new IllegalArgumentException("[DrawableSchoolBoard] Y dimension less than 0");

        // Assign the parameters
        X_DIMENSION = x_dimension;
        Y_DIMENSION = y_dimension;

        // Create the box
        this.box = new Box(x_dimension, y_dimension, 0);

        // Create the correct texture
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("schoolboard.png"))));

        // Assign the texture
        box.setMaterial(material);

        // Rotate the box correctly
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null group");

        // Add the box to the group
        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null group");

        // Remove the box from the group
        group.getChildren().remove(box);
    }

    // This method does nothing because i don't want reflections
    @Override
    public void subscribeToPointLight(PointLight light) {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableSchoolBoard] Null ambient light");

        // Add the box to the light
        light.getScope().add(box);
    }

    /**
     * Position setters need to be synchronized
     */
    @Override
    public synchronized void translate(Point3D point)
    {
        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());
    }

    @Override
    public Point3D getPosition() { return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ()); }
}
