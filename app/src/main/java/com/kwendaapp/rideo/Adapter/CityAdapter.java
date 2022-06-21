package com.kwendaapp.rideo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwendaapp.rideo.Models.City;
import com.kwendaapp.rideo.R;

import java.util.ArrayList;

import utils.ApplyFont;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.SingleItemRowHolder> {


    public void setItemsList(ArrayList<City> itemsList) {
        this.itemsList = itemsList;
    }

    private ArrayList<City> itemsList;
    private final Context mContext;
    private MyClickListener myClickListener;

    public CityAdapter(Context context, ArrayList<City> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;

    }


    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.languageitem, viewGroup, false);
        return new SingleItemRowHolder(v);

    }


    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {

        holder.languageName.setText(itemsList.get(i).getCname());

    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView languageName;


        public SingleItemRowHolder(View view) {
            super(view);

            this.languageName = view.findViewById(R.id.button_english);

            ApplyFont.applyBold(mContext, view.findViewById(R.id.button_english));
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return itemsList == null ? 0 : itemsList.size();
    }

}
