package wallpapers.aura.Data;

import android.widget.ImageView;

/**
 * Created by ${Aditya} on 7/27/2016.
 */
public class WallpaperInfo {
    public String walllink;
    public String walltitle;
    public String wallthumb;
    public String author;
    public ImageView sharedElement;

    public WallpaperInfo(String walllink, String walltitle, String wallthumb, String Author) {
        this.walllink = walllink;
        this.walltitle = walltitle;
        this.wallthumb = wallthumb;
        this.author = Author;
    }

    public WallpaperInfo(String walllink, String walltitle,boolean author, String Author) {
        this.walllink = walllink;
        this.walltitle = walltitle;
        this.wallthumb = walllink;
        this.author = Author;
    }
}
