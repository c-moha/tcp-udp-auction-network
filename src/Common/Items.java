package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class Items implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rq;
    private String name;
    private String description;
    private double price;
    private int duration; // in seconds
    private long timestamp; // when the item was listed
    private ArrayList<UserInfo> subscriber;
    private UserInfo owner;

    public Items(String rq, String name, String description, double price, int duration, long timestamp,
            String owner) {
        this.rq = rq;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.timestamp = System.currentTimeMillis();
        this.owner = DataUsers.getUser(owner);
    }

    // Getters
    public String getRq() {
        return rq;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ArrayList<UserInfo> getSubscriber() {
        return this.subscriber;
    }

    // Setters:
    public void setRq(String rq) {
        this.rq = rq;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimeRemainingSeconds() {
        long currentTime = System.currentTimeMillis();
        long endTime = timestamp + (duration * 1000L); // Convert duration from sec to ms
        long remaining = endTime - currentTime;

        return Math.max(0, remaining / 1000); // Return in seconds, no negative values
    }

    // Adders:
    public void addSubscriber(UserInfo user) {
        this.subscriber.add(user);
    }

    @Override
    public String toString() {
        return "Item{" +
                "rq='" + rq + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", timestamp=" + timestamp +
                '}';
    }
}
