package wallpapers.aura;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.transition.Fade;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.util.ArrayList;
import java.util.List;

import wallpapers.aura.Fragments.OneFragment;

/**
 * Created by Aditya on 08/12/16.
 */
public class CategoryActivity extends AppCompatActivity {

    private MaterialViewPager mViewPager;
    List<String> categoryLinks = new ArrayList<>();
    List<String> categoryNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryNames = (List<String>) getIntent().getSerializableExtra("activity_names");
        categoryLinks = (List<String>) getIntent().getSerializableExtra("activity_links");

        setContentView(R.layout.activity_category);

        //Animations
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Fade slide = new Fade();
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        // Initailizing all the varialbles
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        //Setting the toolbar as actionbar
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
        setTitle("");

        //Getting the viewpager toolbar and making the toolbar as the main toolbar
        toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            //Setting the toolbar once you get the toolbar from the material Viewpager
            setSupportActionBar(toolbar);
        }

        //Setting the Material Viewpager's adapter
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return OneFragment.newInstance(categoryLinks.get(position), true);
            }

            @Override
            public int getCount() {
                return categoryLinks.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return categoryNames.get(position);
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                return HeaderDesign.fromColorAndUrl(
                        getResources().getColor(R.color.colorPrimary),
                        "https://raw.githubusercontent.com/hiten1985/Backgrounds/master/Walls/Thumbs/Soul.png");

            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
    }
}
