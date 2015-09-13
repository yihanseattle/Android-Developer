package p02.custom.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.yihan.android.androiddeveloper.R;

/**
 * Created by HanYi on 9/10/15.
 */
public class CustomListTrailers extends ArrayAdapter<String> {

    private final Context context;
    private final Activity activity;
    private final String[] web;
    private final String[] trailerURL;

    public CustomListTrailers(Context context,Activity activity, String[] web, String[] trailerURL) {
        super(context, R.layout.list_single_trailer, web);
        this.activity = activity;
        this.context = context;
        this.web = web;
        this.trailerURL = trailerURL;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = view;
        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_single_trailer, null, true);
        }

        TextView txtTitle = (TextView) rowView.findViewById(R.id.tvTrailer);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.ivTrailer);
        txtTitle.setText(web[position]);
        imageView.setImageResource(R.drawable.trailer);

        return rowView;
    }
}
