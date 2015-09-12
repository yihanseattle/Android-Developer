package app.com.yihan.android.androiddeveloper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    SharedPreferences sharedPreferences;

    public MainActivityFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Constants.MOVIE_SHAREPREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.MOVIE_SHAREPREFERENCE_SCROLL_CURRENT_POSITION, 0);
        editor.putString(Constants.MOVIE_SHAREPREFERENCE_SORT_BY, Constants.MOVIE_SETTINGS_SORT_BY_POPULAR);
        editor.putString("text", "abcdefg");
        editor.apply();

        return inflater.inflate(R.layout.fragment_main, container, false);
    }


}
