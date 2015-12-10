package chipset.pone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import chipset.pone.R;
import chipset.pone.models.Result;
import chipset.pone.resources.Constants;

/**
 * Developer: chipset
 * Package : chipset.pone.adapters
 * Project : Popular Movies
 * Date : 10/12/15
 */
public class MoviesGridAdapter extends BaseAdapter {

    private List<Result> mResults;
    private LayoutInflater mInflater;
    private Context mContext;

    public MoviesGridAdapter(Context context, List<Result> results) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mResults = results;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Result getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_grid_item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Result result = getItem(position);

        viewHolder.getNameTextView().setText(result.getTitle());
        Picasso.with(mContext).load(Constants.URL_POSTER_IMAGE + result.getPosterPath())
                .into(viewHolder.getPosterImageView());

        return convertView;
    }

    private static class ViewHolder {
        private TextView nameTextView;
        private ImageView posterImageView;

        public ViewHolder(View view) {
            this.posterImageView = (ImageView) view.findViewById(R.id.poster_image_view);
            this.nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public ImageView getPosterImageView() {
            return posterImageView;
        }
    }
}
