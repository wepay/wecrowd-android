package internal;

/**
 * Created by zachv on 7/20/15.
 * Wecrowd Android
 */

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import models.User;

public class LoginManager {
    public enum UserType { MERCHANT, PAYER }
    public static UserType userType = UserType.PAYER;
    private static User user = null;

    public static void login(String email, String password, final APIResponseHandler handler) {
        RequestParams params;

        params = new RequestParams();
        params.put(Constants.LOGIN_EMAIL, email);
        params.put(Constants.LOGIN_PASSWORD, password);

        APIClient.post(Constants.ENDPOINT_LOGIN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Integer userID;
                String userToken;
                Throwable throwable = null;

                super.onSuccess(statusCode, headers, response);

                // Retrieve the data from JSON
                try { userID = response.getInt(Constants.PARAM_USER_ID); }
                catch (JSONException e) {
                    throwable = new Throwable(e.getLocalizedMessage());
                    handler.onCompletion(user, throwable);
                    return;
                }

                try { userToken = response.getString(Constants.PARAM_TOKEN); }
                catch (JSONException e) {
                    throwable = new Throwable(e.getLocalizedMessage());
                    handler.onCompletion(user, throwable);
                    return;
                }

                user = new User(userID, userToken);
                userType = UserType.MERCHANT;

                //noinspection ConstantConditions
                handler.onCompletion(user, throwable);
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  String responseString,
                                  Throwable throwable)
            {
                super.onFailure(statusCode, headers, responseString, throwable);

                handler.onCompletion(user, throwable);
            }
        });
    }

    public static void logout() {
        // Simulate logout since no actual /logout API call exists
        userType = UserType.PAYER;
        user = null;
    }
}
