package com.lechneralexander.effectiveremoteimageloader.workertasks;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lechneralexander.effectiveremoteimageloader.cache.Cache;
import com.lechneralexander.effectiveremoteimageloader.helper.BitmapHelper;

import java.io.IOException;
import java.io.InputStream;

public class AssetBitmapWorkerTask extends BitmapWorkerTask {
	private AssetManager assetManager;

	public AssetBitmapWorkerTask(Cache cache, ImageView imageView, ProgressBar progressBar, AssetManager assetManager, Integer width, Integer height, boolean animated) {
		super(cache, imageView, progressBar, width, height, animated);
		this.assetManager = assetManager;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		//Get asset url
		if (urls.length > 0){
			url = urls[0];
		}else{
			return null;
		}

		//generate cache key
		String cacheKey = url;
		if (width != null && height != null){
			cacheKey = cache.createScaledBitmapKey(url, width, height);
		}

		Bitmap bitmap = null;
		try {
			//decode downscaled bitmap from assets and add it to memory cache
			bitmap = decodeAssetBitmap(url, width, height);
			cache.addBitmapToMemoryCache(cacheKey, bitmap);
		} catch (IOException e) {
			e.printStackTrace();
			isError = true;
		}
		
		return bitmap;
	}

	/**
	 * Get and scale asset bitmap
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
     */
	private Bitmap decodeAssetBitmap(String url, Integer width, Integer height) throws IOException{
		InputStream stream = getAssetInputStream(url);

		//Downscale image if required
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(stream, null, options);

		// set sample size (= downsample factor)
		if (width != null && height != null){
			float scaleFactor = BitmapHelper.calculateDownsampleScaleFactor(options.outWidth, options.outHeight, width, height);
			if (scaleFactor != 0){
				options.inSampleSize = (int) Math.floor(1.0f / scaleFactor);
			}
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(stream, null, options);
	}

	private InputStream getAssetInputStream(String url) throws IOException {
		return assetManager.open(url);
	}
}