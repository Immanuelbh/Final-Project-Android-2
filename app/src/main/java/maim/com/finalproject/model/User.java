package maim.com.finalproject.model;

public class User {

    //private long id;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private int maxRange;
    //Preferred Location
    //Skills I can teach
    //Skills I want to learn
    //User image (uri)


    public User() {
    }


    public User(String firstName, String lastName, int age, int maxRange) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.maxRange = maxRange;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }
}
