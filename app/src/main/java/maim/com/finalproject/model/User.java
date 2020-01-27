package maim.com.finalproject.model;

public class User {

    //private long id;
    private String email;
    private String UID;
    private String name;
    private String age;
    private String maxRange;
    //Preferred Location
    //Skills I can teach
    //Skills I want to learn
    //User image (uri)


    public User(String email, String UID, String name, String age, String maxRange) {
        this.email = email;
        this.UID = UID;
        this.name = name;
        this.age = age;
        this.maxRange = maxRange;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(String maxRange) {
        this.maxRange = maxRange;
    }
}
