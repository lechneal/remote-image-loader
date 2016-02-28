package com.lechneralexander.effectiveremoteimageloader.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.lechneralexander.effectiveremoteimageloader.workertasks.BitmapWorkerTask;

import java.lang.ref.WeakReference;

public class BitmapHelper {

    /**
     * Calculates the float factor to downsample the bitmap to cover the imageView.
     * @param width original Bitmap width
     * @param height original Bitmap height
     * @param newWidth
     * @param newHeight
     * @return float downsample scale factor
     */
    public static float calculateDownsampleScaleFactor(int width, int height, int newWidth, int newHeight) {
        float factor = 1;

        //Check if bitmap should be downscaled
        if (height > newHeight && width > newWidth) {
            // Calculate height and width ratio
            final float heightRatio =  (float) newHeight / (float) height;
            final float widthRatio = (float) newWidth / (float) width;

            // Choose the smaller ratio as the downsample factor, this will guarantee the final bitmap will cover the imageView
            factor = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return factor;
    }

    /**
     * Scales the provided bitmap to the height and width provided (using antialiasing).
     * (Alternative method for scaling bitmaps since Bitmap.createScaledBitmap(...) produces low quality bitmaps.)
     *
     * @param bitmap is the bitmap to scale.
     * @param newWidth is the desired width of the scaled bitmap.
     * @param newHeight is the desired height of the scaled bitmap.
     * @return the scaled bitmap.
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));

        return scaledBitmap;
    }

    public static boolean cancelPotentialWork(String url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            if (!bitmapWorkerTask.isEqualData(url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, BitmapWorkerTask bitmapWorkerTask) {
            //Create an empty bitmap!
            super(res);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}
