package com.example.tms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.tms.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {


    private ActivityDashboardBinding binding;
    CheckInternet internet = new CheckInternet();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        internet.InternetConnectivityChecker(this);
        internet.start();
        replaceFragment(new HomeFragment());
        getSupportActionBar().hide();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {


            switch (item.getItemId()){
                case R.id.home:

                    replaceFragment(new HomeFragment());
                    break;
                case R.id.academics:

                    replaceFragment(new AcademicsFragment());
                    break;
                case R.id.account:

                    replaceFragment(new AccountFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        internet.stop();
    }
}
