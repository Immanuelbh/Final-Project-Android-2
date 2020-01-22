package maim.com.finalproject.model;

import android.net.Uri;

public class SubGenre {
    String name;
    Uri imageUri;

    public SubGenre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
