package delivery.food.eg.com.adomicilio;

import android.graphics.Bitmap;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import android.support.v4.util.LruCache;

/**
 * Created by zezzi on 1/4/15.
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}