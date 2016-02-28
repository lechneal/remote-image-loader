# remote-image-loader for Android
This project can be used to handle remote and asset images in Android. Images are automatically fetched, cached and scaled down to the effective image view size, in order to minimize memory usage and optimize speed.

Images are automatically scaled down in order to perfectly fit inside the given imageView. Scaled-down versions of the original image are kept in memory and in disk cache for maximal speed. 

Optional features: 
* a progressBar/spinner can be used to indicating the image is still loading
* animate (fadeIn) the image

## Usage

### Init
Set the largeHeap flag in your AndroidManifest if you are handling large image files
```xml
android:largeHeap="true"
```
Create a new BitmapLoader
```java
BitmapLoader loader = new BitmapLoader(getAssets(), cache);
```

### Load Asset Image
Load an asset image into an imageView (progressBar is optional and can be null)
```java
loader.loadAssetBitmap("test.jpg", imageView, progressBar, true);
```
### Load Remote Image
Load a remote image into an imageView (progressBar is optional and can be null)
```java
loader.loadAssetBitmap("http://mydomain.com/image.jpg", imageView, progressBar, true);
```

## Cache
Simply implement the given Cache interface. Use a memory and/or disk cache of your choice, for instance LruCache and DiskLruCache. 

## Internal Workflow
Loading remote image into imageView workflow:
![Remote Image Workflow](/remoteImageWorkflow.png)

If the bitmap is not found in memory cache, the fetching, caching, processing of the image are performed off the UI thread

Asset images are handled in a similar way. The only difference is that no disk cache is used for asset images.

## Developed By
Alexander Lechner

## License 
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
