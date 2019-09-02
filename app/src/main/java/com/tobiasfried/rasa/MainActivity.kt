package com.tobiasfried.rasa

import android.content.Intent

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tobiasfried.rasa.messaging.MessageService
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

import android.os.Bundle
import androidx.databinding.DataBindingUtil

import com.tobiasfried.rasa.brews.BrewsFragment
import com.tobiasfried.rasa.databinding.ActivityMainBinding
import com.tobiasfried.rasa.entry.EntryActivity
import com.tobiasfried.rasa.history.HistoryFragment
import com.tobiasfried.rasa.recipes.RecipeFragment
import com.tobiasfried.rasa.settings.SettingsFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    //private FragmentTabHost mTabHost;
    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Get token
        MessageService.getInstanceId()

        // Set ViewPager and FragmentAdapter
        // TODO refactor to Navigation component
        val adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> BrewsFragment()
                    1 -> HistoryFragment()
                    2 -> RecipeFragment()
                    else -> SettingsFragment()
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.title_brews)
                    1 -> getString(R.string.title_history)
                    2 -> getString(R.string.title_recipes)
                    else -> getString(R.string.title_settings)
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }

        // Set the Adapter onto the ViewPager
        binding.viewpager.adapter = adapter

        // Set the TabLayout to the ViewPager
        val tabLayout = findViewById<TabLayout>(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(binding.viewpager)

        // Set FAB
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        floatingActionButton.setOnClickListener { v ->
            val intent = Intent(this@MainActivity, EntryActivity::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            mAuth.signInAnonymously().addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.d("signInAnonymously:success")
                    user = currentUser
                    // TODO UI updates if needed
                    // TODO Save UID in sharedpreferences
                } else {
                    Timber.w(it.exception, "signInAnonymously:failure")
                    // UI updates if needed
                }
            }
        } else {
            // TODO Handle user already logged in
            Timber.i("Logged in as ${currentUser.displayName}: ${currentUser.email}")
        }

    }

}
