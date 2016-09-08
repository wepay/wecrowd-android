package com.wepay.wecrowd.wecrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import internal.APIResponseHandler;
import internal.AppNotifier;
import internal.CampaignArrayAdapter;
import internal.LoginManager;
import models.Campaign;

public class CampaignFeedActivity extends AppCompatActivity {
    public static final String EXTRA_CAMPAIGN_ID = "com.wepay.wecrowd.CAMPAIGN_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_feed);

        listView = (ListView) findViewById(R.id.listview_campaigns);

        setUpListView(listView);
        setUpListViewListener(listView);
    }

    // Utility methods
    private void setUpListView(final ListView listView) {
        // Final Context referece for use in anonymous inner class
        final Context context = this;

        Campaign.fetchAllCampaigns(new APIResponseHandler() {
            @Override
            public void onCompletion(Campaign[] campaigns, Throwable throwable) {
                if (throwable == null) {
                    final ArrayList<Campaign> campaignList;
                    final CampaignArrayAdapter campaignArrayAdapter;
                    final Integer campaignCount;

                    // There's no merchant account associated with the featured campaigns,
                    // so hardcode the merchant's campaigns for the sake of the demo
                    campaignCount = LoginManager.userType == LoginManager.UserType.PAYER ? campaigns.length : 2;
                    campaignList = new ArrayList<>(campaignCount);

                    // Add all retrieved campaigns to the list
                    campaignList.addAll(Arrays.asList(campaigns).subList(0, campaignCount));

                    campaignArrayAdapter = new CampaignArrayAdapter(context, campaignList);
                    listView.setAdapter(campaignArrayAdapter);
                } else {
                    AppNotifier.showSimpleError(context,
                            getString(R.string.error_fetch_title),
                            getString(R.string.error_campaigns_fetch_preface),
                            throwable.getLocalizedMessage());
                }
            }
        });
    }

    private void setUpListViewListener(final ListView listView) {
        // Designate an action for when a user selects a list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Campaign campaign = (Campaign) listView.getAdapter().getItem(position);

                beginDetailActivity(campaign.getCampaignID());
            }
        });
    }

    private void beginDetailActivity(Integer campaignID) {
        Intent intent;

        intent = new Intent(this, CampaignDetailActivity.class);
        intent.putExtra(EXTRA_CAMPAIGN_ID, campaignID);

        startActivity(intent);
    }
}
