/**
 The MIT License (MIT)

 Copyright (c) 2016 Lechner Alexander

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.lechneralexander.effectiveremoteimageloader;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lechneralexander.effectiveremoteimageloader.cache.Cache;
import com.lechneralexander.effectiveremoteimageloader.helper.BitmapHelper;
import com.lechneralexander.effectiveremoteimageloader.workertasks.AssetBitmapWorkerTask;
import com.lechneralexander.effectiveremoteimageloader.workertasks.BitmapWorkerTask;
import com.lechneralexander.effectiveremoteimageloader.workertasks.RemoteBitmapWorkerTask;


/**
 * Class to fetch, scale and cache remote and asset images
 */
public class BitmapLoader {
	private Cache cache = null;
	private AssetManager assetManager = null;

	public BitmapLoader(AssetManager assetManager, Cache cache) {
		this.assetManager = assetManager;
		this.cache = cache;
	}

	/**
	 * Load asset bitmap into given imageView. The bitmap is loaded off the UI thread, automatically downscaled to the imageView's dimensions.
	 * @param url asset url
	 * @param imageView
	 * @param progressBar is shown until the bitmap is loaded. Set null if no progressBar is used.
     */
	public void loadAssetBitmap(final String url, final ImageView imageView, final ProgressBar progressBar, final boolean animated) {
		if (BitmapHelper.cancelPotentialWork(url, imageView)) {

			//Wait until layout was computed and imageView dimensions are known
			imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					//Remove TreeViewObserver (to avoid iterative computations)
					imageView.getViewTreeObserver().removeOnPreDrawListener(this);

					//Prepare vars
					Resources resources = imageView.getResources();
					Integer width = imageView.getMeasuredWidth();
					Integer height = imageView.getMeasuredHeight();

					//Check if view is visible at all
					if (width > 0 || height > 0){
						//Calculate cache key
						String cacheKey = cache.createScaledBitmapKey(url, width, height);

						//Check Memory Cache
						try{
							Bitmap bitmap = cache.getBitmapFromMemoryCache(cacheKey);
							if (bitmap != null) {
								//Use bitmap found in memory cache
								imageView.setImageBitmap(bitmap);
								imageView.setVisibility(View.VISIBLE);
								if (progressBar != null){
									progressBar.setVisibility(View.GONE);
								}
							} else {
								//Bitmap not found in memory cache, load the bitmap off the UI thread
								final BitmapWorkerTask task = new AssetBitmapWorkerTask(cache, imageView, progressBar, assetManager, width, height, animated);
								final BitmapHelper.AsyncDrawable asyncDrawable = new BitmapHelper.AsyncDrawable(resources, task);
								imageView.setImageDrawable(asyncDrawable);
								task.execute(url);

								//TODO: optional parallel task execution: task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
							}
						}catch(Exception exception){
							//Exception while loading bitmap, hide image view
							imageView.setVisibility(View.INVISIBLE);
							if (progressBar != null){
								progressBar.setVisibility(View.GONE);
							}
						}
					}else{
						//Image is not visible
						imageView.setVisibility(View.INVISIBLE);
					}
					return true;
				}
			});
		}
	}

	/**
	 * Load remote bitmap into given imageView. The bitmap is loaded off the UI thread, automatically downscaled to the imageView's dimensions.
	 * @param url remote bitmap url
	 * @param imageView
	 * @param progressBar is shown until the bitmap is loaded. Set null if no progressBar is used.
	 * @param animated if true, the bitmap is faded in
     */
	public void loadRemoteBitmap(final String url, final ImageView imageView, final ProgressBar progressBar, final boolean animated) {
		//Handle empty url
        if (url == null || url.trim().length() == 0){
			return;
		}

		if (BitmapHelper.cancelPotentialWork(url, imageView)) {
            //Wait until layout was computed and imageView dimensions are known
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //Remove TreeViewObserver (to avoid iterative computations)
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    //Prepare vars
                    Resources resources = imageView.getResources();
                    Integer width = imageView.getMeasuredWidth();
                    Integer height = imageView.getMeasuredHeight();

                    //Check if view is visible at all
                    if (width > 0 || height > 0){
						//Calculate cache key
						String cacheKey = cache.createScaledBitmapKey(url, width, height);

                        //Check Memory Cache
                        try{
                            Bitmap bitmap = cache.getBitmapFromMemoryCache(cacheKey);
                            if (bitmap != null) {
								//Use bitmap found in memory cache
                                imageView.setImageBitmap(bitmap);
                                imageView.setVisibility(View.VISIBLE);
                                if (progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
                            } else {
								//Bitmap not found in memory cache, load the bitmap off the UI thread
                                final BitmapWorkerTask task = new RemoteBitmapWorkerTask(cache, imageView, progressBar, width, height, animated);
                                final BitmapHelper.AsyncDrawable asyncDrawable = new BitmapHelper.AsyncDrawable(resources, task);
                                imageView.setImageDrawable(asyncDrawable);
                                task.execute(url);

                                //TODO: optional parallel task execution: task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
                            }
                        }catch(Exception exception){
							//Exception while loading bitmap, hide image view
                            imageView.setVisibility(View.INVISIBLE);
                            if (progressBar != null){
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }else{
						//Image is not visible
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
            });
		}
	}

}
