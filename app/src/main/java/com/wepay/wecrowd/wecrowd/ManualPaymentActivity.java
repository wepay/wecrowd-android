package com.wepay.wecrowd.wecrowd;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wepay.android.TokenizationHandler;
import com.wepay.android.enums.PaymentMethod;
import com.wepay.android.models.PaymentInfo;
import com.wepay.android.models.PaymentToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import internal.APIResponseHandler;
import internal.AppNotifier;
import internal.DonationManager;
import internal.InputManager;
import internal.LoginManager;
import internal.PaymentManager;

public class ManualPaymentActivity extends AppCompatActivity implements TokenizationHandler {
    private List<InfoField> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_payment);

        setUpInformationFields();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setUpInformationFields();
    }

    // Button message
    @SuppressWarnings("unused")
    public void didChooseDonate(View view) {
        performDonation();
    }

    private void performDonation() {
        Address address;
        Boolean virtualTerminal;
        EditText expirationMonthEditText, expirationYearEditText;
        String expirationMonth, expirationYear;

        // Set the address to our placeholder information
        address = new Address(Locale.getDefault());
        address.setAddressLine(0, getString(R.string.demo_address_line));
        address.setLocality(getString(R.string.demo_address_locality));
        address.setPostalCode(getString(R.string.demo_address_postal_code));
        address.setCountryCode(getString(R.string.demo_address_country_code));

        virtualTerminal = LoginManager.userType == LoginManager.UserType.MERCHANT;

        expirationMonthEditText = (EditText) findViewById(R.id.manual_payment_month_entry);
        expirationYearEditText = (EditText) findViewById(R.id.manual_payment_year_entry);

        expirationMonth = expirationMonthEditText.getText().toString();
        expirationYear = expirationYearEditText.getText().toString();

        PaymentInfo paymentInfo = new PaymentInfo(getValueForId(R.id.manual_payment_first_name),
                getValueForId(R.id.manual_payment_last_name),
                getValueForId(R.id.manual_payment_email),
                getString(R.string.demo_donation_description),
                address, address, PaymentMethod.MANUAL,
                getValueForId(R.id.manual_payment_card_number),
                getValueForId(R.id.manual_payment_cvv),
                expirationMonth, expirationYear, virtualTerminal);

        // Tokenize
        PaymentManager.tokenizeInfo(this, paymentInfo, this);

        // Show the loading view
        AppNotifier.showIndeterminateProgress(this, getString(R.string.message_processing));
    }

    private void setUpInformationFields() {
        final ViewGroup fieldViewGroup;
        TextView tagTextView;
        EditText entryEditText, expirationMonthEditText, expirationYearEditText;

        // Grab the group containing all the fields
        fieldViewGroup = (ViewGroup) findViewById(R.id.manual_payment_fields);
        if (fields == null) { fields = fieldConfigurationList(); }

        for (int i = 0; i < fieldViewGroup.getChildCount(); ++i) {
            // Grab the row containing the tag and entry views
            ViewGroup field = (ViewGroup) fieldViewGroup.getChildAt(i);

            // Grab the views in the field
            tagTextView = (TextView) field.findViewById(R.id.linear_tagged_title);
            entryEditText = (EditText) field.findViewById(R.id.linear_tagged_entry);

            InputManager.setKeyboardDismissForEditText(this, entryEditText);

            // Set the text for the field child views
            if (i < fields.size()) {
                entryEditText.setInputType(fields.get(i).inputType);
                tagTextView.setText(fields.get(i).getTag());
                entryEditText.setText(fields.get(i).getEntry(), TextView.BufferType.EDITABLE);
            }
        }

        expirationMonthEditText = (EditText) findViewById(R.id.manual_payment_month_entry);
        expirationYearEditText = (EditText) findViewById(R.id.manual_payment_year_entry);

        InputManager.setKeyboardDismissForEditText(this, expirationMonthEditText);
        InputManager.setKeyboardDismissForEditText(this, expirationYearEditText);
    }

    // Couldn't figure out how to just add the strings directly to XML while reusing the ViewGroup
    // with the <include> tag, so using a programmatic structure.
    private List<InfoField> fieldConfigurationList() {
        List<InfoField> configList = new ArrayList<>();

        configList.add(new InfoField(getString(R.string.title_donation), getPrefillAmount(),
                InputType.TYPE_CLASS_NUMBER));
        configList.add(new InfoField(getString(R.string.title_first_name), getPrefillFirstName(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME));
        configList.add(new InfoField(getString(R.string.title_last_name), getPrefillLastName(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME));
        configList.add(new InfoField(getString(R.string.title_email), getPrefillEmail(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
        configList.add(new InfoField(getString(R.string.title_card_number), getPrefillCardNumber(),
                InputType.TYPE_CLASS_NUMBER));
        configList.add(new InfoField(getString(R.string.title_cvv), getPrefillCVV(),
                InputType.TYPE_CLASS_NUMBER));
        configList.add(new InfoField(getString(R.string.title_zip_code), getPrefillZipCode(),
                InputType.TYPE_CLASS_NUMBER));

        return configList;
    }

    private String getValueForId(final int ID) {
        TextView textView = (TextView) getFieldForID(ID).findViewById(R.id.linear_tagged_entry);

        return textView.getText().toString();
    }

    private ViewGroup getFieldForID(final int ID) {
        return (ViewGroup) findViewById(R.id.manual_payment_fields).findViewById(ID);
    }

    @Override
    public void onSuccess(PaymentInfo paymentInfo, PaymentToken paymentToken) {
        final Context context = this;
        final Integer amount = Integer.parseInt(getValueForId(R.id.manual_payment_donation));

        DonationManager.configureDonationWithToken(paymentToken.getTokenId());
        DonationManager.configureDonationWithAmount(amount);

        DonationManager.makeDonation(this, new APIResponseHandler() {
            @Override
            public void onCompletion(Throwable throwable) {
                AppNotifier.dismissIndeterminateProgress();

                if (throwable == null) {
                    AppNotifier.showSimpleSuccess(context,
                            getString(R.string.message_success_donation));

                    finish();
                } else {
                    AppNotifier.showSimpleError(context,
                            getString(R.string.message_failure_donation),
                            getString(R.string.error_donation_preface),
                            throwable.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void onError(PaymentInfo paymentInfo, com.wepay.android.models.Error error) {
        AppNotifier.dismissIndeterminateProgress();

        AppNotifier.showSimpleError(this, getString(R.string.message_failure_tokenization),
                getString(R.string.error_tokenization_preface),
                error.getLocalizedMessage());
    }

    // Data for a field of information
    class InfoField {
        private final String tag;
        private final String entry;
        private final int inputType;

        public InfoField(String tag, String entry, int inputType) {
            this.tag = tag;
            this.entry = entry;
            this.inputType = inputType;
        }

        public String getTag() { return tag; }
        public String getEntry() { return entry; }
    }

    // Default field value getters
    private String getPrefillAmount() { return getString(R.string.demo_payer_donation_amount); }
    private String getPrefillFirstName() { return getString(R.string.demo_payer_first_name); }
    private String getPrefillLastName() { return getString(R.string.demo_payer_last_name); }
    private String getPrefillEmail() { return getString(R.string.demo_payer_email); }
    private String getPrefillCardNumber() { return getString(R.string.demo_payer_card_number); }
    private String getPrefillCVV() { return getString(R.string.demo_payer_cvv); }
    private String getPrefillZipCode() { return getString(R.string.demo_payer_expiration_zip_code); }
}
