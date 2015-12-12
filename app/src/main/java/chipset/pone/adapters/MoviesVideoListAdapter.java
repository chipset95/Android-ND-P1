package chipset.pone.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import chipset.pone.R;
import chipset.pone.models.MovieVideosResults;
import chipset.pone.resources.Constants;

/**
 * Developer: chipset
 * Package : chipset.pone.adapters
 * Project : Popular Movies
 * Date : 11/12/15
 */
public class MoviesVideoListAdapter extends BaseAdapter {

    private List<MovieVideosResults> mVideosResults;
    private Context mContext;

    public MoviesVideoListAdapter(Context mContext, List<MovieVideosResults> mVideosResults) {
        this.mVideosResults = mVideosResults;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        Log.d("size", mVideosResults.size() + "");
        return mVideosResults.size();
    }

    @Override
    public MovieVideosResults getItem(int position) {
        return mVideosResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        String url = Constants.URL_YOUTUBE_IMAGE_HEADER + getItem(position).getKey() + Constants.URL_YOUTUBE_IMAGE_FOOTER;
        Log.d("url",url);
        Picasso.with(mContext).load(url).into(holder.getVideoImageImageView());
        return convertView;
    }

    private class ViewHolder {

        private ImageView videoImageImageView;

        public ImageView getVideoImageImageView() {
            return videoImageImageView;
        }

        public ViewHolder(View view) {
            this.videoImageImageView = (ImageView) view.findViewById(R.id.video_image_image_view);
        }
    }

}
