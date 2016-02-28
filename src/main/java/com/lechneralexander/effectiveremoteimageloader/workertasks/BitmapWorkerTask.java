package com.lechneralexander.effectiveremoteimageloader.workertasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lechneralexander.effectiveremoteimageloader.animation.UIAnimator;
import com.lechneralexander.effectiveremoteimageloader.cache.Cache;
import com.lechneralexander.effectiveremoteimageloader.helper.BitmapHelper;

import java.lang.ref.WeakReference;

/**
 * Abstract Bitmap Worker Task
 */
public abstract class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	private WeakReference<ImageView> imageViewReference = null;
	private WeakReference<ProgressBar> progressBarReference = null;
	protected Cache cache = null;
	protected Integer width = null;
	protected Integer height = null;
	protected boolean animated = true;
	protected String url = "";
	protected boolean isError = false;

	public BitmapWorkerTask(Cache cache, ImageView imageView, ProgressBar progressBar, Integer width, Integer height, boolean animated) {
		this.cache = cache;
		this.imageViewReference = new WeakReference<>(imageView);
		this.progressBarReference = new WeakReference<>(progressBar);
		this.animated = animated;
		this.width = width;
		this.height = height;

		init();
	}

	private void init(){
		//hide imageView and show progressBar (if given)
		hideImageView();
		showProgressBar();
	}

	@Override
	protected abstract Bitmap doInBackground(String... urls);

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		//Check if task was cancelled
		if (isCancelled() || isError) {
			bitmap = null;
		}

		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = getImageView();
			final BitmapWorkerTask bitmapWorkerTask = BitmapHelper.getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				//Set bitmap and show imageView
				imageView.setImageBitmap(bitmap);
				showImageView();
				if (animated){
					UIAnimator.fadeInImage(imageView);
				}
			}
		}else if (bitmap == null && imageViewReference != null){
			hideImageView();
		}

		//Task executed, hide progress bar
		hideProgressBar();
	}

	public String getData(){
		return url;
	}
	
	public boolean isEqualData(Object object){
		if (url != null && object != null){
			if (url.getClass().equals(object.getClass())){
				return url.equals(object);
			}
		}
		return false;
	}


	protected ImageView getImageView(){
		if (imageViewReference != null){
			return imageViewReference.get();
		}		
		return null;
	}

	protected void showImageView(){
		if (imageViewReference != null){
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				try{
					imageView.setVisibility(View.VISIBLE);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void hideImageView(){
		if (imageViewReference != null){
			final ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				try{
					imageView.setVisibility(View.INVISIBLE);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void showProgressBar(){
		if (progressBarReference != null){
			final ProgressBar progressbar = progressBarReference.get();
			if (progressbar != null) {
				try{
					progressbar.setVisibility(View.VISIBLE);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void hideProgressBar(){
		if (progressBarReference != null){
			final ProgressBar progressbar = progressBarReference.get();
			if (progressbar != null) {
				try{
					progressbar.setVisibility(View.GONE);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}