package com.wepay.wecrowd.wecrowd;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wepay.android.CardReaderHandler;
import com.wepay.android.TokenizationHandler;
import com.wepay.android.enums.ErrorCode;
import com.wepay.android.enums.CurrencyCode;
import com.wepay.android.enums.CardReaderStatus;
import com.wepay.android.models.Error;
import com.wepay.android.models.PaymentInfo;
import com.wepay.android.models.PaymentToken;

import internal.APIResponseHandler;
import internal.AppNotifier;
import internal.Callback;
import internal.DonationManager;
import internal.PaymentManager;


public class SwipePaymentActivity extends AppCompatActivity
        implements CardReaderHandler, TokenizationHandler, Callback
{
    private TextView statusTextView;
    private EditText donateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_payment);

        storeLayoutViews();
        setUpViewInformation();

        statusTextView.setText(getString(R.string.message_swiper_start));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PaymentManager.stopCardReader(this);
    }

    private void storeLayoutViews() {
        statusTextView = (TextView) findViewById(R.id.swipe_payment_status);
    }

    private void setUpViewInformation() {
        ViewGroup donationField;
        TextView donateTag;

        donationField = (ViewGroup) findViewById(R.id.swiper_payment_donation_field);
        donateTag = (TextView) donationField.getChildAt(0);
        donateEditText = (EditText) donationField.getChildAt(1);

        donateTag.setText(getString(R.string.title_donation));
        donateEditText.setText(getString(R.string.demo_payer_donation_amount));
    }

    @Override
    public void onSuccess(PaymentInfo paymentInfo) {
        // No need to do anything
    }

    @Override
    public void onError(com.wepay.android.models.Error error) {
        final AppCompatActivity self = this;

        statusTextView.setText(getString(R.string.title_status_swipe));

        if (error.getErrorCode()== ErrorCode.CARD_READER_TIME_OUT_ERROR.getCode()) {
            AppNotifier.showErrorWithItem(this, getString(R.string.error_swiper_title),
                    getString(R.string.error_swiper_preface), error.getLocalizedMessage(),
                    getString(R.string.error_title_retry),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    // Selected the retry button
                                    PaymentManager.startCardSwipeTokenization(self,
                                            (CardReaderHandler) self,
                                            (TokenizationHandler) self);
                                    break;
                                }
                                case DialogInterface.BUTTON_NEGATIVE: {
                                    // Selected the cancel button
                                    finish();
                                    break;
                                }
                                default: { break; }
                            }
                        }
                    });
        } else {
            AppNotifier.showSimpleError(self, getString(R.string.error_swiper_title),
                    getString(R.string.error_swiper_preface), error.getLocalizedMessage());
        }

        statusTextView.setText(getString(R.string.message_user_try_again));
    }

    @Override
    public void onStatusChange(CardReaderStatus status) {
        String statusMessage = getString(R.string.title_status_swipe);

        if (status.equals(CardReaderStatus.NOT_CONNECTED)) {
            statusMessage = getString(R.string.message_swiper_not_connected);
        } else if (status.equals(CardReaderStatus.WAITING_FOR_CARD)) {
            statusMessage = getString(R.string.message_swiper_waiting);
        } else if (status.equals(CardReaderStatus.SWIPE_DETECTED)) {
            statusMessage = getString(R.string.message_swiper_detected);
        } else if (status.equals(CardReaderStatus.TOKENIZING)) {
            statusMessage = getString(R.string.message_swiper_tokenizing);
        } else if (status.equals(CardReaderStatus.STOPPED)) {
            statusMessage = getString(R.string.message_swiper_stopped);
        }

        statusTextView.setText(statusMessage);
    }

    @Override
    public void onPayerEmailRequested(CardReaderEmailCallback callback) {
        // provide the email address of the payer
        callback.insertPayerEmail("android-example@wepay.com");
    }

    @Override
    public void onTransactionInfoRequested(CardReaderTransactionInfoCallback callback) {
        // provide the amount, currency code and WePay account ID of the merchant
        callback.useTransactionInfo(21.61, CurrencyCode.USD, 1);
    }

    @Override
    public void onReaderResetRequested(CardReaderResetCallback callback) {
        // decide if you want to reset the reader,
        // then execute the callback with the appropriate response
        callback.resetCardReader(false);
    }

    @Override
    public void onSuccess(PaymentInfo paymentInfo, PaymentToken paymentToken) {
        statusTextView.setText("Tokenized!");

        performDonationWithTokenID(paymentToken.getTokenId());
    }

    @Override
    public void onError(PaymentInfo paymentInfo, Error error) {
        AppNotifier.dismissIndeterminateProgress();

        AppNotifier.showSimpleError(this, getString(R.string.message_failure_tokenization),
                getString(R.string.error_tokenization_preface),
                error.getLocalizedMessage());

        statusTextView.setText(getString(R.string.message_user_try_again));
    }

    @Override
    public void onCompletion() {
        finish();
    }

    @SuppressWarnings("unused")
    public void didChooseDonate(View view) {
        PaymentManager.startCardSwipeTokenization(this, this, this);
        DonationManager.configureDonationWithAmount(Integer.parseInt(donateEditText.getText().toString()));
    }

    private void performDonationWithTokenID(String tokenID) {
        final Activity self = this;

        DonationManager.configureDonationWithToken(tokenID);

        DonationManager.makeDonation(this, new APIResponseHandler() {
            @Override
            public void onCompletion(Throwable throwable) {
                AppNotifier.dismissIndeterminateProgress();

                if (throwable == null) {
                    AppNotifier.showSimpleSuccess(self,
                            getString(R.string.message_success_donation));

                    SignatureActivity.callback = (Callback) self;
                    startActivity(new Intent(self, SignatureActivity.class));
                } else {
                    AppNotifier.showSimpleError(self,
                            getString(R.string.message_failure_donation),
                            getString(R.string.error_donation_preface),
                            throwable.getLocalizedMessage());
                }
            }
        });

        AppNotifier.showIndeterminateProgress(self, getString(R.string.message_processing));
    }
}
