package p01.popular.movie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HanYi on 9/11/15.
 */
public class MovieTrailer implements Parcelable {

    private String trialerName;
    private String trailerURL;

    public MovieTrailer(String trailerURL, String trialerName) {
        this.trailerURL = trailerURL;
        this.trialerName = trialerName;
    }

    protected MovieTrailer(Parcel in) {
        trialerName = in.readString();
        trailerURL = in.readString();
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getTrailerURL() {
        return trailerURL;
    }

    public void setTrailerURL(String trailerURL) {
        this.trailerURL = trailerURL;
    }

    public String getTrialerName() {
        return trialerName;
    }

    public void setTrialerName(String trialerName) {
        this.trialerName = trialerName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trialerName);
        dest.writeString(trailerURL);
    }
}
