package models;

/**
 * Created by zachv on 7/21/15.
 * Wecrowd Android
 */
public class User {
    private final Integer ID;
    private final String token;

    public User(Integer ID, String token) {
        this.ID = ID;
        this.token = token;
    }
}
