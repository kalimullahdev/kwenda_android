package com.kwendaapp.rideo.Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.R;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CouponListAdapter extends ArrayAdapter<JSONObject> {

    int vg;

    public ArrayList<JSONObject> list;

    Context context;

    public CouponListAdapter(Context context, int vg, ArrayList<JSONObject> list) {

        super(context, vg, list);

        this.context = context;

        this.vg = vg;

        this.list = list;

    }

    @NotNull
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder") View itemView = inflater.inflate(vg, parent, false);

        TextView discount = itemView.findViewById(R.id.discount);

        TextView promo_code = itemView.findViewById(R.id.promo_code);

        TextView expires = itemView.findViewById(R.id.expiry);


        try {

            if (list.get(position).optJSONObject("promocode").optString("discount_type").equalsIgnoreCase("percent")) {
                discount.setText(list.get(position).optJSONObject("promocode").optString("discount")
                        + "% " + context.getString(R.string.off));
            } else {
                discount.setText(SharedHelper.getKey(context, "currency") + "" +
                        list.get(position).optJSONObject("promocode").optString("discount") + " " + context.getString(R.string.off));
            }

            promo_code.setText(context.getString(R.string.the_applied_coupon) + " " + list.get(position).optJSONObject("promocode").optString("promo_code") + ".");

            String date = list.get(position).optJSONObject("promocode").optString("expiration");

            expires.setText(context.getString(R.string.valid_until) + " " + getDate(date) + " " + getMonth(date) + " " + getYear(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemView;

    }


    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        @SuppressLint("SimpleDateFormat") String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

}
