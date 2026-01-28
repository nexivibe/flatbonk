package ape.flatbonk.entity.component;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.util.ShapeType;

public class RenderComponent {
    private ShapeType shapeType;
    private Color color;
    private float size;
    private boolean visible;
    private int layer;

    // For irregular polygon monsters
    private int polygonSides;
    private float[] vertexOffsets;  // Random offsets for each vertex to make irregular shape
    private boolean isBoss;

    // For Flatlandia-style organs inside monsters
    private float[] organOffsets;  // [x1, y1, size1, x2, y2, size2, ...] relative positions and sizes
    private int organCount;

    public RenderComponent() {
        this.shapeType = null;
        this.color = Color.WHITE.cpy();
        this.size = 10f;
        this.visible = true;
        this.layer = 0;
        this.polygonSides = 0;
        this.vertexOffsets = null;
        this.isBoss = false;
        this.organOffsets = null;
        this.organCount = 0;
    }

    public RenderComponent(ShapeType shapeType, Color color, float size) {
        this.shapeType = shapeType;
        this.color = color.cpy();
        this.size = size;
        this.visible = true;
        this.layer = 0;
        this.polygonSides = 0;
        this.vertexOffsets = null;
        this.isBoss = false;
        this.organOffsets = null;
        this.organCount = 0;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color.cpy();
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getPolygonSides() {
        return polygonSides;
    }

    public void setPolygonSides(int polygonSides) {
        this.polygonSides = polygonSides;
    }

    public float[] getVertexOffsets() {
        return vertexOffsets;
    }

    public void setVertexOffsets(float[] vertexOffsets) {
        this.vertexOffsets = vertexOffsets;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public void setBoss(boolean boss) {
        this.isBoss = boss;
    }

    public float[] getOrganOffsets() {
        return organOffsets;
    }

    public void setOrganOffsets(float[] organOffsets) {
        this.organOffsets = organOffsets;
    }

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
        this.organCount = organCount;
    }
}
