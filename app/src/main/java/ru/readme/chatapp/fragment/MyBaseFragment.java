package ru.readme.chatapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.util.BackListener;

/**
 * Created by dima on 07.12.16.
 */

public class MyBaseFragment extends Fragment implements BackListener{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).setBackListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).setBackListener(this);
        }
    }



    public void doBack() {
        if(getActivity() instanceof MainActivity){
            ((MainActivity)getActivity()).doBack();
        }
    }
}
