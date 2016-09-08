package com.wepay.wecrowd.wecrowd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import internal.APIResponseHandler;
import internal.AppNotifier;
import internal.DonationManager;
import internal.ImageCache;
import internal.LoginManager;
import models.Campaign;
import models.CampaignDetail;


public class CampaignDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_campaign_detail);

        setUpCampaignDetail();
        configureViewForUserType(LoginManager.userType);
    }

    @SuppressWarnings("unused")
    public void didSelectDonate(View view) {
        // Final context reference for use in anonymous inner class
        final Context context = this;

        if (LoginManager.userType == LoginManager.UserType.PAYER) {
            // No merchant is logged in, so the user is an anonymous donator
            // Anonymous donator can only manually enter payment information
            startActivity(new Intent(this, ManualPaymentActivity.class));
        } else if (LoginManager.userType == LoginManager.UserType.MERCHANT) {
            // Merchant is logged in, so present the payment options
            PopupMenu menu = new PopupMenu(this, findViewById(R.id.campaign_detail_button_donate));

            // Configure the menu item cases
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_donation_manual_payment: {
                            startActivity(new Intent(context, ManualPaymentActivity.class));
                            return true;
                        }
                        case R.id.menu_donation_swipe_payment: {
                            startActivity(new Intent(context, SwipePaymentActivity.class));
                            return true;
                        }
                        default: { return false; }
                    }
                }
            });

            menu.inflate(R.menu.menu_donation);
            menu.show();
        }
    }

    private void setUpCampaignDetail() {
        Intent intent;
        Integer campaignID;

        // Grab the selected campaign ID
        intent = getIntent();
        campaignID = intent.getIntExtra(CampaignFeedActivity.EXTRA_CAMPAIGN_ID, -1);

        // Set the donation's campaign
        DonationManager.configureDonationWithID(campaignID);

        // Fetch the server data for the given campaign
        CampaignDetail.fetchCampaignDetail(campaignID, new APIResponseHandler() {
            @Override
            public void onCompletion(Campaign campaign, Throwable throwable) {
                if (throwable == null) {
                    configureViewForCampaignDetail((CampaignDetail) campaign);
                } else {
                    AppNotifier.showSimpleError(CampaignDetailActivity.this,
                            getString(R.string.error_fetch_title),
                            getString(R.string.error_campaign_detail_fetch_preface),
                            throwable.getLocalizedMessage());
                }
            }
        });
    }

    private void configureViewForCampaignDetail(CampaignDetail campaignDetail) {
        Integer progressPercent;
        TextView progressTextView;
        ProgressBar progressBar;
        final ImageView imageView;
        final ProgressBar loadView;
        final Bitmap cachedImage;
        final String cacheKey;

        setTitle(campaignDetail.getTitle());

        progressPercent = floatProgress(campaignDetail.getProgress(), campaignDetail.getGoal());

        imageView = (ImageView) findViewById(R.id.loadable_image);
        loadView = (ProgressBar) findViewById(R.id.image_progress_bar);
        progressTextView = (TextView) findViewById(R.id.campaign_detail_progress_text);
        progressBar = (ProgressBar) findViewById(R.id.campaign_detail_progress_bar);

        progressTextView.setText(stringFromProgress(progressPercent));
        progressBar.setProgress(progressPercent);

        // Check the cache for this image before fetching it remotely
        cacheKey = ImageCache.getKeyForID(campaignDetail.getCampaignID());
        cachedImage = ImageCache.getBitmapFromCache(cacheKey);

        if (cachedImage == null) {
            Campaign.fetchImage(campaignDetail, new APIResponseHandler() {
                @Override
                public void onCompletion(Bitmap bitmap, Throwable throwable) {
                    if (throwable == null) {
                        imageView.setImageBitmap(bitmap);
                        loadView.setVisibility(View.GONE);
                    } else {
                        AppNotifier.showSimpleError(CampaignDetailActivity.this,
                                getString(R.string.error_fetch_title),
                                getString(R.string.error_campaign_image_fetch_preface),
                                throwable.getLocalizedMessage());
                    }
                }
            });
        } else {
            imageView.setImageBitmap(cachedImage);
            loadView.setVisibility(View.GONE);
        }
    }

    private void configureViewForUserType(LoginManager.UserType userType) {
        Button donateButton = (Button) findViewById(R.id.campaign_detail_button_donate);

        if (userType == LoginManager.UserType.PAYER) {
            donateButton.setText(getText(R.string.title_button_donate));
        } else if (userType == LoginManager.UserType.MERCHANT) {
            donateButton.setText(getText(R.string.title_button_accept_donation));
        }
    }

    private String stringFromProgress(Integer progress) {
        return progress.toString() + "%" + " funded";
    }

    private Integer floatProgress(Integer current, Integer goal) {
        Float progress = current.floatValue() / goal.floatValue();

        return Math.round(progress * 100);
    }
}
