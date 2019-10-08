package wallpapers.aura.Util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Aditya on 18/12/16.
 */
public class ReadJsonFile {
    static Context context;

    public ReadJsonFile(Context cont) {
        this.context = cont;
    }

    public static Map main(String args[]) {

        Map<String, String> map = new LinkedHashMap<>();
        Map<String, String> emptyMap = new LinkedHashMap<>();

        try {
            File mainJson = new File(args[0]);
            FileInputStream stream = new FileInputStream(mainJson);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }

            JSONArray response = new JSONArray(jsonStr);
            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject category = (JSONObject) response
                            .get(i);
                    map.put(category.getString(args[1]), category.getString(args[2]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return emptyMap;
        }
    }
}
