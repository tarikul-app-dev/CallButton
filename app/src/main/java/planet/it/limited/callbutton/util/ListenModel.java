package planet.it.limited.callbutton.util;

import android.graphics.drawable.Drawable;

/**
 * Created by Tarikul on 6/7/2018.
 */

public class ListenModel {

    public ListenModel( ) {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIconImage() {
        return iconImage;
    }

    public void setIconImage(Drawable iconImage) {
        this.iconImage = iconImage;
    }

    public String title;
    public Drawable iconImage;


}
