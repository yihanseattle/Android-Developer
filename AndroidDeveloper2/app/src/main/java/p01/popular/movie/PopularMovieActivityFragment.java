package p01.popular.movie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.com.yihan.android.androiddeveloper.Constants;
import app.com.yihan.android.androiddeveloper.R;
import p02.custom.adapters.CustomListTrailers;
import p02.sqlite.FavoriteMovieSQLiteHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMovieActivityFragment extends Fragment {

    // Settings Menu
    private Dialog settingsDialog;
    private RadioGroup rgSettings;
    private RadioButton rbSortByPop, rbSortByHigestRate;
    private Button bSave;

    // essential views
    public View rootView;
    GridView gridView;
    MyAdapter adapter;

    // Debug information
    public static final String DEBUG_TAG = "PopularMovieDebug";

    // save the list of movies
    private volatile List<Movie> listOfMovies;

    // for checking the internet connection
    protected FragmentActivity mActivity;

    // save the current position of the list
    SharedPreferences sharedPreferences;
    int mCurrentPosition;

    FavoriteMovieSQLiteHelper db;

    public PopularMovieActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sharedPreferences = mActivity.getSharedPreferences(Constants.MOVIE_SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // make sure when the screen rotation finishes, it won't do the networking again
        outState.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) listOfMovies);

        // also save the current GridView position
        // when the user comes back from the detailed screen,
        // the previous position can be restored
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.MOVIE_SHAREPREFERENCE_SCROLL_CURRENT_POSITION, mCurrentPosition);
        editor.apply();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        db = new FavoriteMovieSQLiteHelper(getActivity());
        db.onUpgrade(db.getWritableDatabase(), 1, 2);


        // find reference
        listOfMovies = new ArrayList<>();

        rootView = inflater.inflate(R.layout.fragment_popular_movie, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);

        // make sure to have a better layout when landscape
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // set the number of columns to 5
            gridView.setNumColumns(4);
        }

        // launch the detailed activity when clicked on the gridview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(getActivity(), PopularMovieDetails.class);
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_DESCRIPTION, listOfMovies.get(position).getDescription());
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_LENGTH, listOfMovies.get(position).getLength());
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_POSTER_PATH, listOfMovies.get(position).getPosterPath());
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_RATING_VIEWER, listOfMovies.get(position).getRatingViewer());
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_TITLE, listOfMovies.get(position).getTitle());
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_YEAR, listOfMovies.get(position).getYear());
                intent.putExtra(Constants.MOVIE_DETAIL_INTENT_MOVIEID, listOfMovies.get(position).getMovieID());
                startActivity(intent);
            }
        });

        // record the GridView position
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentPosition = firstVisibleItem;
            }
        });


        // check for internet connection first
        if (isNetworkAvailable()) {
            if (savedInstanceState == null || !savedInstanceState.containsKey("list")) {
                // do the networking if there is no information of GridView was found
                String result = sharedPreferences.getString(Constants.MOVIE_SHAREPREFERENCE_SORT_BY, Constants.MOVIE_SETTINGS_SORT_BY_POPULAR);
                new GetMovies(getActivity().getBaseContext(), adapter, gridView, listOfMovies, sharedPreferences).execute(result);
            } else {
                // skip the networking part and restore the GridView
                listOfMovies = savedInstanceState.getParcelableArrayList("list");
                adapter = new MyAdapter(getActivity().getApplicationContext(), listOfMovies);
                gridView.setAdapter(adapter);
                gridView.setSelection(sharedPreferences.getInt(Constants.MOVIE_SHAREPREFERENCE_SCROLL_CURRENT_POSITION, 0));
            }
        } else {
            // show a toast to let the user know what is happening
            Toast.makeText(getActivity().getApplicationContext(), "No Network Connection.", Toast.LENGTH_LONG).show();
        }


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_popular_movie, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.pop_movie_settings) {

            displaySettingsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void displaySettingsMenu() {
        settingsDialog = new Dialog(getActivity());

        // setting custom layout to dialog
        settingsDialog.setContentView(R.layout.pop_movie_settings_dialog);
        settingsDialog.setTitle("Popular Movies Settings");

        rgSettings = (RadioGroup) settingsDialog.findViewById(R.id.radioSortByGroup);
        rbSortByHigestRate = (RadioButton) settingsDialog.findViewById(R.id.radioSortByHighestRate);
        rbSortByPop = (RadioButton) settingsDialog.findViewById(R.id.radioSortByMostPop);
        if (sharedPreferences.getString(Constants.MOVIE_SHAREPREFERENCE_SORT_BY, Constants.MOVIE_SETTINGS_SORT_BY_POPULAR).equals(Constants.MOVIE_SETTINGS_SORT_BY_HIGHEST_RATE))
            rbSortByHigestRate.setChecked(true);
        else
            rbSortByPop.setChecked(true);

        bSave = (Button) settingsDialog.findViewById(R.id.bSaveSettings);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result;

                // get selected radio button from radioGroup
                int selectedId = rgSettings.getCheckedRadioButtonId();

                // handle the favorite first because it's temporary
                if (selectedId == R.id.radioSortByFavorite) {
                    result = Constants.MOVIE_SETTINGS_SORT_BY_FAVORITE_MOVIE;
                    // don't save to SharedPreference because it's temporary
                } else {
                    result = selectedId == R.id.radioSortByHighestRate ? Constants.MOVIE_SETTINGS_SORT_BY_HIGHEST_RATE : Constants.MOVIE_SETTINGS_SORT_BY_POPULAR;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.MOVIE_SHAREPREFERENCE_SORT_BY, result);
                    editor.apply();
                }


                settingsDialog.dismiss();

                if (isNetworkAvailable()) {
                    if (result.equals(Constants.MOVIE_SETTINGS_SORT_BY_FAVORITE_MOVIE)) {
                        // load local favorite movies
                        List<Movie> list = db.getAllMovies();
                        adapter = new MyAdapter(getActivity().getApplicationContext(), list);
                        gridView.setAdapter(adapter);
//                        gridView.setSelection(sharedPreferences.getInt(Constants.MOVIE_SHAREPREFERENCE_SCROLL_CURRENT_POSITION, 0));


                    } else {
                        new PopularMovieActivityFragment().new GetMovies(getActivity().getBaseContext(), adapter, gridView, listOfMovies, sharedPreferences).execute(result);
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Network Connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
        // show the settings dialog
        settingsDialog.show();
    }


    private class MyAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;
        Context c;
        List<Movie> l;

        public MyAdapter(Context context, List<Movie> list) {
            inflater = LayoutInflater.from(context);
            this.l = list;
            c = context;
            for (Movie m : list) {
                items.add(new Item(m.getTitle(), 11111));
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;

            if (v == null) {
                v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);

            Item item = (Item) getItem(i);

//            picture.setImageResource(item.drawableId);

            Picasso.with(c).load(l.get(i).getPosterPath()).into(picture);

            name.setText(item.name);

            return v;
        }

        private class Item {
            final String name;
            final int drawableId;

            Item(String name, int drawableId) {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }


    public class GetMovies extends AsyncTask<String, Void, String> {

        // URL information
        private static final String urlPart1 = "http://api.themoviedb.org/3/discover/movie?sort_by=";
        private static final String sortByPopular = "popularity.desc";
        private static final String sortByHighestRated = "vote_average.desc";
        private static final String urlPart2 = "&api_key=";
        private static final String apiKey = "7484fef40eb0e9db4ad99a3bfbf849c7";

        // JSON parsing
        private static final String jsonArrayResult = "results";
        private static final String title = "original_title";
        private static final String year = "release_date";
        private static final String movieId = "id";
        // no length information given in json data
        // private static final String length;
        private static final String ratingViewer = "vote_average";
        private static final String description = "overview";
        private static final String posterPath = "poster_path";

        // to hold the parent activity reference
        Context c;
        GridView gv;
        MyAdapter ma;
        List<Movie> l;
        SharedPreferences sp;

        public GetMovies(Context c, MyAdapter ma, GridView gv, List<Movie> l, SharedPreferences sp) {
            this.c = c;
            this.ma = ma;
            this.gv = gv;
            this.l = l;
            this.sp = sp;
        }

        @Override
        protected void onPreExecute() {
            l.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = "";
                if (params[0].equals(Constants.MOVIE_SETTINGS_SORT_BY_FAVORITE_MOVIE)) {

                } else if (params[0].equals(Constants.MOVIE_SETTINGS_SORT_BY_HIGHEST_RATE)) {
                    url = urlPart1 + sortByHighestRated + urlPart2 + apiKey;
                } else {
                    url = urlPart1 + sortByPopular + urlPart2 + apiKey;
                }

                // make the network call and retrieve the data
                String jsonData = downloadUrl(url);

                // JSON parsing
                JSONObject j = new JSONObject(jsonData);
                JSONArray jArray = j.getJSONArray(jsonArrayResult);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject currentMovie = jArray.getJSONObject(i);
                    String movieDescription = currentMovie.getString(description);
                    String movieLength = "120min";
                    String moviePosterPath = currentMovie.getString(posterPath);
                    String movieRatingViewer = currentMovie.getString(ratingViewer) + "/10.0";
                    String movieTitle = currentMovie.getString(title);
                    String movieYear = currentMovie.getString(year);
                    String id = currentMovie.getString(movieId);
                    Movie m = new Movie(movieDescription, movieLength, moviePosterPath, movieRatingViewer, movieTitle, movieYear, id);
                    l.add(m);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ma = new MyAdapter(c, l);
            gv.setAdapter(ma);
            gv.setSelection(sp.getInt(Constants.MOVIE_SHAREPREFERENCE_SCROLL_CURRENT_POSITION, 0));
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
