package com.lxj.sample.letsplay.Main;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.lxj.sample.letsplay.About.AboutActivity;
import com.lxj.sample.letsplay.Idea.IdeaActivity;
import com.lxj.sample.letsplay.Login.LoginActiivty;
import com.lxj.sample.letsplay.MyMessage.MyMessageActivity;
import com.lxj.sample.letsplay.MyViews.CustomImageView;
import com.lxj.sample.letsplay.R;
import com.lxj.sample.letsplay.Search.SearchActivity;
import com.lxj.sample.letsplay.Setting.SettingActivity;
import com.lxj.sample.letsplay.Vedio.VedioActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import qiu.niorgai.StatusBarCompat;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private TabLayout mTabLayout;
    private MyViewPagerAdapter myViewPagerAdapter;
    private List<Fragment> fragments;
    private List<String> titles;
    private FragmentLatest fragmentLatest;
    private FragmentTheme fragmentTheme;
    private FragmentMyFriends fragmentMyFriends;
    private FragmentWakeUnderstand fragmentWakeUnderstand;
    private ViewPager mViewPager;
    private Intent intent;
    private CustomImageView nav_header_image;
    private LayoutInflater inflater;
    private CoordinatorLayout coordinatorLayout;
    private BottomBar bottomBar;
    private FragmentGuoKe fragmentGuoKe;
    private Toolbar toolbar;

    private Window window;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_CoordinatorLayout);
        createBottomBar(savedInstanceState);
        initFragment();
        toolbar.setBackgroundColor(getResources().getColor(R.color.daily));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        inflater = LayoutInflater.from(this);
        View headerView = inflater.inflate(R.layout.nav_header_main,navigationView,false);
        nav_header_image = (CustomImageView) headerView.findViewById(R.id.nav_header_circleImageView);
        nav_header_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, LoginActiivty.class);
                startActivity(intent);
            }
        });
        navigationView.addHeaderView(headerView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
//            case R.id.action_search:
//                //TODO
//                break;
            case R.id.select_date:

        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Intent intent;

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_message) {

            intent = new Intent(MainActivity.this,MyMessageActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_vedio) {
            intent = new Intent(MainActivity.this, VedioActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_knowledge) {
            intent = new Intent(MainActivity.this, IdeaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {
            intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //初始化TabLayout+ViewPager
    private void initFragment(){

//        mTabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        mViewPager.setOffscreenPageLimit(3);

        //初始化Fragment
        fragmentWakeUnderstand = new FragmentWakeUnderstand();
        fragmentLatest = new FragmentLatest();
        fragmentTheme = new FragmentTheme();
        fragmentMyFriends = new FragmentMyFriends();
        fragmentGuoKe = new FragmentGuoKe();

        fragments = new ArrayList<Fragment>();
        fragments.add(fragmentLatest);
//        fragments.add(fragmentTheme);
        fragments.add(fragmentWakeUnderstand);
        fragments.add(fragmentMyFriends);
        fragments.add(fragmentGuoKe);

        titles = new ArrayList<String>();
        titles.add("知乎日报");
        titles.add("小音箱");
        titles.add("热门消息");
        titles.add("果壳精选");

//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),fragments,titles);
        mViewPager.setAdapter(myViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.selectTabAtPosition(position,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void createBottomBar(Bundle saveInstanceState){
        bottomBar = BottomBar.attachShy(coordinatorLayout,mViewPager,saveInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId){
                    case R.id.bb_menu_zhihu:
                        mViewPager.setCurrentItem(0);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.daily));
                        StatusBarCompat.setStatusBarColor(MainActivity.this, Color.parseColor("#BDBDBD"));
                        break;
                    case R.id.bb_menu_theme_news:
                        mViewPager.setCurrentItem(1);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.theme));
                        StatusBarCompat.setStatusBarColor(MainActivity.this, Color.parseColor("#37474F"));
                        break;
                    case R.id.bb_menu_hotest:
                        mViewPager.setCurrentItem(2);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.hot));
                        StatusBarCompat.setStatusBarColor(MainActivity.this, Color.parseColor("#F44336"));
                        break;
                    case R.id.bb_menu_guoke:
                        mViewPager.setCurrentItem(3);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.guokr));
                        StatusBarCompat.setStatusBarColor(MainActivity.this, Color.parseColor("#9C27B0"));
                        break;
                }
            }
            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
        bottomBar.mapColorForTab(0,"#BDBDBD");
        bottomBar.mapColorForTab(1,"#37474F");
        bottomBar.mapColorForTab(2,"#F44336");
        bottomBar.mapColorForTab(3,"#9C27B0");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }
}
