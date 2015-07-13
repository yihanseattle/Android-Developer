package app.com.yihan.android.androiddeveloper;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import p01.popular.movie.PopularMovieActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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


    public void launcherSpotify(View v) {
        Toast.makeText(getApplicationContext(), "Launch Spotify", Toast.LENGTH_LONG).show();
    }

    public void launcherPopularMovies(View v) {
        Intent intent = new Intent(MainActivity.this, PopularMovieActivity.class);
        this.startActivity(intent);
    }

    public void launcherScoresApp(View v) {
        Toast.makeText(getApplicationContext(), "Launch Scores App", Toast.LENGTH_LONG).show();
    }

    public void launcherLibraryApp(View v) {
        Toast.makeText(getApplicationContext(), "Launch Library App", Toast.LENGTH_LONG).show();
    }

    public void launcherBuildItBigger(View v) {
        Toast.makeText(getApplicationContext(), "Launch Build It Bigger", Toast.LENGTH_LONG).show();
    }

    public void launcherXYZReader(View v) {
        Toast.makeText(getApplicationContext(), "Launch XYZ Reader", Toast.LENGTH_LONG).show();
    }

    public void launcherCapstoneMyOwnApp(View v) {
        Toast.makeText(getApplicationContext(), "Launch Capstone My Own App", Toast.LENGTH_LONG).show();
    }

}
