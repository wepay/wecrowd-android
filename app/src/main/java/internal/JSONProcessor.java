package internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zachv on 7/21/15.
 * Wecrowd Android
 */
public class JSONProcessor {
    public static String stringFromJSON(JSONObject object, String key) {
        String value;

        try { value = object.getString(key); }
        catch (JSONException e) { throw new RuntimeException(e); }

        return value;
    }

    public static Integer integerFromJSON(JSONObject object, String key) {
        Integer value;

        try { value = object.getInt(key); }
        catch (JSONException e) { throw new RuntimeException(e); }

        return value;
    }

    public static JSONObject jsonObjectFromArray(JSONArray array, int index) {
        JSONObject object;

        try { object = array.getJSONObject(index); }
        catch (JSONException e) {throw new RuntimeException(e); }

        return object;
    }
}
