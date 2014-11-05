package org.auie.image;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.auie.core.Auie;
import org.auie.utils.UEImageNotByteException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

public class UEImageLoader {

    private static HashSet<String> mSet;
    private static Map<String,SoftReference<Bitmap>> mImageCache;   
    private static UEImageCacheManager cacheManager;  
    private static ExecutorService mExecutorService;
    private Handler mHandler;   
    
    static{  
        mSet = new HashSet<String>();  
        mImageCache = new HashMap<String,SoftReference<Bitmap>>();  
        cacheManager = new UEImageCacheManager(mImageCache);  
    }  
  
    public UEImageLoader(Context context){  
        mHandler = new Handler();  
        startThreadPoolIfNecessary();
        String defaultDir = context.getCacheDir().getAbsolutePath();  
        setCachedDir(defaultDir);  
    }  
       
    public void setCache2File(boolean flag){  
        cacheManager.setCache2File(flag);  
    }  
    
    public void setCachedDir(String dir){  
        cacheManager.setCachedDir(dir);  
    }  
  
    public static void startThreadPoolIfNecessary(){  
        if(mExecutorService == null || mExecutorService.isShutdown() || mExecutorService.isTerminated()){  
            mExecutorService = Executors.newFixedThreadPool(3);
        }  
    }  
    
    public void downloadImage(final String url, final UEImageLoadListener callback){  
        downloadImage(url, true, callback);  
    }  
      
    public void downloadFile(final String url, final UEImageLoadListener callback){  
    	Bitmap bitmap = cacheManager.getBitmapFromMemory(url);  
        if(bitmap != null){  
            if(callback != null){  
                callback.onImageLoadComlepeted(bitmap, url);  
            }  
        }else{  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
					try {
						final Bitmap bitmap = new UEImage(url, true).toBitmap();
						mHandler.post(new Runnable(){  
							@Override  
							public void run(){  
								if(callback != null)  
									callback.onImageLoadComlepeted(bitmap, url);  
								mSet.remove(url);  
							}  
						});  
					} catch (IOException | UEImageNotByteException e) {
						e.printStackTrace();
					}  
                }  
            });  
        }  
    }
    
    public void downloadImage(final String url, final boolean cache2Memory, final UEImageLoadListener callback){  
        if(mSet.contains(url)){  
            Log.w(Auie.TAG, "图片正在下载，不能重复下载");  
            return;
        }  
          
        Bitmap bitmap = cacheManager.getBitmapFromMemory(url);  
        if(bitmap != null){  
            if(callback != null){  
                callback.onImageLoadComlepeted(bitmap, url);  
            }  
        }else{
            mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
                    final Bitmap bitmap = cacheManager.getBitmapFromHttp(url, cache2Memory);  
                    mHandler.post(new Runnable(){  
                        @Override  
                        public void run(){  
                            if(callback != null)  
                                callback.onImageLoadComlepeted(bitmap, url);  
                            mSet.remove(url);  
                        }  
                    });  
                }  
            });  
        }  
    }  
      
    public void preLoadNextImage(final String url){    
        downloadImage(url, null);  
    }  
    
    
    public interface UEImageLoadListener{   
        public void onImageLoadComlepeted(Bitmap bitmap, String imageUrl);  
    }  
      
}
