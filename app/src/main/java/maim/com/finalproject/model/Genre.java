package maim.com.finalproject.model;

import android.net.Uri;

import java.util.List;

public class Genre {
    private String name;
    private Uri imageUri;
    private List<SubGenre> subGenres;

    public Genre(String name, List<SubGenre> subGenres) {
        this.name = name;
        this.subGenres = subGenres;
    }

    public Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
