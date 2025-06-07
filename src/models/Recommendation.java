package models;
public class Recommendation {
    private String fromDoctor;
    private long timestamp;
    private String message;

    public Recommendation(String fromDoctor, long timestamp, String message) {
        this.fromDoctor = fromDoctor;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getFromDoctor() {
        return fromDoctor;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
    public void setFromDoctor(String fromDoctor) {
    this.fromDoctor = fromDoctor;
    }

    public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
    }

    public void setMessage(String message) {
    this.message = message;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
           "fromDoctor='" + fromDoctor + '\'' +
           ", timestamp=" + timestamp +
           ", message='" + message + '\'' +
           '}';
        }
}