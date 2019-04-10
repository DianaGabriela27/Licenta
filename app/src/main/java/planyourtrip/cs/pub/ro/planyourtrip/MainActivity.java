package planyourtrip.cs.pub.ro.planyourtrip;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private int mPreviousMenuItemId;
    private SharedPreferences sharedPreferences;
    private ImageView profileImage;

    private ProfileImageClickListener profileImageClickListener = new ProfileImageClickListener();
    private class ProfileImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, Constants.RESULT_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            profileImage.setImageURI(selectedImage);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.USER_PHOTO, selectedImage.toString());
            editor.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreviousMenuItemId = R.id.nav_destinations;

        /*Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = CityFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.inc, fragment).commit();*/

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String email = sharedPreferences.getString(Constants.USER_EMAIL, getString(R.string.app_name));

        navigationView.setNavigationItemSelectedListener(this);

        // Get reference to the navigation view header and email textview
        View navigationHeader = navigationView.getHeaderView(0);
        TextView emailTextView = navigationHeader.findViewById(R.id.email);
        emailTextView.setText(email);

        profileImage = navigationHeader.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(profileImageClickListener);

        if(sharedPreferences.getString(Constants.USER_PHOTO, null) != null) {
            profileImage.setImageURI(Uri.parse(sharedPreferences.getString(Constants.USER_PHOTO, getString(R.string.app_name))));
        } else {
            System.out.println("DSDJASDKASJDLKASJDLASKJASKJDLASJDL");
            Picasso.with(MainActivity.this).load("null").placeholder(R.drawable.icon_profile)
                    .error(R.drawable.icon_profile).into(profileImage);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == mPreviousMenuItemId) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        int id = item.getItemId();
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {
//            case R.id.nav_travel:
//                fragment = TravelFragment.newInstance();
//                break;
//
//            case R.id.nav_mytrips:
//                fragment = MyTripsFragment.newInstance();
//                break;
//
//            case R.id.nav_city:
//                fragment = CityFragment.newInstance();
//                break;
//
//            case R.id.nav_utility:
//                fragment = UtilitiesFragment.newInstance();
//                break;
//
            case R.id.nav_about_us:
                fragment = AboutUsFragment.newInstance();
                break;

            case R.id.nav_signout: {

                //set AlertDialog before signout
                ContextThemeWrapper crt = new ContextThemeWrapper(this, R.style.AlertDialog);
                AlertDialog.Builder builder = new AlertDialog.Builder(crt);
                builder.setMessage(R.string.signout_message)
                        .setPositiveButton(R.string.positive_button,
                                (dialog, which) -> {
                                    sharedPreferences
                                            .edit()
                                            .putString(Constants.USER_TOKEN, null)
                                            .apply();
                                    Intent i = new Intent(getApplicationContext(), LoginSignUpActivity.class);
                                    startActivity(i);
                                    finish();
                                })
                        .setNegativeButton(android.R.string.cancel,
                                (dialog, which) -> {

                                });
                builder.create().show();
                break;
            }
//
//            case R.id.nav_myfriends :
//                fragment = MyFriendsFragment.newInstance();
//                break;
            case R.id.nav_settings :
                fragment = SettingsFragment.newInstance();
                break;
        }
//
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.inc, fragment).commit();
        }
//
        drawerLayout.closeDrawer(GravityCompat.START);
        mPreviousMenuItemId = item.getItemId();
        return true;
    }

    private void fillNavigationView(String emailId, String imageURL) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get reference to the navigation view header and email textview
        View navigationHeader = navigationView.getHeaderView(0);
        TextView emailTextView = navigationHeader.findViewById(R.id.email);
        emailTextView.setText(emailId);

        ImageView imageView = navigationHeader.findViewById(R.id.profile_image);
        Picasso.with(MainActivity.this).load(imageURL).placeholder(R.drawable.icon_profile)
                .error(R.drawable.icon_profile).into(imageView);
    }
}
