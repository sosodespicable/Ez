package com.lxj.sample.letsplay.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxj.sample.letsplay.R;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class FragmentGuoKe extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guoke,container,false);
        return view;
    }
}
