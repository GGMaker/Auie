package org.auie.image;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.auie.utils.UEImageNotByteException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@SuppressWarnings("deprecation")
public class UEImage {
	
	public static final float TRANSFORMROUND_CIRCLE = -1;

	private Bitmap bitmap = null;
	private Drawable drawable = null;
	
	public UEImage(Drawable drawable){
		this.drawable = drawable;
		drawableToBitmap();
	}
	
	public UEImage(Resources res, int resId){
		this(res.getDrawable(resId));
	}
	
	public UEImage(Bitmap bitmap){
		this(new BitmapDrawable(bitmap));
	}
	
	public UEImage(String filePath) throws UEImageNotByteException{
		if (!new File(filePath).exists()) {
			throw new UEImageNotByteException("this params length is 0 or not exists, so not tansform to image.");
		}
		this.bitmap = BitmapFactory.decodeFile(filePath);
		bitmapToDrawable();
	}

	public UEImage(byte[] data) throws UEImageNotByteException{
		if (data.length == 0) {
			throw new UEImageNotByteException("this params length is 0 or not exists, so not tansform to image.");
		}
		this.bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		bitmapToDrawable();
	}
	
	public UEImage(String filePath, boolean resize) throws IOException, UEImageNotByteException{
		if (!resize) {
			if (!new File(filePath).exists()) {
				throw new UEImageNotByteException("this params length is 0 or not exists, so not tansform to image.");
			}
			this.bitmap = BitmapFactory.decodeFile(filePath);
		} else {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(filePath)));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int i = 0;
			Bitmap bitmap = null;
			while (true) {
				if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
					in = new BufferedInputStream(new FileInputStream(new File(filePath)));
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, options);
					break;
				}
				i += 1;
			}
			this.bitmap = bitmap;
		}
		bitmapToDrawable();
	}
	
	public UEImage transformRound(float radius){
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final int color = 0xff888888; 
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		if (radius == TRANSFORMROUND_CIRCLE) {
			radius = width > height ? width/2 : height/2;
		}
		Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		this.bitmap = mBitmap;
		bitmapToDrawable();
		return this;
	}
	
	public Bitmap toBitmap(){
		return bitmap;
	}
	
	public Drawable toDrawable(){
		return drawable;
	}
	
	public byte[] toByteArray(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	    return baos.toByteArray();
	}
	
	private void bitmapToDrawable(){
		this.drawable = new BitmapDrawable(this.bitmap);
	}
	
	private void drawableToBitmap() {
		final int width = this.drawable.getIntrinsicWidth();
		final int height = this.drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, this.drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		this.drawable.setBounds(0, 0, width, height);
		this.drawable.draw(canvas);
		this.bitmap = bitmap;
	}
	
	public UEImage addFilterToGray(){
		this.drawable.mutate();
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);
		this.drawable.setColorFilter(new ColorMatrixColorFilter(matrix));
		drawableToBitmap();
		return this;
	}
	
	public UEImage compressOnlyQuality(int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, null);
        bitmapToDrawable();
        return this;
    }
	
	public UEImage compress(int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int options = 100;  
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > quality) {        
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, null);
        bitmapToDrawable();
        return this;
    }
	
	public UEImage resize(int width, int height){
		float scaleWidth = ((float)width) / bitmap.getWidth();
		float scaleHeight = ((float)height) / bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		bitmapToDrawable();
 		return this;
	}
}
