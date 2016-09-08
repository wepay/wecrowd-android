package internal;

/**
 * Created by zachv on 7/16/15.
 * WeCrowd Android
 */

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class APIClient {
    private static final String URL_BASE = "http://wecrowd.wepay.com/api/";
    private static final String USER_AGENT = "WeCrowd-android";

    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), null, responseHandler);
    }

    public static void getFromRaw(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, null, responseHandler);
    }

    public static void post(String url,
                            RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context,
                            String url,
                            Map<String, Object> params,
                            AsyncHttpResponseHandler responseHandler)
    {
        String contentType;
        JSONObject jsonParams;
        StringEntity entity = null;

        contentType = "application/json";
        jsonParams = new JSONObject(params);

        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            // Do nothing, let the library throw an error
        }

        client.setUserAgent(USER_AGENT);

        client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return URL_BASE + relativeUrl;
    }
}
