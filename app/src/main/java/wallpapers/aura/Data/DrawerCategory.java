package wallpapers.aura.Data;


/**
 * Created by Aditya on 18/12/16.
 */

public class DrawerCategory {
    public String subName;
    public Category jsons;

    public DrawerCategory(String  name,Category JsonsConverted)
    {
        this.subName=name;
        this.jsons=JsonsConverted;
    }
}
