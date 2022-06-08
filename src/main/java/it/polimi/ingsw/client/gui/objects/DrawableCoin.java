package it.polimi.ingsw.client.gui.objects;

import java.util.Objects;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;

public class DrawableCoin extends DrawableObject
{
    // The object parser
    private static final ObjectModelParser parser = new ObjectModelParser("Models/coin.obj", 5);

    /**
     * Object mesh and texture
     */
    private final TriangleMesh triangleMesh;
    private final MeshView coinMesh;

    /**
     * Constructor
     * 
     * @param updater The animation updater
     */
    public DrawableCoin(AnimationHandler updater)
    {
        super(updater);

        // Create the mesh referring to the object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(parser.getNormals());
        triangleMesh.getFaces().addAll(parser.getFaces());
        triangleMesh.getPoints().addAll(parser.getVertices());
        triangleMesh.getTexCoords().addAll(parser.getTextures());

        // Create the actual node with the mesh
        coinMesh = new MeshView(triangleMesh);

        // Create the texture material
        PhongMaterial material = new PhongMaterial();

        // Pick the texture
        material.setDiffuseMap(new Image(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("coin.png"))));
        // material.setDiffuseColor(Color.rgb(102, 92, 88));

        // Set the material
        coinMesh.setMaterial(material);

        // Set the mouse transparency
        coinMesh.setMouseTransparent(true);
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCoin] Null group");

        group.getChildren().add(coinMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCoin] Null group");

        group.getChildren().remove(coinMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCoin] Null point light");

        light.getScope().add(coinMesh);
    }

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCoin] Null ambient light");
        light.getScope().add(coinMesh);
    }

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCoin] Null point light");

        light.getScope().remove(coinMesh);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {}

    @Override
    public void enableVisibility()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void disableVisibility()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableCoin] Null 3D point");

        coinMesh.translateXProperty().set(point.getX());
        coinMesh.translateYProperty().set(point.getY());
        coinMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableCoin] Null rotation");

        coinMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(coinMesh.getTranslateX(), coinMesh.getTranslateY(), coinMesh.getTranslateZ());
    }
}
