package p02.helper;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.yihan.android.androiddeveloper.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenHelperFragment extends Fragment {

    public MainScreenHelperFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen_helper, container, false);
    }
}
