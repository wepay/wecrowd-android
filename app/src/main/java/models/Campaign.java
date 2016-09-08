package models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import internal.APIClient;
import internal.APIResponseHandler;
import internal.Constants;
import internal.JSONProcessor;

/**
 * Created by zachv on 7/20/15.
 * Wecrowd Android
 */

public class Campaign {
    // Protected members
    final Integer campaignID;
    final String title;
    private final String endDate;
    private Bitmap imageBMP;
    Integer goal;

    // Unexposed members
    String imageURL;

    // Constructors
    private Campaign(Integer ID, String title) {
        this.campaignID = ID;
        this.title = title;

        this.endDate = readableDateString();
    }

    private Campaign(Integer ID, String title, Integer goal) {
        this(ID, title);
        this.goal = goal;
    }

    Campaign(Integer ID, String title, Integer goal, String imageURL) {
        this(ID, title, goal);
        this.imageURL = imageURL;
    }

    // Static methods
    public static void fetchAllCampaigns(final APIResponseHandler responseHandler) {
        APIClient.get(Constants.ENDPOINT_FEATURED_CAMPAIGNS, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                super.onSuccess(statusCode, headers, responseArray);
                final Campaign[] campaigns = new Campaign[responseArray.length()];

                for (int i = 0; i < responseArray.length(); ++i) {
                    JSONObject responseObject;
                    Campaign campaign;

                    responseObject = JSONProcessor.jsonObjectFromArray(responseArray, i);

                    campaign = campaignFromJSONObject(responseObject);
                    campaigns[i] = campaign;
                }

                responseHandler.onCompletion(campaigns, null);
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  String responseString,
                                  Throwable throwable)
            {
                super.onFailure(statusCode, headers, responseString, throwable);

                responseHandler.onCompletion((Campaign[]) null, throwable);
            }
        });
    }

    static Campaign campaignFromJSONObject(JSONObject object) {
        String title, imageURL;
        Integer ID, goal;

        ID = JSONProcessor.integerFromJSON(object, Constants.CAMPAIGN_ID);
        title = JSONProcessor.stringFromJSON(object, Constants.CAMPAIGN_NAME);
        imageURL = JSONProcessor.stringFromJSON(object, Constants.CAMPAIGN_IMAGE_URL);
        goal = JSONProcessor.integerFromJSON(object, Constants.CAMPAIGN_GOAL);

        return new Campaign(ID, title, goal, imageURL);
    }

    // External methods
    public static void fetchImage(final Campaign campaign,
                                  final APIResponseHandler responseHandler)
    {
        if (campaign.imageBMP == null) {
            APIClient.getFromRaw(campaign.imageURL, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                    campaign.imageBMP = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    if (responseHandler != null) {
                        responseHandler.onCompletion(campaign.imageBMP, null);
                    }
                }

                @Override
                public void onFailure(int statusCode,
                                      Header[] headers,
                                      byte[] bytes,
                                      Throwable throwable) {
                    if (responseHandler != null) {
                        responseHandler.onCompletion((Bitmap) null, throwable);
                    }
                }
            });
        } else {
            responseHandler.onCompletion(campaign.imageBMP, null);
        }
    }

    // Utilities
    private String readableDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);

        return dateFormat.format(Calendar.getInstance().getTime());
    }

    // Accessors
    public Integer getCampaignID() { return campaignID; }
    public String getTitle() { return title; }
    public String getEndDate() { return endDate; }
    public Integer getGoal() { return goal; }
}
