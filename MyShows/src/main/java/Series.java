import java.io.Serializable;

public class Series implements Serializable {
    public String name;
    public String href;
    public Integer rating;
    public String description;

    public Series(String name, String href, Integer rating, String description) {
        this.name = name;
        this.href = href;
        this.rating = rating;
        this.description = description;
    }
}
