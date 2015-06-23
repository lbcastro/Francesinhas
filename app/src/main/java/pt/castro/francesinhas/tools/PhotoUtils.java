package pt.castro.francesinhas.tools;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Lourenço on 23/06/2015.
 */
public class PhotoUtils {
    public static DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true).cacheOnDisc(true)
                .postProcessor(null).delayBeforeLoading(0).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
    }
}