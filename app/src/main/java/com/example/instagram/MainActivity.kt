package com.example.instagram

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.instagram.Fragments.AccountFragment
import com.example.instagram.Fragments.AlertFragment
import com.example.instagram.Fragments.HomeFragment
import com.example.instagram.Fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        loadFragment(HomeFragment())

        val bottomAppBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomAppBar.setOnNavigationItemSelectedListener { menuItem ->
            when{
                menuItem.itemId == R.id.home -> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true

                }
                menuItem.itemId == R.id.search -> {
                    loadFragment(SearchFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                menuItem.itemId == R.id.alert -> {
                    loadFragment(AlertFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                menuItem.itemId == R.id.account -> {
                    loadFragment(AccountFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().also { fragmentTransaction ->
            fragmentTransaction.replace(R.id.fragment_holder, fragment)
            fragmentTransaction.commit()
        }
    }
}