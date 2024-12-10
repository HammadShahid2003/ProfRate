package com.example.profrate;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cloudinary.android.MediaManager;
import com.example.profrate.ViewsFragments.HomeFragment;
import com.example.profrate.ViewsFragments.PostsFragment;
import com.example.profrate.ViewsFragments.ProfessorFragment;
import com.example.profrate.ViewsFragments.ProfileFragment;
import com.example.profrate.ViewsFragments.University_Fragment;
import com.example.profrate.model.Keys;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        if (savedInstanceState == null) { // To avoid reloading on configuration changes
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new HomeFragment())
                    .commit();
        }



//        TabLayout tabLayout = findViewById(R.id.tab_layout);
//      ViewPager2 viewPager = findViewById(R.id.view_pager);
//
//
//        viewPager.setAdapter(new ViewPagerAdapter(this));
//
//        // Set up the TabLayout with the ViewPager using TabLayoutMediator
//        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                switch (position) {
//                    case 0:
//                        tab.setIcon(R.drawable.ic_professor);
//                        tab.setText("Professors");
//                        break;
//                    case 1:
//                        tab.setIcon(R.drawable.ic_university);
//                        tab.setText("University");
//                        break;
//                    case 2:
//                        tab.setIcon(R.drawable.ic_profile);
//                        tab.setText("Profile");
//                        break;
//                }
//            }
//        }).attach();
        Map<String, Object> config = new HashMap();
        config.put("cloud_name", Keys.cloudName);
        config.put("api_key", Keys.apiKey);
        config.put("api_secret", Keys.apiSecret);
        MediaManager.init(MainActivity.this, config);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        FrameLayout frameLayout=findViewById(R.id.frameLayout);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // Determine which fragment to display based on selected menu item
                int selectedid=item.getItemId() ;

                    if(selectedid== R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    }
                    else if(selectedid== R.id.nav_professor) {
                        selectedFragment = new ProfessorFragment();
                    }
                   else if(selectedid== R.id.nav_university) {
                        selectedFragment = new University_Fragment();
                    }
                    else if(selectedid== R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                    }
                    else if(selectedid== R.id.nav_post) {
                        selectedFragment = new PostsFragment();
                    }



                // Replace the existing fragment with the selected one
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }
}