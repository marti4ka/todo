package de.unimannheim.becker.todo.md.model;

public class Reminder {
    private int id;
    private double latitude;
    private double longtitude;
    private int itemId;

    public Reminder() {
        super();
    }

    public Reminder(int id, double latitude, double longtitude, int itemId) {
        super();
        this.id = id;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
