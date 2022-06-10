package it.polimi.ingsw.client.gui.objects;

import java.util.Objects;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.CharacterType;
import it.polimi.ingsw.model.game.CharacterCardType;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class DrawableCharacterCard extends DrawableObject
{
    /**
     * Scale factor between X and Y dimensions
     */
    public static final double SCALE_FACTOR = 1.516;

    /**
     * X Dimension constant
     */
    private final double X_DIMENSION;

    /**
     * Y Dimension constant
     */
    private final double Y_DIMENSION;

    /**
     * The card type
     */
    private final CharacterCardType TYPE;

    /**
     * The actual graphical box
     */
    private final Box box;

    /**
     * Constructor
     * 
     * @param updater
     */
    public DrawableCharacterCard(double x_dimension, CharacterCardType type, AnimationHandler updater)
    {
        super(updater);

        if (x_dimension <= 0)
            throw new IllegalArgumentException("[DrawableCharacterCard] Zero or less X dimension");
        if (type == null)
            throw new NullPointerException("[DrawableCharacterCard] Null card type");

        // Set the constants
        X_DIMENSION = x_dimension;
        Y_DIMENSION = x_dimension * SCALE_FACTOR;
        TYPE = type;

        // Create the box
        box = new Box(X_DIMENSION, Y_DIMENSION, 0);

        // Create the texture
        PhongMaterial material = new PhongMaterial();

        // Extract the image texture from the character type
        material.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(CharacterType.valueOf(TYPE.name()).getFilename()))));

        // Set the material
        box.setMaterial(material);

        // Add the rotations
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCharacterCard] Null group");

        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCharacterCard] Null group");

        group.getChildren().remove(box);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCharacterCard] Null ambient light");

        light.getScope().add(box);
    }

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {}

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCharacterCard] Null ambient light");

        light.getScope().remove(box);
    }

    @Override
    public void enableVisibility()
    {
        box.setMouseTransparent(false);
    }

    @Override
    public void disableVisibility()
    {
        box.setMouseTransparent(true);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableCharacterCard] Null point");

        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableCharacterCard] Null rotation");

        box.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ());
    }
}
