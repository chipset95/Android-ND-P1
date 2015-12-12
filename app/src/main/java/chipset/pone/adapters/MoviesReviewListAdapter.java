package chipset.pone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import chipset.pone.R;
import chipset.pone.models.MovieReviewsResults;

/**
 * Developer: chipset
 * Package : chipset.pone.adapters
 * Project : Popular Movies
 * Date : 11/12/15
 */
public class MoviesReviewListAdapter extends BaseAdapter {

    private List<MovieReviewsResults> mReviewsResults;
    private Context mContext;

    public MoviesReviewListAdapter(Context mContext, List<MovieReviewsResults> mReviewsResults) {
        this.mReviewsResults = mReviewsResults;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mReviewsResults.size();
    }

    @Override
    public MovieReviewsResults getItem(int position) {
        return mReviewsResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.getReviewAuthorTextView().setText(getItem(position).getAuthor());
        holder.getReviewContentTextView().setText(getItem(position).getContent());
        return convertView;
    }

    private class ViewHolder {

        private TextView reviewAuthorTextView, reviewContentTextView;

        public ViewHolder(View view) {
            reviewAuthorTextView = (TextView) view.findViewById(R.id.review_author_text_view);
            reviewContentTextView = (TextView) view.findViewById(R.id.review_content_text_view);
        }

        public TextView getReviewContentTextView() {
            return reviewContentTextView;
        }

        public TextView getReviewAuthorTextView() {
            return reviewAuthorTextView;
        }
    }

}
