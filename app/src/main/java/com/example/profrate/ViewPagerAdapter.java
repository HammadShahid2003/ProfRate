package com.example.profrate;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.profrate.ViewsFragments.ProfessorFragment;
import com.example.profrate.ViewsFragments.University_Fragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    Context context;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.context=fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProfessorFragment();
            case 1:
                return new ProfessorFragment();
            case 2:
                return new University_Fragment();
            default:
                return new University_Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}
