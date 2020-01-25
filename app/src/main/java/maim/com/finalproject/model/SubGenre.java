package maim.com.finalproject.model;


public class SubGenre {
    private String name;
    private String imageUrl;

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
