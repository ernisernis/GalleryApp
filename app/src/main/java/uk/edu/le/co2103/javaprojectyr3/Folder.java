package uk.edu.le.co2103.javaprojectyr3;

public class Folder {

    String title;
    byte[] image;
   int count;

   public Folder(String title, int count, byte[] image) {
       this.title = title;
       this.count = count;
       this.image = image;
   }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() { return count; }

    public void setCount ( int count) {
       this.count = count;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
