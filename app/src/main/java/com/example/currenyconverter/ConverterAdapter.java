package com.example.currenyconverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * CurrencyConverter's RecyclerView.Adapter
 */
public class ConverterAdapter extends RecyclerView.Adapter<ConverterAdapter.ConverterViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    public List<Currency> currencyList;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Context mContext;

    /**
     * Constructor
     *
     * @param context
     * @param currencyList
     */
    public ConverterAdapter(Context context, List<Currency> currencyList) {
        this.mContext = context;
        this.currencyList = currencyList;
        this.sharedPref = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();
    }

    /**
     * ViewHolder implementation
     */
    public class ConverterViewHolder extends RecyclerView.ViewHolder {
        private ImageView countryFlag;
        private TextView countryName;
        private EditText currencyValue;
        View rowView;

        public ConverterViewHolder(View view) {
            super(view);
            rowView = itemView;
            countryFlag = (ImageView) view.findViewById(R.id.country_flag);
            countryName = (TextView) view.findViewById(R.id.country_name);
            currencyValue = (EditText) view.findViewById(R.id.currency_value);
        }
    }



    @Override
    public ConverterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_list_row, parent, false);

        return new ConverterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConverterViewHolder holder, int position) {
        Currency currency = currencyList.get(position);
        holder.countryFlag.setImageResource(currency.getCountryFlag());
        holder.countryName.setText(currency.getCurrencyName());
        holder.currencyValue.setText(currency.getCurrencyValue());
        if (position == 0) {
            holder.currencyValue.addTextChangedListener(textWatcher);
        }
        holder.currencyValue.setEnabled(currency.isCurrencyEditable());
        holder.currencyValue.setSelection(holder.currencyValue.getText().length());

    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    @Override
    public void onRowMoved(ConverterViewHolder myViewHolder, ConverterViewHolder myViewHolder1, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(currencyList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(currencyList, i, i - 1);
            }
        }

        if (toPosition == 0) {
            myViewHolder.currencyValue.addTextChangedListener(textWatcher);
            myViewHolder.currencyValue.setEnabled(true);
            myViewHolder1.currencyValue.removeTextChangedListener(textWatcher);
            myViewHolder1.currencyValue.setEnabled(false);

            editor.putString(Constants.PREFERENCE_CURRENCY, currencyList.get(0).getCurrencyName());
            editor.putString(Constants.PREFERENCE_VALUE, currencyList.get(0).getCurrencyValue());
            editor.commit();
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(ConverterViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(ConverterViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
            currencyList.get(0).setCurrencyValue(charSequence.toString());
            editor.putString(Constants.PREFERENCE_CURRENCY, currencyList.get(0).getCurrencyName());
            editor.putString(Constants.PREFERENCE_VALUE, charSequence.toString());
            editor.commit();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
