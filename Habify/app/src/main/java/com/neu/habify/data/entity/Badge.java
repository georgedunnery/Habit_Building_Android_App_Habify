package com.neu.habify.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity to represent a badge which can be earned by the user. The badge should have a drawable
 * icon.
 */
@Entity(tableName = "badges")
public class Badge {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "value")
    private int value;

    @ColumnInfo(name = "icon")
    private String icon;

    public Badge(int id, @NonNull String name, int value, @NonNull String icon) {
        this.id = id;
        this.name = name;
        this.value = value;
        // This should be a reference to a drawable resource
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
