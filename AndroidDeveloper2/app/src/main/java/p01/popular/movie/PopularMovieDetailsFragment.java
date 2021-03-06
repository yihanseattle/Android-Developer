package p01.popular.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.com.yihan.android.androiddeveloper.Constants;
import app.com.yihan.android.androiddeveloper.R;
import p02.custom.adapters.CustomListReviews;
import p02.custom.adapters.CustomListTrailers;
import p02.sqlite.FavoriteMovieSQLiteHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMovieDetailsFragment extends Fragment {

    static final String DETAIL_URI = "URI";

    TextView tvDescription, tvYear, tvTitle, tvRating, tvLength;
    ImageView ivPoster;
    Button bFavoriteMarker;
    ListView lvTrailers;
    ListView lvReviews;
    String[] movieTrailerURLs;
    boolean hasBeenFavorited;
    FavoriteMovieSQLiteHelper db;

    private String movieId;

    private Movie movie;

    // Debug information
    public static final String DEBUG_TAG = "MovieDetailsDebug";


    public PopularMovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        setContentView(R.layout.activity_popular_movie_activity_details);

        View rootView = inflater.inflate(R.layout.fragment_popular_movie_details, container, false);

        db = new FavoriteMovieSQLiteHelper(getActivity());
        initializeViews(rootView);
        showMovieInformation();
        showMovieTrailers(getActivity().getApplicationContext(), getActivity());
        showMovieReviews(getActivity().getApplicationContext(), getActivity());


        lvTrailers = (ListView) rootView.findViewById(R.id.lvMovieTrailers);
        lvReviews = (ListView) rootView.findViewById(R.id.lvMovieReviews);

        return rootView;
    }

    private void initializeViews(View rootView) {
        tvDescription = (TextView) rootView.findViewById(R.id.tvMovieDetailsDescription);
        tvYear = (TextView) rootView.findViewById(R.id.tvMovieDetailYear);
        tvTitle = (TextView) rootView.findViewById(R.id.tvMovieDetailsTitle);
        tvRating = (TextView) rootView.findViewById(R.id.tvMovieDetailRating);
        tvLength = (TextView) rootView.findViewById(R.id.tvMovieDetailLength);
        ivPoster = (ImageView) rootView.findViewById(R.id.ivPoster);
        bFavoriteMarker = (Button) rootView.findViewById(R.id.bMovieMarkAsFavorite);

    }

    private void showMovieInformation() {

        // get the flag for pane mode
        boolean isOnePane = false;
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.MOVIE_SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            isOnePane = prefs.getBoolean(Constants.IS_ONE_PANE_MODE, true);
        }

        // retrieve necessary data
        String movieDetailsOverview;
        String movieDetailsYear;
        String movieDetailsTitle;
        String movieDetailsRating;
        String movieDetailsPosterPath;
        String movieDetailsLength;

        if (isOnePane) {
            // one pane
            Intent intent = getActivity().getIntent();
            movieDetailsOverview = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_DESCRIPTION);
            movieDetailsYear = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_YEAR);
            movieDetailsTitle = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_TITLE);
            movieDetailsRating = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_RATING_VIEWER);
            movieDetailsPosterPath = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_POSTER_PATH);
            movieDetailsLength = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_LENGTH);
            movieId = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_MOVIEID);
        } else {
            // two pane
            Bundle args = getArguments();
            movieDetailsOverview = args.getString(Constants.MOVIE_DETAIL_INTENT_DESCRIPTION);
            movieDetailsYear = args.getString(Constants.MOVIE_DETAIL_INTENT_YEAR);
            movieDetailsTitle = args.getString(Constants.MOVIE_DETAIL_INTENT_TITLE);
            movieDetailsRating = args.getString(Constants.MOVIE_DETAIL_INTENT_RATING_VIEWER);
            movieDetailsPosterPath = args.getString(Constants.MOVIE_DETAIL_INTENT_POSTER_PATH);
            movieDetailsLength = args.getString(Constants.MOVIE_DETAIL_INTENT_LENGTH);
            movieId = args.getString(Constants.MOVIE_DETAIL_INTENT_MOVIEID);
        }

        // record the Movie object.
        // It's used later in Save As Favorite in the Database
        movie = new Movie(movieDetailsOverview, movieDetailsLength, movieDetailsPosterPath, movieDetailsRating, movieDetailsTitle, movieDetailsYear, movieId);

        // display information on the screen
        tvDescription.setText(movieDetailsOverview);
        tvYear.setText(movieDetailsYear);
        tvTitle.setText(movieDetailsTitle);
        tvRating.setText(movieDetailsRating);
        Picasso.with(getActivity().getApplicationContext()).load(movieDetailsPosterPath).into(ivPoster);
        tvLength.setText(movieDetailsLength);

        // DEBUG
