package p01.popular.movie;

/**
 * Created by HanYi on 7/12/15.
 */
public class Movie {

    private static final String posterURL = "http://image.tmdb.org/t/p/w500";
    private String title;
    private String year;
    private String length;
    private String ratingViewer;
    private String description;
    private String posterPath;

    public Movie(String description, String length, String posterPath, String ratingViewer, String title, String year) {
        this.description = description;
        this.length = length;
        this.posterPath = posterURL + posterPath;
        this.ratingViewer = ratingViewer;
        this.title = title;
        this.year = year;
    }

    public static String getPosterURL() {
        return posterURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getRatingViewer() {
        return ratingViewer;
    }

    public void setRatingViewer(String ratingViewer) {
        this.ratingViewer = ratingViewer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
