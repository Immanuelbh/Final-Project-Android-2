package maim.com.finalproject.model;


import java.io.Serializable;

public class SubGenre implements Serializable {
    private String name;
    private String imageUrl;
    //private boolean checked;

    public SubGenre() {    }

    public SubGenre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
