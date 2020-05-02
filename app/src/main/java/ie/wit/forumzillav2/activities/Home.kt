package ie.wit.forumzillav2.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ie.wit.forumzillav2.R
import ie.wit.forumzillav2.fragments.AboutFragment
import ie.wit.forumzillav2.fragments.CreatePostFragment
import ie.wit.forumzillav2.fragments.HomeFragment
import ie.wit.forumzillav2.fragments.PostsListFragment
import ie.wit.forumzillav2.main.ForumApp
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.startActivity

class Home : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction
    lateinit var app: ForumApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)
        app = application as ForumApp

        navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.getHeaderView(0).nav_header_email.text = app.auth.currentUser?.email

        ft = supportFragmentManager.beginTransaction()

        val fragment = HomeFragment.newInstance() //after signin <- goes to HomeFragment
        ft.replace(R.id.homeFrame, fragment)
        ft.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home ->
                navigateTo(HomeFragment.newInstance())
            R.id.nav_news ->
                navigateTo(PostsListFragment.newInstance())
            R.id.nav_create_post ->
                navigateTo(CreatePostFragment.newInstance())
            R.id.nav_about ->
                navigateTo(AboutFragment.newInstance())
            R.id.nav_logout -> signOut()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun signOut() {
        app.auth.signOut()
        startActivity<LoginActivity>() //start login after signout
        finish()
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

}