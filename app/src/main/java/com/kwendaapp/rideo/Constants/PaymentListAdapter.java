package com.kwendaapp.rideo.Constants;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kwendaapp.rideo.Models.CardDetails;
import com.kwendaapp.rideo.R;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class PaymentListAdapter extends ArrayAdapter<CardDetails> {

    int vg;

    public ArrayList<CardDetails> list;

    Context context;

    public PaymentListAdapter(Context context, int vg, ArrayList<CardDetails> list) {

        super(context, vg, list);

        this.context = context;

        this.vg = vg;

        this.list = list;

    }

    @SuppressLint("SetTextI18n")
    @NotNull
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder") View itemView = inflater.inflate(vg, parent, false);

        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);

        TextView cardNumber = itemView.findViewById(R.id.cardNumber);

        ImageView tickImg = itemView.findViewById(R.id.img_tick);

        try {
            if (list.get(position).getBrand().equalsIgnoreCase("MASTERCARD")) {
                paymentTypeImg.setImageResource(R.drawable.credit_card);
            } else if (list.get(position).getBrand().equalsIgnoreCase("MASTRO")) {
                paymentTypeImg.setImageResource(R.drawable.visa_payment_icon);
            } else if (list.get(position).getBrand().equalsIgnoreCase("Visa")) {
                paymentTypeImg.setImageResource(R.drawable.visa);
            }
            cardNumber.setText("xxxx - xxxx - xxxx - " + list.get(position).getLast_four());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }
}
