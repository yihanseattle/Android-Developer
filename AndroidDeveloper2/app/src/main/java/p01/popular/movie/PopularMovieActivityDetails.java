package p01.popular.movie;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import app.com.yihan.android.androiddeveloper.Constants;
import app.com.yihan.android.androiddeveloper.R;

public class PopularMovieActivityDetails extends ActionBarActivity {

    TextView tvDescription, tvYear, tvTitle, tvRating, tvLength;
    ImageView ivPoster;
    Button bFavoriteMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movie_activity_details);

        initializeViews();
        showMovieInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movie_activity_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        tvDescription = (TextView) findViewById(R.id.tvMovieDetailsDescription);
        tvYear = (TextView) findViewById(R.id.tvMovieDetailYear);
        tvTitle = (TextView) findViewById(R.id.tvMovieDetailsTitle);
        tvRating = (TextView) findViewById(R.id.tvMovieDetailRating);
        tvLength = (TextView) findViewById(R.id.tvMovieDetailLength);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);
        bFavoriteMarker = (Button) findViewById(R.id.bMovieMarkAsFavorite);
    }

    private void showMovieInformation() {
        Intent intent = getIntent();
        String movieDetailsOverview = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_DESCRIPTION);
        String movieDetailsYear = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_YEAR);
        String movieDetailsTitle = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_TITLE);
        String movieDetailsRating = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_RATING_VIEWER);
        String movieDetailsPosterPath = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_POSTER_PATH);
        String movieDetailsLength = intent.getStringExtra(Constants.MOVIE_DETAIL_INTENT_LENGTH);

        tvDescription.setText(movieDetailsOverview);
        tvYear.setText(movieDetailsYear);
        tvTitle.setText(movieDetailsTitle);
        tvRating.setText(movieDetailsRating);
        Picasso.with(getApplicationContext()).load(movieDetailsPosterPath).into(ivPoster);
        tvLength.setText(movieDetailsLength);
        bFavoriteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Marked as Favorite!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
