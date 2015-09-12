package p02.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import p01.popular.movie.Movie;

/**
 * Created by HanYi on 9/12/15.
 */
public class FavoriteMovieSQLiteHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "FavoriteMovieDB";
    private static final String table_FAVORITE_MOVIE = "favoritemovies";

    private static final String posterURL = "http://image.tmdb.org/t/p/w500";

    // column names
    private static final String movie_TITLE = "title";
    private static final String movie_YEAR = "year";
    private static final String movie_LENGTH = "length";
    private static final String movie_RATINGVIEWER = "ratingViewer";
    private static final String movie_DESCRIPTION = "description";
    private static final String movie_POSTERPATH = "posterPath";
    private static final String movie_ID = "id";
    private static final String movie_MOVIEID = "movieid";

    private static final String[] COLUMNS = { movie_ID, movie_MOVIEID, movie_TITLE, movie_YEAR, movie_LENGTH, movie_RATINGVIEWER, movie_DESCRIPTION, movie_POSTERPATH };

    public FavoriteMovieSQLiteHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS favoritemovies ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "movieid TEXT, " + "year TEXT, " + "length TEXT, " + "ratingViewer TEXT, " + "description TEXT, " + "posterPath TEXT, " + "title TEXT )";
        db.execSQL(CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS favoritemovies");
        this.onCreate(db);
    }

    public void createFavoriteMovie(Movie m) {
        // get reference of the FavoriteMovieDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(movie_MOVIEID, m.getMovieID());
        values.put(movie_TITLE, m.getTitle());
        values.put(movie_YEAR, m.getYear());
        values.put(movie_LENGTH, m.getLength());
        values.put(movie_RATINGVIEWER, m.getRatingViewer());
        values.put(movie_DESCRIPTION, m.getDescription());
        values.put(movie_POSTERPATH, m.getPosterPath());

        // insert movie
        db.insert(table_FAVORITE_MOVIE, null, values);

        // close database transaction
        db.close();
    }

    public Movie getFavoriteMovie(String movieId) {
        // get reference of the FavoriteMovieDB database
        SQLiteDatabase db = this.getReadableDatabase();
        // get book query
        Cursor cursor = db.query(table_FAVORITE_MOVIE, COLUMNS, " movieid = ?", new String[] { movieId }, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {

            return null;
        }


        Movie m = new Movie();
        m.setId(Integer.parseInt(cursor.getString(0)));
        m.setMovieID(cursor.getString(1));
        m.setTitle(cursor.getString(2));
        m.setYear(cursor.getString(3));
        m.setLength(cursor.getString(4));
        m.setRatingViewer(cursor.getString(5));
        m.setDescription(cursor.getString(6));
        m.setPosterPath(cursor.getString(7));

        return m;
    }

    // Deleting single favorite movie
    public void deleteFavoriteMovie(Movie m) {
        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        // delete book
        db.delete(table_FAVORITE_MOVIE, movie_MOVIEID + " = ?", new String[]{String.valueOf(m.getMovieID())});
        db.close();
    }

    public List<Movie> getAllMovies() {
        List<Movie> movies = new LinkedList<>();
        // select movie query
        String query = "SELECT  * FROM " + table_FAVORITE_MOVIE;

        // get reference of the FavoriteMovieDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Movie m = null;
        if (cursor.moveToFirst()) {
            do {
                m = new Movie();
                m.setId(Integer.parseInt(cursor.getString(0)));
                m.setMovieID(cursor.getString(1));
                m.setYear(cursor.getString(2));
                m.setLength(cursor.getString(3));
                m.setRatingViewer(cursor.getString(4));
                m.setDescription(cursor.getString(5));
                m.setPosterPath(cursor.getString(6));
                m.setTitle(cursor.getString(7));

                // Add book to books
                movies.add(m);
            } while (cursor.moveToNext());
        }
        return movies;
    }



}
