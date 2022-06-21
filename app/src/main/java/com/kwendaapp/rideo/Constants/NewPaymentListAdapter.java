package com.kwendaapp.rideo.Constants;


import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.List;

public class NewPaymentListAdapter extends ArrayAdapter<CardDetails> {

    List<CardDetails> list;

    Context context;

    Activity activity;

    public NewPaymentListAdapter(Context context, List<CardDetails> list, Activity activity) {
        super(context, R.layout.payment_list_item, list);
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NotNull
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("ViewHolder") View itemView = inflater.inflate(R.layout.payment_list_item, parent, false);

        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);

        TextView cardNumber = itemView.findViewById(R.id.cardNumber);


        try {

            if (list.get(position).getBrand().equalsIgnoreCase("MASTER")) {
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
