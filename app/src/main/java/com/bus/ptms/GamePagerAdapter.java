package com.bus.ptms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by xeonyan on 9/7/2016.
 */
public class GamePagerAdapter extends FragmentPagerAdapter {
    Context context;
    int questionCount;
    public GamePagerAdapter(FragmentManager fm,Context context,int questionCount) {
        super(fm);
        this.context = context;
        this.questionCount = questionCount;
    }

    @Override
    public Fragment getItem(int position) {
        // init fragments
        QuestionFrag frag = new QuestionFrag();
        Bundle bundle = new Bundle();
        bundle.putInt("pos",position);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return questionCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position + 1) +" / " + questionCount;
    }
    // show page title
}
