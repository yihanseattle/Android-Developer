package p02.custom.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.yihan.android.androiddeveloper.R;

/**
 * Created by HanYi on 9/11/15.
 */
public class CustomListReviews extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] author;
    private final String[] content;

    public CustomListReviews(Activity context, String[] author, String[] content) {
        super(context, R.layout.list_single_reviews, author);
        this.context = context;
        this.author = author;
        this.content = content;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single_reviews, null, true);

        TextView txtAuthor = (TextView) rowView.findViewById(R.id.tvReviewAuthor);
        txtAuthor.setText(author[position]);

        TextView txtContent = (TextView) rowView.findViewById(R.id.tvReviewContent);
        txtContent.setText(content[position]);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.ivReview);
        imageView.setImageResource(R.drawable.emptyheart);

        return rowView;
    }
}
