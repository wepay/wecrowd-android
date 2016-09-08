package internal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by zachv on 8/6/15.
 * Wecrowd Android
 */
public class InputManager {
    public static void setKeyboardDismissForEditText(final Context context, EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { hideKeyboard(context, v); }
            }
        });
    }

    public static void setKeyboardDismissForEditTexts(final Context context, EditText[] editTexts) {
        for (EditText editText : editTexts) {
            setKeyboardDismissForEditText(context, editText);
        }
    }

    private static void hideKeyboard(final Context context, View view) {
        InputMethodManager inputMethodManager;

        inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
