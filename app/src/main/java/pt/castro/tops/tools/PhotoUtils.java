package pt.castro.tops.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import java.io.File;

import pt.castro.tops.R;

/**
 * Created by Lourenço on 23/06/2015.
 */
public class PhotoUtils {

    /**
     * Decodes a bitmap from the given file.
     *
     * @param imageFile Image file
     * @return Bitmap object
     */
    public static Bitmap bitmapFromFile(final File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    }

    /**
     * Tints the provided drawable resource with a specific color
     *
     * @param context  Application context
     * @param resource Drawable resource
     * @return Tinted drawable object
     */
    public static Drawable tintedDrawable(final Context context, final int resource) {
        final Drawable normalDrawable = ContextCompat.getDrawable(context, resource);
        final Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, R.color.blue_bright));
        return wrapDrawable;
    }
}