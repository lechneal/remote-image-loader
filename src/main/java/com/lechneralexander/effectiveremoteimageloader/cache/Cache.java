package com.lechneralexander.effectiveremoteimageloader.cache;

import android.graphics.Bitmap;


public interface Cache {
    /**
     * Get the bitmap from memory cache. Return null if no memory cache is used.
     *
     * @param url
     * @return
     */
    Bitmap getBitmapFromMemoryCache(String url);

    boolean addBitmapToMemoryCache(String cacheKey, Bitmap bitmap);

    /**
     * Get the bitmap from disk cache. Return null if no disk cache is used.
     *
     * @param cacheKey
     * @return
     */
    Bitmap getBitmapFromDiskCache(String cacheKey);

    boolean addBitmapToDiskCache(String cacheKey, Bitmap bitmap);

    /**
     * Add bitmap to memory and disk cache
     *
     * @param cacheKey
     * @param bitmap
     * @return
     */
    boolean addBitmap(String cacheKey, Bitmap bitmap);

    /**
     * Create a unique cache key for the scaled bitmap, using url, requested bitmap with and height
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    String createScaledBitmapKey(String url, Integer width, Integer height);

}
