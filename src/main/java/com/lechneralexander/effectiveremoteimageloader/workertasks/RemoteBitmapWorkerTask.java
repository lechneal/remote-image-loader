package com.lechneralexander.effectiveremoteimageloader.workertasks;

import android.accounts.NetworkErrorException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lechneralexander.effectiveremoteimageloader.cache.Cache;
import com.lechneralexander.effectiveremoteimageloader.helper.BitmapHelper;

import java.io.IOException;
import java.io.InputStream;

public class RemoteBitmapWorkerTask extends BitmapWorkerTask {

	public RemoteBitmapWorkerTask(Cache cache, ImageView imageView, ProgressBar progressBar, Integer width, Integer height, boolean animated) {
		super(cache, imageView, progressBar, width, height, animated);
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		//Get remote url
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

		//try to get image from disk cache
		Bitmap bitmap = null;
		try {
			bitmap = cache.getBitmapFromDiskCache(cacheKey);
			if (bitmap != null){
				//bitmap was found in disk cache, add it to memory cache
				cache.addBitmapToMemoryCache(cacheKey, bitmap);
			}else{
				//bitmap was not found in disk cache, fetch and cache remote image
				bitmap = decodeRemoteBitmap(url, width, height);
				cache.addBitmap(cacheKey, bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
		} 
		return bitmap;
	}

	/**
	 * Fetch, cache and scale remote bitmap
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 * @throws NetworkErrorException
     */
	private Bitmap decodeRemoteBitmap(String url, Integer width, Integer height) throws IOException, NetworkErrorException {
		//try to load original bitmap from disk cache!
		Bitmap originalBitmap = cache.getBitmapFromDiskCache(url);
		if (originalBitmap == null){
			//fetch remote file
			InputStream stream = getRemoteInputStream(url);
			//TODO: use a different inSampleSize decoding the stream (handling extremely large images)
			originalBitmap = BitmapFactory.decodeStream(stream);
			//Add original bitmap to cache
			if (originalBitmap != null){
				cache.addBitmapToDiskCache(url, originalBitmap);
			}else{
				throw new NetworkErrorException();
			}
		}

		//Scale bitmap if required
		Bitmap scaledBitmap = originalBitmap;
		if (width != null && height != null){
			float scaleFactor = BitmapHelper.calculateDownsampleScaleFactor(originalBitmap.getWidth(), originalBitmap.getHeight(), width, height);
			if (scaleFactor != 1){
				int newWidth = (int) (originalBitmap.getWidth() * scaleFactor);
				int newHeight = (int) (originalBitmap.getHeight() * scaleFactor);
				scaledBitmap = BitmapHelper.scaleBitmap(originalBitmap, newWidth, newHeight);
			}
		}
		return scaledBitmap;
	}

	private InputStream getRemoteInputStream(String url) throws IOException {
		return new java.net.URL(url).openStream();
	}
}