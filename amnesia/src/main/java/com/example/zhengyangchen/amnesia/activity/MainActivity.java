package com.example.zhengyangchen.amnesia.activity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.zhengyangchen.amnesia.R;
import com.example.zhengyangchen.amnesia.bean.Memo;
import com.example.zhengyangchen.amnesia.fragment.AlarmsFragment;
import com.example.zhengyangchen.amnesia.fragment.CalendarFragment;
import com.example.zhengyangchen.amnesia.fragment.MemoFragment;
import com.example.zhengyangchen.amnesia.fragment.MineFragment;
import com.example.zhengyangchen.amnesia.fragment.addMemoDialogFragment;
import com.example.zhengyangchen.amnesia.service.registerScreenActionReceiverService;
import com.example.zhengyangchen.amnesia.util.OnNotifyDataSetChangedListener;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity implements MaterialTabListener,OnNotifyDataSetChangedListener {
    //tab标题
    private String[] titles = {"闹钟", "备忘", "日历", "我"};
    //fragment的集合
    private List<Fragment> fragmentList;
    private MaterialTabHost materialTabHost;
    private ViewPagerAdapter viewPagerAdapter;
    private Resources res;
    private ViewPager viewPager;
    private CalendarFragment mCalendarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerScreenActionReceiverService.onStartService(MainActivity.this);
        res = this.getResources();
        //初始化view
        initView();
    }

    private void initView() {
        //TabLayout tabLayout = (TabLayout) findViewById(R.id.id_TabLayout);
        materialTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //将fragment添加集合中去
        addFragmentToFragmentList();

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                materialTabHost.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
            materialTabHost.addTab(
                    materialTabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this)
            );
        }

//        //给viewpager设置adapter
//        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return fragmentList.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return titles.length;
//            }
//
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return titles[position];
//            }
//        });

        //tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * 将Fragment添加到集合中去
     */
    private void addFragmentToFragmentList() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new AlarmsFragment());
        MemoFragment memoFragment = new MemoFragment();
        memoFragment.setOnMemoNotifyDataSetChangedListener(this);
        fragmentList.add(memoFragment);
        mCalendarFragment = new CalendarFragment();
        fragmentList.add(mCalendarFragment);
        fragmentList.add(new MineFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    //将新添加的memo对象传递到card中去
    @Override
    public void notifyDataChangeListener(Memo memo) {
        mCalendarFragment.CreateCard(memo);
        mCalendarFragment.notifyDataSetChanged();
    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            return fragmentList.get(num);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "tab 1";
                case 1:
                    return "tab 2";
                case 2:
                    return "tab 3";
                default:
                    return null;
            }
        }
    }

    private Drawable getIcon(int position) {
        switch (position) {
            case 0:
                return res.getDrawable(R.drawable.ic_menu_recent_history);
            case 1:
                return res.getDrawable(R.drawable.ic_menu_edit);
            case 2:
                return res.getDrawable(R.drawable.ic_menu_month);
            case 3:
                return res.getDrawable(R.drawable.ic_menu_friendslist);
        }
        return null;
    }
}
