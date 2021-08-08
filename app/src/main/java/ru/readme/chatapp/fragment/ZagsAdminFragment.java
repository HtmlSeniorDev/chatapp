package ru.readme.chatapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.readme.chatapp.R;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.helper.ZagsAdminHelper;

/**
 * Created by dima on 05.01.17.
 */

public class ZagsAdminFragment extends MyBaseFragment implements ZagsAdminHelper.OnZagsAdminHelperListener, View.OnClickListener {

    private WaitDialog waitDialog;
    private ZagsAdminHelper helper;
    private EditText edPrice;
    private Button btSave;
    private int price;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.zags_admin, null);
        waitDialog = new WaitDialog(getActivity());
        helper = new ZagsAdminHelper(getActivity(), this);
        edPrice = (EditText) v.findViewById(R.id.ed_zags_admin_price);
        btSave = (Button) v.findViewById(R.id.bt_zags_admin_save);
        btSave.setOnClickListener(this);
        if (savedInstanceState != null && savedInstanceState.containsKey("price")) {
            price = savedInstanceState.getInt("price", 0);
        }
        if (price > 0) {
            edPrice.setText("" + (price / 100));
        } else {
            waitDialog.show();
            helper.getPrice();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.virt_zags);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("price", price);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onGetPrice(boolean done, int price) {
        waitDialog.hide();
        if (!done) {
            Toast.makeText(getActivity(), R.string.error_get_price, Toast.LENGTH_LONG).show();
        } else {
            edPrice.setText("" + (price / 100));
            this.price = price;
        }
    }

    @Override
    public void onSetPrice(boolean done, int price) {
        waitDialog.hide();
        if (!done) {
            Toast.makeText(getActivity(), R.string.error_set_price, Toast.LENGTH_LONG).show();
        } else {
            this.price = price;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_zags_admin_save) {
            String pr = edPrice.getText().toString();
            int prc = 0;
            try {
                prc = Integer.parseInt(pr)*100;
            } catch (Exception e) {
            }
            waitDialog.show();
            helper.setPrice(prc);
        }
    }
}
