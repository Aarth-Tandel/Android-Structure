package com.wozart.route_3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wozart.route_3.favouritesTab.FavTab;
import com.wozart.route_3.fragment.HomeTab;
import com.wozart.route_3.fragment.SceneTab;

/**
 * Created by wozart on 05/10/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FavTab Favourites = new FavTab();
                return Favourites;
            case 1:
                HomeTab Home = new HomeTab();
                return Home;
            case 2:
                SceneTab Scenes = new SceneTab();
                return Scenes;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
