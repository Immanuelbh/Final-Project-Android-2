package maim.com.finalproject.model;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

public class Genre implements Serializable {
    private String name;
    private String imageUrl;
    private HashMap<String,SubGenre> subGenres;

    public Genre(){}

    public Genre(String name, String image_url, HashMap<String,SubGenre> subGenres) {
        this.name = name;
        this.imageUrl = image_url;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image_url) {
        this.imageUrl = image_url;
    }


    public HashMap<String, SubGenre> getSubGenres() {
        return subGenres;
    }

    public void setSubGenres(HashMap<String, SubGenre> subGenres) {
        this.subGenres = subGenres;
    }

    @NonNull
    @Override
    public String toString() {
        String list;
        if(this.subGenres != null)
            list = "yes";
        else
            list = "no";
        return "Genre: " + this.name + " | " + "image URL: " + this.imageUrl + " | " + "has list? " + list;
    }
}
