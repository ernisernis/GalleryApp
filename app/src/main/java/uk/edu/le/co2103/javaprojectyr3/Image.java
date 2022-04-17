package uk.edu.le.co2103.javaprojectyr3;

public class Image {

    byte[] image;
    int id;

    public Image(int id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
