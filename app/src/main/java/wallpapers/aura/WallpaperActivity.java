package wallpapers.aura;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import wallpapers.aura.Detectors.ShakeDetector;
import wallpapers.aura.Listeners.OnSwipeTouchListener;
import wallpapers.aura.Services.ShakeService;
import wallpapers.aura.Singleton.SingletonInfo;

/**
 * Created by Aditya on 08/12/16.
 */
public class WallpaperActivity extends AppCompatActivity {

    static KenBurnsView activityImage;
    String allinfo[] = new String[6];
    Context context;
    File mFileCache;
    CardView cardGplus;
    TextView description;
    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    String filename;

    public void performDownloadAnimation(final View v, final boolean starting, int heightPixels) {
        int paddingheight = (int) (heightPixels * 0.1 / 2);

        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
        ValueAnimator animator;
        if (starting) {
            animator = ValueAnimator.ofInt(0, paddingheight);
        } else {
            animator = ValueAnimator.ofInt(paddingheight, 0);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                params.setMargins((Integer) valueAnimator.getAnimatedValue(), (Integer) valueAnimator.getAnimatedValue(), (Integer) valueAnimator.getAnimatedValue(), (Integer) valueAnimator.getAnimatedValue() * 3);
                activityImage.requestLayout();
            }
        });
        animator.setDuration(1000);
        animator.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {
                if (starting) {
                    activityImage.pause();
                }
            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                if (!starting) {
                    activityImage.resume();
                }
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {
        ((ViewGroup) activityImage.getParent()).removeView(activityImage);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
        super.onBackPressed();
    }

    public int getAnimationHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    //Calculating the Statusbar height
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //Calculating the Statusbar height
    public int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //Getting the Author Description from the main json
    public boolean getAuthorDescription() {
        boolean done = false;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET
                , context.getResources().getString(R.string.authorjsons)
                , null
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject author = (JSONObject) response
                                .get(i);
                        //Getting the author name and check if its there in the json
                        if (author.has(allinfo[1])) {

                            // if yes it does exist then just pass the author description value to the ralative layout
                            allinfo[3] = author.getString("author_description") + "\n\n Dont forget to follow him down below";
                            //Setting the Description
                            setDecription(allinfo[3]);

                            //Checking if the links are present, if present then we set the visibility and make it active
                            if (author.has("gplus")) {
                                allinfo[4] = author.getString("gplus");
                                setCard(cardGplus, allinfo[4]);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error", error.toString());
            }
        }

        );
        SingletonInfo.getInstance().addToRequestQueue(jsonArrayRequest);
        return done;
    }

    //Set the Desired card as active
    private void setCard(CardView card, String url) {
        card.setVisibility(View.VISIBLE);
        final String uriurl = url;
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriurl)));
            }
        });
    }

    // Get all the Strings from intents and jsons
    public void getStrings() {
        allinfo[0] = getIntent().getStringExtra("wall_name"); //Name of the Wall
        allinfo[1] = getIntent().getStringExtra("wall_author"); //Author of the Wall
        allinfo[2] = getIntent().getStringExtra("wall_image"); // Image link of the Wall
        getAuthorDescription();
    }

    //Setting the Author Description
    public void setDecription(String authorDescription) {
        description.setText(authorDescription);
    }

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ShakeDetector initialization
        Intent intent = new Intent(WallpaperActivity.this, ShakeService.class);
        startService(intent);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();

        //Setting the Context and Getting the Required Strings from there Desired Sources
        context = this;
        getStrings();

        //Setting the Content of the view to our layout
        setContentView(R.layout.activity_wallpaper);

        //Initializing all the Views
        activityImage = (KenBurnsView) findViewById(R.id.activityImage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView activityName = (TextView) findViewById(R.id.activityName);
        final TextView activityAuthor = (TextView) findViewById(R.id.activityauthor);
        description = (TextView) findViewById(R.id.activitydescription);
        cardGplus = (CardView) findViewById(R.id.activityfollow);
        nestedScrollView = (NestedScrollView) findViewById(R.id.main_layout);

        // Check if the version of Android is Lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
            lp.setMargins(0, getStatusBarHeight(), 0, 0);
        }

        //BottomSheet
        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(getNavigationBarHeight() + 100);

        //Toolbar
        toolbar.setNavigationIcon(R.drawable.close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Animations
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Fade slide = new Fade();
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        //Setting Wall and Author Name
        activityName.setText(allinfo[0].replace("_", " "));
        activityAuthor.setText(allinfo[1]);

        activityImage.setVisibility(View.INVISIBLE);

        //Getting the File Folder to save the images
        final File saveFolder = new File(Environment.getExternalStorageDirectory(), context
                .getString(R.string.app_name));

        Log.d("Location", "" + saveFolder);

        //Creating the Directory incase no directory is found
        if (saveFolder.exists()) {
            saveFolder.mkdirs();
        }

        //Getting the filename of the Wallpaper and changing its Name
        String name = allinfo[0].replace(" ", "_");
        //Getting the extension of the Wallpaper and checking if its png or jpg
        String extension = allinfo[2].toLowerCase(Locale.getDefault()).endsWith(".png")
                ? "png" : "jpg";

        //Storing the file name and extension in the file
        filename = String.format("%s.%s", name, extension);

        Log.d("File name", "" + filename);

        //Saving the file name with the file
        mFileCache = new File(saveFolder, filename);


        activityImage.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                //Picasso to load the Actionbar Collapsing Image
                Picasso.with(getBaseContext())
                        .load(allinfo[2])
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(activityImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                                    @Override
                                    public void onShake(int count) {
                                        if (!mFileCache.exists()) {
                                            performDownloadAnimation(activityImage, true, getAnimationHeight());
                                            new downloadResources().execute(new String[]{allinfo[2], filename, "", Integer.toString(getAnimationHeight())});
                                        } else {
                                            AlertDialog.Builder ad = new AlertDialog.Builder(context);
                                            ad.setTitle("Note :- ");
                                            ad.setMessage(getResources().getString(R.string.already_downloaded));
                                            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            ad.show();
                                        }
                                    }
                                });

                                // get the center for the clipping circle
                                int cx = (int) ((activityImage.getLeft() + activityImage.getRight()) * Math.random());
                                int cy = (int) ((activityImage.getTop() + activityImage.getBottom()) * Math.random());

                                // get the final radius for the clipping circle
                                int dx = Math.max(cx, activityImage.getWidth() - cx);
                                int dy = Math.max(cy, activityImage.getHeight() - cy);
                                final float finalRadius = (float) Math.hypot(dx, dy);


                                // Android native animator
                                if (android.os.Build.VERSION.SDK_INT >= 21) {
                                    Animator animator = ViewAnimationUtils.createCircularReveal(activityImage, cx, cy, 0, finalRadius);
                                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                    animator.setDuration(850);
                                    activityImage.setVisibility(View.VISIBLE);
                                    animator.start();
                                } else {
                                    activityImage.setVisibility(View.VISIBLE);
                                }

                                activityImage.setOnTouchListener(new OnSwipeTouchListener(WallpaperActivity.this) {
                                    @Override
                                    public void onSwipeBottom() {
                                        performDownloadAnimation(activityImage, true, getAnimationHeight());
                                        new downloadResources().execute(new String[]{allinfo[2], filename, "", Integer.toString(getAnimationHeight())});
                                    }
                                });

                                boolean tip = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("tips", true);

                                if (tip) {
                                    AlertDialog.Builder ad = new AlertDialog.Builder(context);
                                    ad.setTitle("Tip :-");
                                    ad.setMessage("If you need to download wallpapers, shake your phone !! :)");
                                    ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    ad.setNegativeButton("Dont Show again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("tips", false).apply();
                                        }
                                    });
                                    ad.show();
                                }


              /*          avLoadingIndicatorView.setVisibility(View.GONE);
                        Drawable image = activityImage.getDrawable();

                        Palette.from(Basic.drawableToBitmap(image)).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {

                                Palette.Swatch vibrant;
                                boolean gotback = false;
                                vibrant = palette.getVibrantSwatch();
                                if (vibrant != null && !gotback) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    }
                                    gotback = true;
                                }
                                vibrant = palette.getMutedSwatch();
                                if (vibrant != null && !gotback) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    }
                                    gotback = true;
                                }
                                vibrant = palette.getDarkVibrantSwatch();
                                if (vibrant != null && !gotback) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    }
                                    gotback = true;
                                }
                                vibrant = palette.getDarkMutedSwatch();
                                if (vibrant != null && !gotback) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    }
                                }
                            }
                        });*/
                            }

                            @Override
                            public void onError() {

                            }
                        });

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                Log.d("Image Attached for " + filename, "No");
            }
        });

        //Floating Button(Default)
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mFileCache.exists()) {
                    Log.e("Location", name + " Folder:-" + saveFolder);
                    new downloadResources().execute(new String[]{currentIntent.getStringExtra("wall_image"), name, ""});
                } else
                {
                    Snackbar.make(view, getResources().getString(R.string.already_downloaded), Snackbar.LENGTH_LONG).show();
                }
            }
        });*/
    }

    private class downloadResources extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;
        Boolean wallpaper = false;
        int animationheight;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Downloading....");
            // progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    performDownloadAnimation(activityImage, false, animationheight);
                }
            }, 1000);
            if (wallpaper) {
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(context);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(mFileCache.getPath(), options);
                    myWallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setMessage(context.getResources().getString(R.string.wallpaper_loaded));
                ad.show();
            }
        }

        @Override
        protected String doInBackground(String... sUrl) {

            //Animation Height
            animationheight = Integer.parseInt(sUrl[3]);

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            if (sUrl[2].equals("wall")) {
                wallpaper = true;
            } else {
                wallpaper = false;
            }
            try {
                URL url = new URL(sUrl[0]);
                //URLConnection connection = url.openConnection();
                File myDir = new File(Environment.getExternalStorageDirectory(), context
                        .getString(R.string.app_name));
                // create the directory if it doesnt exist
                if (!myDir.exists()) myDir.mkdirs();

                File outputFile = new File(myDir, sUrl[1]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);
                Log.d("Download Location", outputFile + "");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }
}
