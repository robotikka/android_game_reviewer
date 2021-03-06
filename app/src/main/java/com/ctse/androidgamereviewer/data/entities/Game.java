package com.ctse.androidgamereviewer.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room performs Object Relational Mapping and the @Entity annotation ensures that
 * the Game entity class is mapped to the relevant SQLite table in the database.
 *
 * @see Entity
 * <a href="https://developer.android.com/topic/libraries/architecture/room">
 * Official Room Documentation</a>
 */
@Entity(indices = {@Index(value = {"game_title"}, unique = true)})
public class Game {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String _id;
    @ColumnInfo(name = "game_title")
    private String title;
    private String genre;
    private String image;
    private String release_date;

    public Game() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}