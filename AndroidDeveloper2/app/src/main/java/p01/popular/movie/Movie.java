package p01.popular.movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HanYi on 7/12/15.
 */
public class Movie implements Parcelable{

    private static final String posterURL = "http://image.tmdb.org/t/p/w500";
    private String title;
    private String year;
    private String length;
    private String ratingViewer;
    private String description;
    private String posterPath;
    private int id;
    private String movieID;

    public Movie() {
    }

    public Movie(String description, String length, String posterPath, String ratingViewer, String title, String year, String movieID) {
        this.description = description;
        this.length = length;
        this.posterPath = posterURL + posterPath;
        this.ratingViewer = ratingViewer;
        this.title = title;
        this.year = year;
        this.movieID = movieID;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        year = in.readString();
        length = in.readString();
        ratingViewer = in.readString();
        description = in.readString();
        posterPath = in.readString();
        movieID = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(length);
        dest.writeString(posterPath);
        dest.writeString(ratingViewer);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeInt(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }
}
