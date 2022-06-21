package com.kwendaapp.rideo.Activities;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aapbd.appbajarlib.notification.AlertMessage;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;
import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.Helper.URLHelper;
import com.kwendaapp.rideo.Models.CardInfo;
import com.kwendaapp.rideo.R;
import com.kwendaapp.rideo.UserApplication;
import com.kwendaapp.rideo.Utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


public class ActivityWallet extends AppCompatActivity implements View.OnClickListener, PaytmPaymentTransactionCallback, PaymentStatusListener, PaymentResultListener {
    private final int ADD_CARD_CODE = 435;
    private static final int PAYPAL_REQUEST_CODE = 7001;
    private Button add_fund_button;
    private EditText money_et;
    TextView balance_tv;
    private String session_token;
    private Button one;
    private Button two;
    private Button three;
    double update_amount;
    private ArrayList<CardInfo> cardInfoArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView lblCardNumber;
    private LinearLayout lnrAddmoney;
    private int selectedPosition = 0;
    private final Utilities utils = new Utilities();
    private CardInfo cardInfo;
    private RadioGroup paymentgroup;
    private RadioButton stripe, paypal, flutterwave, upi, razorpay;
    private ImageView stripeimg, paypalimg, flutterwaveimg, upiimg, razorpayimg;
    boolean loading;
    private static PayPalConfiguration config;
    private String PaymentType = "CARD";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wallet);
        cardInfoArrayList = new ArrayList<>();
        add_fund_button = findViewById(R.id.add_fund_button);
        balance_tv = findViewById(R.id.balance_tv);
        TextView currencySymbol = findViewById(R.id.currencySymbol);
        paymentgroup = findViewById(R.id.paymentgroup);
        context = this;
        customDialog = new CustomDialog(context);
        currencySymbol.setText(SharedHelper.getKey(context, "currency"));
        money_et = findViewById(R.id.money_et);
        TextView lblPaymentChange = findViewById(R.id.lblPaymentChange);
        lblCardNumber = findViewById(R.id.lblCardNumber);
        LinearLayout lnrClose = findViewById(R.id.lnrClose);
        lnrAddmoney = findViewById(R.id.lnrAddmoney);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        Button add_money = findViewById(R.id.add_money);
        ImageView backArrow = findViewById(R.id.backArrow);
        stripe = findViewById(R.id.stripe);
        paypal = findViewById(R.id.paypal);
        upi = findViewById(R.id.upi);
        razorpay = findViewById(R.id.razorpay);
        flutterwave = findViewById(R.id.flutterwave);
        paypalimg = findViewById(R.id.paypalimg);
        stripeimg = findViewById(R.id.stripeimg);
        razorpayimg = findViewById(R.id.razorpayimg);
        flutterwaveimg = findViewById(R.id.flutterwaveimg);
        upiimg = findViewById(R.id.upiimg);

        lnrAddmoney.setVisibility(View.GONE);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        one.setText(SharedHelper.getKey(context, "currency") + "10");
        two.setText(SharedHelper.getKey(context, "currency") + "20");
        three.setText(SharedHelper.getKey(context, "currency") + "50");

        backArrow.setOnClickListener(view -> onBackPressed());

        add_money.setOnClickListener(view -> {
            if (cardInfo != null) {
                addMoney(cardInfo);
            } else {
                AlertMessage.showMessage(context, "Alert", "Kindly select a card");
            }
        });

        lblPaymentChange.setOnClickListener(view -> {
            if (cardInfoArrayList.size() > 0) {
                showChooser();
            } else {
                gotoAddCard();
            }
        });

        lnrClose.setOnClickListener(view -> lnrAddmoney.setVisibility(View.GONE));

        add_fund_button.setOnClickListener(this);
        ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(context.getResources().getString(R.string.please_wait));

        session_token = SharedHelper.getKey(this, "access_token");
        runOnUiThread(() -> {
            getBalance();
            selectpaymentmethod();
            getCards(false);
        });
        String clientid = SharedHelper.getKey(context, "paypal_client_id");
        if (clientid != null) {
            config = new PayPalConfiguration()
                    .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                    .clientId(clientid);
            Intent intent = new Intent(this, PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            startService(intent);
        }
    }


    private void selectpaymentmethod() {
        if (SharedHelper.getKey(context, "CARD").equalsIgnoreCase("0")) {
            stripe.setVisibility(View.GONE);
            stripeimg.setVisibility(View.GONE);
            stripe.setChecked(false);
            paypal.setChecked(true);
        }


        if (SharedHelper.getKey(context, "paypal").equalsIgnoreCase("0")) {
            paypal.setVisibility(View.GONE);
            paypalimg.setVisibility(View.GONE);
            paypal.setChecked(false);
            flutterwave.setChecked(true);
        }

        if (SharedHelper.getKey(context, "rave").equalsIgnoreCase("0")) {
            flutterwave.setVisibility(View.GONE);
            flutterwaveimg.setVisibility(View.GONE);
            flutterwave.setChecked(false);
            upi.setChecked(true);
        }

        if (SharedHelper.getKey(context, "UPI").equalsIgnoreCase("0")) {
            upi.setVisibility(View.GONE);
            upiimg.setVisibility(View.GONE);
            upi.setChecked(false);
            razorpay.setChecked(true);
        }

        if (SharedHelper.getKey(context, "razor").equalsIgnoreCase("0")) {
            razorpay.setVisibility(View.GONE);
            razorpayimg.setVisibility(View.GONE);
            if (paymentgroup.getChildCount() == 0) {
                Toast.makeText(context, "No Payment Method Available !", Toast.LENGTH_SHORT).show();
                add_fund_button.setEnabled(false);
            } else if (!SharedHelper.getKey(context, "rave").equalsIgnoreCase("0")) {
                flutterwave.setChecked(true);
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void getBalance() {
        if ((customDialog != null))
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.getUserProfileUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    if (e != null) {
                        if (e instanceof TimeoutException) {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }
                        if (e instanceof NetworkErrorException) {
                            getBalance();
                        }
                        return;
                    }
                    if (response != null) {
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                currency = jsonObject.optString("currency");
                                balance_tv.setText(jsonObject.optString("currency") + jsonObject.optString("wallet_balance"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && customDialog.isShowing())
                                customDialog.dismiss();
                            if (response.getHeaders().code() == 401) {
                                refreshAccessToken("GET_BALANCE");
                            }
                        }
                    }
                });
    }

    private void refreshAccessToken(final String tag) {

        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, response -> {

            Utilities.print("SignUpResponse", response.toString());
            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
            if (tag.equalsIgnoreCase("GET_BALANCE")) {
                getBalance();
            } else if (tag.equalsIgnoreCase("GET_CARDS")) {
                getCards(loading);
            } else {
                addMoney(cardInfo);
            }
        }, error -> {
            NetworkResponse response = error.networkResponse;

            if (response != null && response.data != null) {
                SharedHelper.putKey(context, "loggedIn", context.getResources().getString(R.string.False));
                utils.GoToBeginActivity(ActivityWallet.this);
            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    refreshAccessToken(tag);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        UserApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (lnrAddmoney.getVisibility() == View.VISIBLE) {
            lnrAddmoney.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getCards(final boolean showLoading) {
        loading = showLoading;
        if (loading) {
            if (customDialog != null)
                customDialog.show();
        }
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result
                    if (response != null) {
                        if (showLoading) {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                getCards(showLoading);
                            }
                            return;
                        }
                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.getResult());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject cardObj = jsonArray.getJSONObject(i);
                                    CardInfo card_Info = new CardInfo();
                                    card_Info.setCardId(cardObj.optString("card_id"));
                                    card_Info.setCardType(cardObj.optString("brand"));
                                    card_Info.setLastFour(cardObj.optString("last_four"));
                                    cardInfoArrayList.add(card_Info);

                                    if (i == 0) {
                                        lblCardNumber.setText("XXXX-XXXX-XXXX-" + card_Info.getLastFour());
                                        cardInfo = card_Info;
                                    }
                                }

                                if (showLoading) {
                                    if (cardInfoArrayList.size() > 0) {
                                        showChooser();
                                    }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if (response.getHeaders().code() == 401) {
                                refreshAccessToken("GET_CARDS");
                            }
                        }
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fund_button:
                if (money_et.getText().toString().isEmpty()) {
                    update_amount = 0;
                    Toast.makeText(this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    int selectedid = paymentgroup.getCheckedRadioButtonId();
                    RadioButton rbtn = findViewById(R.id.stripe);
                    RadioButton rbtn2 = findViewById(R.id.paypal);
                    RadioButton rbtn3 = findViewById(R.id.flutterwave);
                    RadioButton rbtn4 = findViewById(R.id.upi);
                    RadioButton rbtn5 = findViewById(R.id.razorpay);

                    if (rbtn.getId() == selectedid) {
                        PaymentType = "CARD";
                        stripepayment();
                    } else if (rbtn2.getId() == selectedid) {
                        PaymentType = "PAYPAL";
                        paypalpayment();
                    } else if (rbtn3.getId() == selectedid) {
                        PaymentType = "CARD";
                        makeflutterwavePayment();
                    } else if (rbtn4.getId() == selectedid) {
                        PaymentType = "UPI";
                        allUPIPayments();
                    } else if (rbtn5.getId() == selectedid) {
                        PaymentType = "ONLINE";
                        startrazorpaypayment();
                    }

                }
                break;

            case R.id.one:
                one.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke_black));
                two.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke));
                three.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke));
                money_et.setText("10");
                break;
            case R.id.two:
                one.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke));
                two.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke_black));
                three.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke));
                money_et.setText("20");
                break;
            case R.id.three:
                one.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke));
                two.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke));
                three.setBackground(ContextCompat.getDrawable(context, R.drawable.border_stroke_black));
                money_et.setText("50");
                break;
        }
    }

    public void startrazorpaypayment() {

        Checkout checkout = new Checkout();
        checkout.setKeyID(SharedHelper.getKey(context, "razor_key"));
        checkout.setImage(R.mipmap.ic_launcher);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", "Add Wallet Amount");
            options.put("currency", "INR");
            options.put("prefill.email", SharedHelper.getKey(context, "email"));
            options.put("prefill.contact", SharedHelper.getKey(context, "mobile"));
            options.put("amount", Integer.parseInt(money_et.getText().toString()) * 100);
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    private void stripepayment() {
        update_amount = Double.parseDouble(money_et.getText().toString().trim());
        lnrAddmoney.setVisibility(View.VISIBLE);
    }

    public void makeflutterwavePayment() {
        String email = SharedHelper.getKey(context, "email");
        String fName = SharedHelper.getKey(context, "first_name");
        String lName = SharedHelper.getKey(context, "last_name");
        String txRef = email + " " + UUID.randomUUID().toString();
        String publicKey = SharedHelper.getKey(context, "rave_publicKey");
        String encryptionKey = SharedHelper.getKey(context, "rave_encryptionKey");



            /*
            Create instance of RavePayManager
             */

        Log.v("F_Currency", SharedHelper.getKey(context, "rave_currency"));
        double amount = Double.parseDouble(money_et.getText().toString());
        new RaveUiManager(this).setAmount(amount)
                .setCountry(SharedHelper.getKey(context, "rave_country"))
                .setCurrency("ZMW")
                .setEmail(email)
                .setfName(fName)
                .setlName(lName)
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(txRef)
//                .acceptFrancMobileMoneyPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .acceptBankTransferPayments(true)
                .acceptMpesaPayments(true)
                .acceptUssdPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .onStagingEnv(false)
                .allowSaveCardFeature(true)
                .initialize();
    }


    private void paypalpayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(money_et.getText().toString()), "USD",
                "Add Wallet Amount", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent2 = new Intent(this, PaymentActivity.class);
        intent2.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent2.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent2, PAYPAL_REQUEST_CODE);
    }

    private void allUPIPayments() {
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(SharedHelper.getKey(context, "UPI_key"))
                .setPayeeName(SharedHelper.getKey(context, "first_name"))
                .setTransactionId(generateString())
                .setTransactionRefId(generateString())
                .setDescription("Wallet Amount Add: " + money_et.getText().toString().trim())
                .setAmount(String.valueOf(Double.valueOf(money_et.getText().toString().trim())))
                .build();
        easyUpiPayment.startPayment();
        easyUpiPayment.setPaymentStatusListener(ActivityWallet.this);
    }


    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards(true);
                }
            }
        } else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                addMoney_new(cardInfo);
            }
        } else if (requestCode == 1225 && resultCode == Activity.RESULT_OK) {
            addMoney(cardInfo);
        } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
            assert data != null;
            Toast.makeText(this, "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
        } else if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    addMoney_new(cardInfo);
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("SetTextI18n")
    private void showChooser() {

        final String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(context.getResources().getString(R.string.add_money_using));
        builderSingle.setSingleChoiceItems(cardsList, selectedPosition, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.custom_tv);

        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card;
            card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            Log.e("Items clicked===>", "" + selectedPosition);
            cardInfo = cardInfoArrayList.get(selectedPosition);
            lblCardNumber.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        });
        builderSingle.setNeutralButton(
                "Manage",
                (dialog, which) -> {
                    startActivity(new Intent(context, Payment.class));
                    dialog.dismiss();
                });
        builderSingle.setNegativeButton("cancel",
                ((dialog, which) -> dialog.dismiss()));


        builderSingle.show();
    }

    @SuppressLint("SetTextI18n")
    private void addMoney(final CardInfo cardInfo) {
        if (customDialog != null)
            customDialog.show();


        JsonObject json = new JsonObject();
        json.addProperty("card_id", cardInfo.getCardId());
        json.addProperty("amount", money_et.getText().toString());


        Ion.with(this)
                .load(URLHelper.addCardUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result

                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    if (e != null) {
                        if (e instanceof TimeoutException) {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }
                        if (e instanceof NetworkErrorException) {
                            addMoney(cardInfo);
                        }
                        return;
                    }

                    Log.e("ION response", response.getResult() + " ");

                    if (response.getHeaders().code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.getResult());

                            JSONObject userObj = jsonObject.getJSONObject("user");
                            balance_tv.setText(currency + userObj.optString("wallet_balance"));
                            SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                            money_et.setText("");
                            lnrAddmoney.setVisibility(View.GONE);
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();

                            AlertMessage.showMessage(context, "Wallet", jsonObject.optString("message"));

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        try {
                            if (response.getHeaders() != null) {
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("ADD_MONEY");
                                }
                            }
                        } catch (Exception exception) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void addMoney_new(final CardInfo cardInfo) {
        if (customDialog != null)
            customDialog.show();


        JsonObject json = new JsonObject();
        json.addProperty("amount", money_et.getText().toString());
        json.addProperty("type", PaymentType);


        Ion.with(this)
                .load(URLHelper.addmoney)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback((e, response) -> {
                    // response contains both the headers and the string result

                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();

                    if (e != null) {
                        if (e instanceof TimeoutException) {
                            displayMessage(context.getResources().getString(R.string.please_try_again));
                        }
                        if (e instanceof NetworkErrorException) {
                            addMoney(cardInfo);
                        }
                        return;
                    }

                    Log.e("ION response", response.getResult() + " ");

                    if (response.getHeaders().code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.getResult());

                            JSONObject userObj = jsonObject.getJSONObject("user");
                            balance_tv.setText(currency + userObj.optString("wallet_balance"));
                            SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                            money_et.setText("");
                            lnrAddmoney.setVisibility(View.GONE);
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();

                            AlertMessage.showMessage(context, "Wallet", jsonObject.optString("message"));

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        try {
                            if (response.getHeaders() != null) {
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("ADD_MONEY");
                                }
                            }
                        } catch (Exception exception) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        addMoney_new(cardInfo);
    }

    @Override
    public void networkNotAvailable() {
    }

    @Override
    public void clientAuthenticationFailed(String s) {
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  " + s);
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true " + s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  ");
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel ");
    }


    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {

    }

    @Override
    public void onTransactionSuccess() {
        addMoney_new(cardInfo);
    }

    @Override
    public void onTransactionSubmitted() {

    }

    @Override
    public void onTransactionFailed() {

    }

    @Override
    public void onTransactionCancelled() {

    }


    //RazorPay Payment
    @Override
    public void onPaymentSuccess(String s) {
        addMoney_new(cardInfo);
        Checkout.clearUserData(this);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(context, "Some Error Occurred ! Please try again after sometime" + s, Toast.LENGTH_SHORT).show();
    }
}
