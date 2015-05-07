package ua.ck.geekhub.prozapas.ghprozapasrssreader.applications;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;

import static ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const.IMAGELOADER;

/**
 * Created by Dante on 4/29/2015.
 */
public class RssApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }

    private void initImageLoader () {
        if (IMAGELOADER == null) {
            IMAGELOADER = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.mipmap.no_photo)
                    .showImageOnFail(R.mipmap.no_photo)
                    .showImageOnLoading(R.mipmap.no_photo)
                    .build();
            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                    .diskCacheExtraOptions(1024, 1024, null)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .memoryCacheSize(2 * 1024 * 1024)
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .defaultDisplayImageOptions(options)
                    .build();
            IMAGELOADER.init(configuration);
        }
    }
}
