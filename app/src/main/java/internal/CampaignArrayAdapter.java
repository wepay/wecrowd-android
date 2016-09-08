package internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wepay.wecrowd.wecrowd.R;

import java.util.ArrayList;

import models.Campaign;

/**
 * Created by zachv on 7/21/15.
 * Wecrowd Android
 */
public class CampaignArrayAdapter extends ArrayAdapter<Campaign> {
    private final Context context;
    private final ArrayList<Campaign> values;


    public CampaignArrayAdapter(Context context, ArrayList<Campaign> values) {
        super(context, -1, values);

        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        View rowView;
        ViewHolder viewHolder;
        final ViewHolder finalViewHolder;
        final Campaign campaign;
        final Bitmap campaignImage;
        final String cacheKey;

        campaign = this.values.get(position);
        rowView = convertView;

        // Set up our view for recycling to reduce load
        if (rowView == null) {
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_campaign_feed, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.titleText = (TextView) rowView.findViewById(R.id.campaign_feed_cell_title);
            viewHolder.goalText = (TextView) rowView.findViewById(R.id.campaign_feed_cell_end_date);
            viewHolder.imageView = (ImageView) rowView.findViewById(R.id.loadable_image);
            viewHolder.progressView = (ProgressBar) rowView.findViewById(R.id.image_progress_bar);

            rowView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.titleText.setText(campaign.getTitle(), TextView.BufferType.NORMAL);
        viewHolder.goalText.setText(campaign.getEndDate());

        // Check if the image already exists in the cache
        cacheKey = ImageCache.getKeyForID(campaign.getCampaignID());
        campaignImage = ImageCache.getBitmapFromCache(cacheKey);

        // Necessary for accessing members from anonymous inner class
        finalViewHolder = viewHolder;

        if (campaignImage == null) {
            Campaign.fetchImage(campaign, new APIResponseHandler() {
                @Override
                public void onCompletion(Bitmap bitmap, Throwable throwable) {
                    finalViewHolder.imageView.setImageBitmap(bitmap);

                    ImageCache.addBitmapToCache(cacheKey, bitmap);

                    finalViewHolder.progressView.setVisibility(View.GONE);
                }
            });
        } else {
            finalViewHolder.imageView.setImageBitmap(campaignImage);
            finalViewHolder.progressView.setVisibility(View.GONE);
        }

        return rowView;
    }

    // Used to recycle the main view item
    static class ViewHolder {
        TextView titleText, goalText;
        ImageView imageView;
        ProgressBar progressView;
    }
}
