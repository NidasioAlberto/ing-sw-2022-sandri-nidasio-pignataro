package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.WizardType;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class DrawableAssistantCard extends DrawableObject
{
    // Scale factor between X and Y dimensions
    public static final double SCALE_FACTOR = 1.468;

    /**
     * X Dimension constant
     */
    private final double X_DIMENSION;

    /**
     * Y Dimension constant
     */
    private final double Y_DIMENSION;

    /**
     * The wizard type
     */
    private final WizardType TYPE;

    /**
     * The two boxes (for front and back)
     */
    private final Box frontBox;
    private final Box backBox;

    /**
     * The card position used for animation purposes
     */
    private Point3D position;

    /**
     * Constructor
     * @param updater
     */
    public DrawableAssistantCard(double x_dimension, int number, WizardType type, AnimationHandler updater)
    {
        super(updater);

        if(x_dimension <= 0)
            throw new IllegalArgumentException("[DrawableAssistantCard] Zero or less X dimension");
        if(number <= 0 || number > 10)
            throw new IllegalArgumentException("[DrawableAssistantCard] Invalid card number");
        if(type == null)
            throw new NullPointerException("[DrawableAssistantCard] Null wizard type");

        // Set the constants
        X_DIMENSION = x_dimension;
        Y_DIMENSION = X_DIMENSION * SCALE_FACTOR;
        TYPE        = type;

        // Set the card position
        position = new Point3D(0, 0, 0);

        // Create the boxes
        frontBox = new Box(X_DIMENSION, Y_DIMENSION, 0);
        backBox = new Box(X_DIMENSION, Y_DIMENSION, 0);

        // Create the textures
        PhongMaterial frontMaterial = new PhongMaterial();
        PhongMaterial backMaterial = new PhongMaterial();

        frontMaterial.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        "assistant" + number + ".png"))));

        backMaterial.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        TYPE.getFilename()))));

        // Set the materials
        frontBox.setMaterial(frontMaterial);
        backBox.setMaterial(backMaterial);

        // Add the correct transforms
        frontBox.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        frontBox.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));
        backBox.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        backBox.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));
        backBox.getTransforms().add(new Rotate(180, new Point3D(0, 1, 0)));

        // The default card is set not flipped
        setFlipped(false);

        // If not null subscribe to the updater
        if(updater != null)
            updater.subscribeObject(this);

        // Animation settings
        frontBox.setOnMouseEntered((event) -> {
            if(positions.size() == 0)
            {
                // Save the current position before translating
                position = new Point3D(frontBox.getTranslateX(), frontBox.getTranslateY(), frontBox.getTranslateZ());
                this.addAnimationPosition(new Point3D(position.getX(), position.getY(), position.getZ() + X_DIMENSION * SCALE_FACTOR / 2), 5);
            }
        });

        frontBox.setOnMouseExited((event -> {
            this.addAnimationPosition(new Point3D(position.getX(), position.getY(), position.getZ()), 5);
        }));
    }

    /**
     * This method sets if the card is flipped or not
     * @param flipped The status of the wanted card
     */
    public void setFlipped(boolean flipped)
    {
        if(flipped)
            backBox.setLayoutY(-2);
        else
            backBox.setLayoutY(2);
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableAssistantCard] Null group");

        // If all goes correct i assign the objects to the group
        group.getChildren().add(frontBox);
        group.getChildren().add(backBox);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableAssistantCard] Null group");

        // If all goes correct i remove the objects from the group
        group.getChildren().remove(frontBox);
        group.getChildren().remove(backBox);
    }

    // This method does nothing, i want only to subscribe to ambient light
    @Override
    public void subscribeToPointLight(PointLight light) {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableAssistantCard] Null light");

        // if all is correct i add the objects to the scope
        light.getScope().add(frontBox);
        light.getScope().add(backBox);
    }

    @Override
    public void enableVisibility() {

    }

    @Override
    public void disableVisibility() {

    }

    @Override
    public void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableAssistantCard] Null 3D point");

        // If the point is not null i translate the two boxes
        frontBox.translateXProperty().set(point.getX());
        frontBox.translateYProperty().set(point.getY());
        frontBox.translateZProperty().set(point.getZ());

        backBox.translateXProperty().set(point.getX());
        backBox.translateYProperty().set(point.getY());
        backBox.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if(rotation == null)
            throw new NullPointerException("[DrawableAssistantCard] Null rotation");

        // If the rotation is not null i rotate the two boxes
        frontBox.getTransforms().add(rotation);
        backBox.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(frontBox.getTranslateX(), frontBox.getTranslateY(), frontBox.getTranslateZ());
    }
}
