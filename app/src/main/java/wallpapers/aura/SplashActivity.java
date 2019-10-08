package wallpapers.aura;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wallpapers.aura.Data.Category;
import wallpapers.aura.Data.DrawerCategory;
import wallpapers.aura.Services.Downloader;
import wallpapers.aura.Util.Basic;
import wallpapers.aura.Util.ReadJsonFile;

/**
 * Created by Aditya on 08/12/16.
 */

public class SplashActivity extends Activity {
    TextView textView;
    static Category Main = new Category();
    static List<DrawerCategory> drawer = new ArrayList<>();
    int itemsmapped = 1;
    boolean changed = false;

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.toString().contains("arg1=1")) {
                downloadDrawer();
            }
            if (msg.toString().contains("arg1=" + itemsmapped)) {
                StartDownloadoF(getResources().getString(R.string.authorjsons), "authors_info.json");
                StartDownloadoF(getResources().getString(R.string.wallpapercategoriesjsons), "Main.json");
            }
            if (msg.toString().contains("arg1=" + (itemsmapped + 2))) {
                if (!changed)
                    changetoMain();
            }
        }
    };

    public void changetoMain() {
        MapCategories();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 500);
    }

    public void downloadDrawer() {
        String[] checkerCommands = {getApplicationContext().getCacheDir() + "/alljsons/drawer.json", "drawer", "jsons"};
        final Map<String, String> jsonMap = ReadJsonFile.main(checkerCommands);
        for (String key : jsonMap.keySet()) {
            StartDownloadoF(jsonMap.get(key), key + ".json");
            itemsmapped++;
        }
    }

    public void MapCategories() {
        if (!drawer.isEmpty()) {
        }
        String[] checkerCommands = {getApplicationContext().getCacheDir() + "/alljsons/drawer.json", "drawer", "jsons"};
        drawer = new ArrayList<>();
        final Map<String, String> jsonMap = ReadJsonFile.main(checkerCommands);
        for (String key : jsonMap.keySet()) {
            Category newone = new Category();
            DrawerCategory drawerCategory = new DrawerCategory(key, newone);
            drawer.add(drawerCategory);
        }
        Main = new Category();
        String[] checkerCommands2 = {getCacheDir() + "/alljsons/Main.json", "category", "jsonlink"};
        final Map<String, String> jsonMap2 = ReadJsonFile.main(checkerCommands2);
        for (String key : jsonMap2.keySet()) {
            Main.categoryLinks.add(jsonMap2.get(key));
            Main.categoryNames.add(key);
        }

    }

    public void StartDownloadoF(String link, String name) {
        Intent i = new Intent(this, Downloader.class);
        i.putExtra("link", link);
        i.putExtra("filename", name);
        i.putExtra(Downloader.EXTRA_MESSENGER, new Messenger(handler));
        startService(i);
    }

    public void filesToDownload(boolean check) {
        if (!check) {
            changetoMain();
        }
        StartDownloadoF(getResources().getString(R.string.drawercategoriesjson), "drawer.json");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("downloads", 0).apply();

        textView = (TextView) findViewById(R.id.textView);
        File folders = new File(getCacheDir(), "/alljsons/");
        if (!folders.exists()) {
            folders.mkdirs();
        }

        File authorinfo = new File(getCacheDir() + "/alljsons/authors_info.json");
        File categories = new File(getCacheDir() + "/alljsons/Main.json");
        File drawercat = new File(getCacheDir() + "/alljsons/drawer.json");

        if (!authorinfo.exists() && !categories.exists() && !drawercat.exists()) {
            if (!Basic.isNetworkAvailable(this)) {
                textView.setVisibility(View.VISIBLE);
            } else {
                filesToDownload(true);
            }
        } else {
            changed = true;
            changetoMain();
            filesToDownload(true);
        }
    }
}