//        List m = db.getAllMovies();
//        if (m.size() == 0) {
//            Toast.makeText(getActivity().getApplicationContext(), "empty", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getActivity().getApplicationContext(), "not empty", Toast.LENGTH_LONG).show();
//        }

        // set the correct text for the button
        Movie mm = db.getFavoriteMovie(movieId);
        if (mm == null) {
            // this movie has not been favorited yet
            bFavoriteMarker.setText("Favorite it!");
            hasBeenFavorited = false;
        } else {
            // this movie has not been favorited yet
            bFavoriteMarker.setText("Unfavorite it!");
            hasBeenFavorited = true;
        }
        bFavoriteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasBeenFavorited) {
                    bFavoriteMarker.setText("Favorite it!");
                    db.deleteFavoriteMovie(movie);

                    // DEBUG
//                    Movie mm = db.getFavoriteMovie(movieId);
//                    if (mm == null) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Delete Successful", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity().getApplicationContext(), "Delete Unsuccessful", Toast.LENGTH_LONG).show();
//                    }
//
//                    List m = db.getAllMovies();
//                    if (m.size() == 0) {
//                        Toast.makeText(getActivity().getApplicationContext(), "empty", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity().getApplicationContext(), "not empty", Toast.LENGTH_LONG).show();
//                    }
                } else {
                    bFavoriteMarker.setText("Unfavorite it.");
                    db.createFavoriteMovie(movie);

                    // DEBUG
//                    Movie mm = db.getFavoriteMovie(movieId);
//                    if (mm == null) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Create Unsuccessful", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity().getApplicationContext(), "Create Successful", Toast.LENGTH_LONG).show();
//                    }
//
//                    List m = db.getAllMovies();
//                    if (m.size() == 0) {
//                        Toast.makeText(getActivity().getApplicationContext(), "empty", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getActivity().getApplicationContext(), "not empty", Toast.LENGTH_LONG).show();
//                    }

                }
                hasBeenFavorited = !hasBeenFavorited;
            }
        });
    }

    private void showMovieTrailers(Context c, Activity a) {
        new GetMovieTrailers(c, a).execute(movieId);

    }

    private void showMovieReviews(Context c, Activity a) {
        new GetMovieReviews(c, a).execute(movieId);
    }


    public class GetMovieTrailers extends AsyncTask<String, Void, String> {

        private static final String urlPart1 = "https://api.themoviedb.org/3/movie/";
        private static final String urlPart2 = "?api_key=7484fef40eb0e9db4ad99a3bfbf849c7&append_to_response=trailers";

        private static final String trailers = "trailers";
        private static final String trailersYoutube = "youtube";
        private static final String trailersYoutubeName = "name";
        private static final String trailersYoutubeSource = "source";

        private List<MovieTrailer> listTrailers;
        private Context context;
        private Activity activity;

        public GetMovieTrailers(Context c, Activity a) {
            this.context = c;
            this.activity = a;
        }

        @Override
        protected void onPreExecute() {
            listTrailers = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = urlPart1 + params[0] + urlPart2;


                // make the network call and retrieve the data
                String jsonData = downloadUrl(url);

                // JSON parsing
                JSONObject j = new JSONObject(jsonData);
                JSONObject jObj = j.getJSONObject(trailers);
                JSONArray jYoutubeArray = jObj.getJSONArray(trailersYoutube);
                for (int i = 0; i < jYoutubeArray.length(); i++) {
                    JSONObject currentTrailer = jYoutubeArray.getJSONObject(i);
                    String movieTrailerName = currentTrailer.getString(trailersYoutubeName);
                    String movieTrailerSource = currentTrailer.getString(trailersYoutubeSource);
                    String movieTrailerURL = "https://www.youtube.com/watch?v=" + movieTrailerSource;
                    MovieTrailer mt = new MovieTrailer(movieTrailerURL, movieTrailerName);
                    listTrailers.add(mt);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            // save the names to an local array
            final String[] web = new String[listTrailers.size()];
            for (int i = 0; i < listTrailers.size(); i++) {
                web[i] = listTrailers.get(i).getTrialerName();
            }

            // save the URLs to an global array
            movieTrailerURLs = new String[listTrailers.size()];
            for (int i = 0; i < listTrailers.size(); i++) {
                movieTrailerURLs[i] = listTrailers.get(i).getTrailerURL();
            }

            CustomListTrailers adapter = new CustomListTrailers(context, activity, web, movieTrailerURLs);
            lvTrailers.setAdapter(adapter);
            lvTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(context, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movieTrailerURLs[position])));
                }
            });

        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {

            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            return responseStrBuilder.toString();
        }
    }

    public class GetMovieReviews extends AsyncTask<String, Void, String> {

        private static final String urlPart1 = "https://api.themoviedb.org/3/movie/";
        private static final String urlPart2 = "?api_key=7484fef40eb0e9db4ad99a3bfbf849c7&append_to_response=reviews";


        private static final String reviews = "reviews";
        private static final String reviewResults = "results";
        private static final String reviewResultsAuthor = "author";
        private static final String reviewResultsContent = "content";

        private List<MovieReview> listReviews;
        private Activity activity;
        private Context context;

        public GetMovieReviews(Context context, Activity activity) {
            this.activity = activity;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            listReviews = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = urlPart1 + params[0] + urlPart2;


                // make the network call and retrieve the data
                String jsonData = downloadUrl(url);

                // JSON parsing
                JSONObject j = new JSONObject(jsonData);
                JSONObject jObj = j.getJSONObject(reviews);
                JSONArray jYoutubeArray = jObj.getJSONArray(reviewResults);
                for (int i = 0; i < jYoutubeArray.length(); i++) {
                    JSONObject currentTrailer = jYoutubeArray.getJSONObject(i);
                    String movieReviewAuthor = currentTrailer.getString(reviewResultsAuthor);
                    String movieReviewContent = currentTrailer.getString(reviewResultsContent);
                    MovieReview mr = new MovieReview(movieReviewAuthor, movieReviewContent);
                    listReviews.add(mr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            if (listReviews.size() == 0) {
                Toast.makeText(context, "No Reviews Found For This Movie", Toast.LENGTH_SHORT).show();
            }

            // save the names to an local array
            final String[] author = new String[listReviews.size()];
            for (int i = 0; i < listReviews.size(); i++) {
                author[i] = listReviews.get(i).getReviewAuthor();
            }

            // save the URLs to an global array
            final String[] content = new String[listReviews.size()];
            for (int i = 0; i < listReviews.size(); i++) {
                content[i] = listReviews.get(i).getReviewContent();
            }

            CustomListReviews adapter = new CustomListReviews(context, activity, author, content);
            lvReviews.setAdapter(adapter);
            lvReviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(context, "You Clicked at " + author[+position], Toast.LENGTH_SHORT).show();
                }
            });

        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {

            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            return responseStrBuilder.toString();
        }
    }


}
