package wallpapers.aura;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import wallpapers.aura.Fragments.OneFragment;
import wallpapers.aura.Util.Basic;

/**
 * Created by Aditya on 08/12/16.
 */
public class MainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    List<IDrawerItem> list = new ArrayList();

    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    Drawer drawer;
    LibsSupportFragment fragment;
    int IdenEnd;

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        preferences.edit().putInt("FirstRun", 1);

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        //Toolbar
        setSupportActionBar(toolbar);

        //WindowAnimations-Exit
        if (Build.VERSION.SDK_INT >= 21) {
            Explode slide = new Explode();
            slide.setDuration(1000);
            getWindow().setExitTransition(slide);
        }

        //Setup-ViewPagers
        setupViewPager(viewPager);

        //Setup-Tab layouts with Viewpager
        tabLayout.setupWithViewPager(viewPager);

        //Check Permission
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        //Create Drawer
        createDrawer();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        //Take List for Fragments
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            //Adding Fragments and there Titles to the Adapter
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        //Calling the Adapter and adding fragments to add the Viewpager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < SplashActivity.Main.categoryLinks.size(); i++) {
            adapter.addFragment(OneFragment.newInstance(SplashActivity.Main.categoryLinks.get(i), false), SplashActivity.Main.categoryNames.get(i));
        }

        //Setting the Adapter
        viewPager.setAdapter(adapter);
    }

    public void GatherDrawerItemsfromJson() {
        int IdenStart = 20;
        int i;
        for (i = 0; i < SplashActivity.drawer.size(); i++) {
            PrimaryDrawerItem primaryDrawerItem = new PrimaryDrawerItem().withName(SplashActivity.drawer.get(i).subName).withLevel(2).withIdentifier(IdenStart);
            IdenStart++;
            list.add(primaryDrawerItem);
        }
        IdenEnd = IdenStart + i + 1;

    }

    public void createDrawer() {

        //Adding Account Header
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_image)
                .withProfileImagesVisible(false)
                .build();

        GatherDrawerItemsfromJson();

        //Creating the Drawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Wallpapers").withIcon(R
                                .drawable.home).withIdentifier(2),
                        new ExpandableDrawerItem().withName("Categories").withSubItems(list),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Facebook").withIcon(R
                                .drawable.facebook).withIdentifier(5),
                        new SecondaryDrawerItem().withName("G+").withIcon(R
                                .drawable.gplus).withIdentifier(6),
                        new SecondaryDrawerItem().withName("Licenses").withIcon(R
                                .drawable.github).withIdentifier(8),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Contact").withIdentifier(10),
                        new SecondaryDrawerItem().withName("Rate").withIdentifier(11),
                        new SecondaryDrawerItem().withName("Share App").withIdentifier(7),
                        new SecondaryDrawerItem().withName("About").withIdentifier(9)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {

                            if (drawerItem.getIdentifier() >= 20 && drawerItem.getIdentifier() <= IdenEnd) {

                                Intent i = new Intent(MainActivity.this, CategoryActivity.class);
                                i.putExtra("activity_names", (ArrayList<String>) SplashActivity.drawer.get((int) (drawerItem.getIdentifier() - 20)).jsons.categoryNames);
                                Basic.l("");
                                i.putExtra("activity_links", (ArrayList<String>) SplashActivity.drawer.get((int) (drawerItem.getIdentifier() - 20)).jsons.categoryLinks);
                                startActivity(i);
                            } else {
                                switch ((int) drawerItem.getIdentifier()) {
                                    case 1:
                                        switchFragment("Themes", "ThemeHolderFragment");
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        switchFragment("Free Themes", "FreeThemeFragment");
                                        break;
                                    case 4:
                                        switchFragment("Paid Themes", "PaidThemeFragment");
                                        break;
                                    case 5:
                                        startActivity(new Intent(Intent.ACTION_VIEW
                                                , Uri.parse(getResources().getString(R.string.facebooklink))));
                                        break;
                                    case 6:
                                        startActivity(new Intent(Intent.ACTION_VIEW
                                                , Uri.parse(getResources().getString(R.string.googlepluslink))));
                                        break;
                                    case 7:
                                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                        sharingIntent.setType("text/plain");
                                        String shareBody = "Check out " + getResources().getString(R.string.app_name)
                                                + " by Aditya Gupta !\n\nDownload it here!: "
                                                + getResources().getString(R.string.store_link)
                                                + BuildConfig.APPLICATION_ID;
                                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                        startActivity(Intent.createChooser(sharingIntent
                                                , "Share Using :-"));
                                        break;

                                    case 8:
                                        switchFragmentToLicenses("Licenses", fragment);
                                        break;
                                    case 9:
                                        switchFragment("About", "AboutFragment");
                                        break;
                                    case 10:
                                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("mailto:" + getResources().getString(R.string.email_id)));
                                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                                getResources().getString(R.string.app_name) + "...");
                                        intent.putExtra(Intent.EXTRA_TEXT,
                                                "");
                                        startActivity(Intent.createChooser(intent,
                                                ("Talk Using:-")));
                                        break;
                                    case 11:
                                        startActivity(new Intent(Intent.ACTION_VIEW
                                                , Uri.parse(getResources().getString(R.string.store_link) + BuildConfig.APPLICATION_ID)));
                                        break;
                                    case 21:
                                        startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                                        break;
                                    case 22:

                                        break;
                                    case 23:

                                        break;
                                    case 24:

                                        break;
                                    case 25:
                                        preferences.edit().putString("SelectedCategory", "Misc").apply();
                                        drawer.updateBadge(21, new StringHolder(""));
                                        drawer.updateBadge(22, new StringHolder(""));
                                        drawer.updateBadge(23, new StringHolder(""));
                                        drawer.updateBadge(24, new StringHolder(""));
                                        drawer.updateBadge(25, new StringHolder("×"));
                                        drawer.closeDrawer();
                                        drawer.setSelection(2);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        return false;
                    }
                })
                .build();

        //Setting the Selection to select the wallpapers as the main Selection for Drawer
        drawer.setSelectionAtPosition(1);
    }

    private void switchFragment(String title, String fragment) {
        getSupportActionBar().setTitle(title);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.viewpager, Fragment
                .instantiate(this, "wallpapers.aura.Fragments." + fragment));
        tx.commit();
    }

    private void switchFragmentToLicenses(String title, LibsSupportFragment fragment) {
        getSupportActionBar().setTitle(title);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.viewpager, fragment);
        tx.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
