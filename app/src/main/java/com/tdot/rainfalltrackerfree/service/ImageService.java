package com.tdot.rainfalltrackerfree.service;

import java.io.InputStream;

import com.tdot.rainfalltrackerfree.model.DataStore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageService extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    DataStore dS;
    int frame;

    public ImageService(ImageView bmImage, DataStore dSvalue,int frame) {
        this.bmImage = bmImage;
        dS=dSvalue;
        this.frame=frame;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        
        		
        //Here add the bitmap to the datastore , effectively this is cache
        //dS.getLayer(dS.getNumLayers()-1).setBitmapCache(frame, result);
        dS.getLayer(dS.getNumLayers()-1).setBitmapCache(frame, getResizedBitmap(result,262,196));
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
