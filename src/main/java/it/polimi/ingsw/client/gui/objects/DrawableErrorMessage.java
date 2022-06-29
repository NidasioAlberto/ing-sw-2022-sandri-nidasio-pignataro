package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class DrawableErrorMessage extends DrawableObject
{
    /**
     * The camera angle
     */
    private final double CAMERA_ANGLE;

    /**
     * The box x dimension
     */
    private final double X_DIMENSION;

    /**
     * The text area and its box
     */
    private Text text;
    private BorderPane pane;

    /**
     * Constructor
     * 
     * @param cameraAngle The camera angle to which paralelize the text
     * @param x_dimension The box X dimension before creating a new line
     * @param updater Updater object
     */
    public DrawableErrorMessage(double cameraAngle, double x_dimension, AnimationHandler updater)
    {
        super(updater);

        if (x_dimension <= 0)
            throw new IllegalArgumentException("[DrawableErrorMessage] Invalid x dimension");

        // Set the parameters
        CAMERA_ANGLE = cameraAngle;
        X_DIMENSION = x_dimension;

        // Create the text and box
        text = new Text();
        pane = new BorderPane();

        // Set the text limits
        text.setWrappingWidth(X_DIMENSION);
        text.setCache(true);
        text.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        text.setFont(Font.font("Verdana", 40));
        // text.setStyle("-fx-text-inner-color: white;");
        text.setFill(Color.WHITE);

        // Fill the pane with the text
        pane.setTop(text);
        pane.getTransforms().add(new Rotate(-CAMERA_ANGLE, new Point3D(1, 0, 0)));
        pane.setCache(true);
        pane.setCacheHint(CacheHint.SCALE_AND_ROTATE);

        // Scale the box to obtain more resolution
        pane.setScaleX(0.25);
        pane.setScaleY(0.25);
        pane.setScaleZ(0.25);
        BorderPane.setAlignment(text, Pos.CENTER);

        pane.setMinSize(550, 400);
        pane.setPadding(new Insets(50, 50, 50, 50));
    }

    /**
     * Method to set the error text
     * 
     * @param text The text to be set
     */
    public void setText(String text)
    {
        if (text == null)
            throw new NullPointerException("[DrawableErrorMessage] Null text");

        this.text.setText(text);
    }

    /**
     * Method to set the background color
     * 
     * @param color The string formatted with "#RRGGBB" style
     */
    public void setBackground(Color color)
    {
        if (color == null)
            throw new NullPointerException("[DrawableErrorMessage] Null background color");

        // Create the parsed string
        String parsed = color.toString().substring(2, 8);
        // System.out.println("#" + parsed);

        // Set the parsed color
        pane.setStyle("-fx-background-radius: 18 18 18 18; -fx-border-radius: 18 18 18 18; -fx-background-color: #" + parsed + ";");
    }

    /**
     * Sets the text color
     * 
     * @param color The color to be set
     */
    public void setTextColor(Color color)
    {
        if (color == null)
            throw new NullPointerException("[DrawableErrorMessage] Null text color");

        text.setFill(color);
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableErrorMessage] Null group");

        group.getChildren().add(pane);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableErrorMessage] Null group");

        group.getChildren().remove(pane);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableErrorMessage] Null ambient light");

        light.getScope().add(pane);
    }

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {}

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableErrorMessage] Null ambient light");

        light.getScope().remove(pane);
    }

    @Override
    public void enableVisibility()
    {}

    @Override
    public void disableVisibility()
    {}

    @Override
    public void updateAnimation()
    {
        // First call super
        super.updateAnimation();

        if (positions.size() == 0 && pane != null)
            // If the animation ends i make this object camera invisible
            pane.setVisible(false);
    }

    @Override
    public void addAnimationPosition(Point3D point, double speed)
    {
        // Call the super
        super.addAnimationPosition(point, speed);

        // Make the object camera visible
        pane.setVisible(true);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableErrorMessage] Null point");

        pane.translateXProperty().set(point.getX());
        pane.translateYProperty().set(point.getY());
        pane.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableErrorMessage] Null rotation");

        pane.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(pane.getTranslateX(), pane.getTranslateY(), pane.getTranslateZ());
    }
}
