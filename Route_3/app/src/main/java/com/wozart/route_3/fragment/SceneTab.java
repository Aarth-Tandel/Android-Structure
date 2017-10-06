package com.wozart.route_3.fragment;

/**
 * Created by wozart on 05/10/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wozart.route_3.R;

public class SceneTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scene_fragment, container, false);
    }
}
