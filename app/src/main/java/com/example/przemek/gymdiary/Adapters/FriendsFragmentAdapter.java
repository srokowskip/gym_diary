package com.example.przemek.gymdiary.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.przemek.gymdiary.Fragments.FriendsFragment;
import com.example.przemek.gymdiary.Fragments.LiveTrainingExerciseFragment;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragmentAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public FriendsFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void putFragment(int key, Fragment fragment) {

        registeredFragments.put(key, fragment);

    }
    public boolean arrayContains(int key){

        return registeredFragments.get(key) != null;

    }

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt("position", position);
        Fragment frag = FriendsFragment.newInstance();
        frag.setArguments(b);
        return frag;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public List<LiveTrainingExerciseFragment> getRegisteredFragments() {

        List<LiveTrainingExerciseFragment> listOfFragments = new ArrayList<>();

        for (int i = 0; i < mNumOfTabs; i++) {
            listOfFragments.add((LiveTrainingExerciseFragment) getRegisteredFragment(i));
        }
        return listOfFragments;
    }

}
