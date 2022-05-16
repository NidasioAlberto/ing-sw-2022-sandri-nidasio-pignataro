package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;

public class DrawableTower extends DrawableObject
{
    /**
     * Tower type (color)
     */
    private final TowerType TYPE;

    /**
     * Tower meshes
     */
    private TriangleMesh triangleMesh;
    private MeshView towerMesh;


    private volatile double offsetPosX;
    private volatile double offsetPosZ;

    private volatile double posX;
    private volatile double posZ;

    /**
     * Constructor
     * @param type The tower color
     */
    public DrawableTower(TowerType type)
    {
        if(type == null)
            throw new NullPointerException("[DrawableTower] Null tower type");

        // Set the constants
        TYPE = type;

        // Read the object file
        ObjectModelParser parser = new ObjectModelParser("Models/tower.obj", 1);

        // Create the mesh referring to the object file
        triangleMesh = new TriangleMesh();

        // Assign all the vertices, faces, textures and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(parser.getNormals());
        triangleMesh.getFaces().addAll(parser.getFaces());
        triangleMesh.getPoints().addAll(parser.getVertices());
        triangleMesh.getTexCoords().addAll(parser.getTextures());

        // Now i create the actual node with the mesh
        towerMesh = new MeshView(triangleMesh);

        // Set the texture color
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(type.getColor());
        material.setSpecularColor(Color.WHITE);

        // Set the material
        towerMesh.setMaterial(material);



        towerMesh.setOnMouseClicked((MouseEvent event) ->{
            offsetPosX = event.getX();
            offsetPosZ = event.getZ();
            posX = towerMesh.getTranslateX();
            posZ = towerMesh.getTranslateZ();
        });

        towerMesh.setOnMouseDragged((MouseEvent event) -> {
            //System.out.println("provolone");
            posX = posX + event.getX() - offsetPosX;
            posZ = posZ + event.getZ() - offsetPosZ;
            this.addAnimationPosition(new Point3D(posX, 0, posZ), 100);
        });
    }
    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableTower] Null group");

        // Add the mesh to the group
        group.getChildren().add(towerMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableTower] Null group");

        // remove the mesh from the group
        group.getChildren().remove(towerMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableTower] Null point light");

        // Add the mesh to the point light scope
        light.getScope().add(towerMesh);
    }

    // This method does nothing because the tower is subscribed only to point light
    @Override
    public void subscribeToAmbientLight(AmbientLight light) {}

    @Override
    public void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableTower] Null point");

        // Translate the mesh
        towerMesh.translateXProperty().set(point.getX());
        towerMesh.translateYProperty().set(point.getY());
        towerMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if(rotation == null)
            throw new NullPointerException("[DrawableTower] Null rotation");

        // Add the transformation to the mesh
        towerMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(towerMesh.getTranslateX(), towerMesh.getTranslateY(), towerMesh.getTranslateZ());
    }
}
