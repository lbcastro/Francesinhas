package pt.castro.francesinhas.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

/**
 * Created by Lourenço on 23/06/2015.
 */
public class PhotoUtils {
    public static DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                .cacheOnDisk(true).postProcessor(null).delayBeforeLoading(0)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new
                        FadeInBitmapDisplayer(400)).imageScaleType(ImageScaleType
                        .EXACTLY).build();
    }

    public static Bitmap bitmapFromFile(final File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    }
}