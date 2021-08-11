package ru.readme.chatapp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.util.SettingsHelper;

/**
 * Created by dima on 16.01.17.
 */

public class SettingsFragment extends MyBaseFragment implements SeekBar.OnSeekBarChangeListener, CheckBox.OnCheckedChangeListener {

    private SettingsHelper settingsHelper;
    private SeekBar sbAvas, sbText, sbChaters, sbRooms;
    private CheckBox kbhide, push, autoenter, screenlock;
    private TextView txt_avs,txt_msg,txt_usr,txt_rooms;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, null);
        settingsHelper = new SettingsHelper(getActivity());
        sbAvas = (SeekBar) v.findViewById(R.id.sb_settings_avas);
        sbText = (SeekBar) v.findViewById(R.id.sb_settings_text);
        sbChaters = (SeekBar) v.findViewById(R.id.sb_settings_chaters);
        sbRooms = (SeekBar) v.findViewById(R.id.sb_settings_rooms);
        kbhide = (CheckBox) v.findViewById(R.id.kbhide);
        push = (CheckBox) v.findViewById(R.id.push);
        autoenter = (CheckBox) v.findViewById(R.id.autoenter);
        screenlock = (CheckBox) v.findViewById(R.id.screenlock);

        txt_avs = (TextView) v.findViewById(R.id.txt_avs);
        txt_msg = (TextView) v.findViewById(R.id.txt_msg);
        txt_usr = (TextView) v.findViewById(R.id.txt_usr);
        txt_rooms = (TextView) v.findViewById(R.id.txt_rooms);

      /*  txt_avs.setText(txt_avs.getText().toString()+ " ("+settingsHelper.getAvasSize()+")");
        txt_msg.setText(txt_msg.getText().toString()+ " ("+settingsHelper.getTextSize()+")");
        txt_usr.setText(txt_usr.getText().toString()+ " ("+settingsHelper.getChatersSize()+")");
        txt_rooms.setText(txt_rooms.getText().toString()+ " ("+settingsHelper.getRoomsSize()+")");
*/

        sbRooms.setProgress(settingsHelper.getRoomsSize());
        sbAvas.setProgress(settingsHelper.getAvasSize());
        sbChaters.setProgress(settingsHelper.getChatersSize());
        sbText.setProgress(settingsHelper.getTextSize());

        kbhide.setChecked(settingsHelper.getKBHide());
        push.setChecked(settingsHelper.getPush());
        autoenter.setChecked(settingsHelper.getAE());
        screenlock.setChecked(settingsHelper.getSL());

        sbText.setOnSeekBarChangeListener(this);
        sbChaters.setOnSeekBarChangeListener(this);
        sbAvas.setOnSeekBarChangeListener(this);
        sbRooms.setOnSeekBarChangeListener(this);

        kbhide.setOnCheckedChangeListener(this);
        push.setOnCheckedChangeListener(this);
        autoenter.setOnCheckedChangeListener(this);

        if (getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings);
        }

        return v;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress < 1) {
            seekBar.setProgress(1);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.sb_settings_avas) {
            settingsHelper.setAvasSize(seekBar.getProgress());
        } else if (seekBar.getId() == R.id.sb_settings_chaters) {
            settingsHelper.setChatersSize(seekBar.getProgress());
        } else if (seekBar.getId() == R.id.sb_settings_rooms) {
            settingsHelper.setRoomsSize(seekBar.getProgress());
        } else if (seekBar.getId() == R.id.sb_settings_text) {
            settingsHelper.setTextSize(seekBar.getProgress());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == kbhide){
    settingsHelper.setKBHide(isChecked);
        } else if(buttonView == push){
    settingsHelper.setPush(isChecked);
        } else if(buttonView == autoenter){
    settingsHelper.setAE(isChecked);
        }else if(buttonView == screenlock){
     settingsHelper.setSL(isChecked);
        }
    }
}
