package pt.castro.francesinhas.list;

/**
 * Created by lourenco on 21/06/15.
 */
public class PhotoReference {
    private String reference;
    private int width;
    private int height;

    public PhotoReference(final String reference, final int width, final int height) {
        this.reference = reference;
        this.width = width;
        this.height = height;
    }

    public String getReference() {
        return reference;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}