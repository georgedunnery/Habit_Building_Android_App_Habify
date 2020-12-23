package com.neu.habify.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Class to represent a user. Habify will only have one local user. This will store their profile
 * data.
 */
@Entity(tableName = "users",
        foreignKeys = @ForeignKey(entity = Event.class,
                                  parentColumns = "id",
                                  childColumns = "created"))
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "created")
    private int created;

    public User(@NonNull String firstName, @NonNull String lastName, @NonNull String username, int created) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        // An event must be created before referencing the foreign key here!
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }
}
