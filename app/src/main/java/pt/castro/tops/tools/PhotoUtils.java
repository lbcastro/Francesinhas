package pt.castro.tops.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Lourenço on 23/06/2015.
 */
public class PhotoUtils {
    public static DisplayImageOptions getDisplayImageOptions(boolean fadeIn) {
        return new DisplayImageOptions.Builder().cacheOnDisk(true).postProcessor(null)
                .delayBeforeLoading(0).cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(fadeIn ? 400 : 0) {
            @Override
            public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
                if (loadedFrom != LoadedFrom.MEMORY_CACHE) {
                    super.display(bitmap, imageAware, loadedFrom);
                } else {
                    imageAware.setImageBitmap(bitmap);
                }
            }
        }).imageScaleType(ImageScaleType.EXACTLY).build();
    }

    public static Bitmap bitmapFromFile(final File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    }

    public static Bitmap getCachedBitmap(final String url) {
        List<Bitmap> list = MemoryCacheUtils.findCachedBitmapsForImageUri(url, ImageLoader
                .getInstance().getMemoryCache());
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}