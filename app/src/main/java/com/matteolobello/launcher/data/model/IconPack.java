package com.matteolobello.launcher.data.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class IconPack {

    private String mIconPackPackageName;

    private Resources iconPackRes;

    private HashMap<String, String> mPackagesDrawables = new HashMap<>();
    private List<Bitmap> mBackImages = new ArrayList<>();
    private Bitmap mMaskImage = null;
    private Bitmap mFrontImage = null;
    private float mFactor = 1.0f;

    private boolean mLoaded;

    public IconPack(String iconPackPackageName) {
        mIconPackPackageName = iconPackPackageName;
    }

    public void load(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            XmlPullParser xmlPullParser = null;

            iconPackRes = packageManager.getResourcesForApplication(mIconPackPackageName);
            int appFilterId = iconPackRes.getIdentifier("appfilter", "xml", mIconPackPackageName);
            if (appFilterId > 0) {
                xmlPullParser = iconPackRes.getXml(appFilterId);
            } else {
                // No resource found, try to open it from Assets folder
                try {
                    InputStream appFilterStream = iconPackRes.getAssets().open("appfilter.xml");

                    XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                    xmlPullParserFactory.setNamespaceAware(true);
                    xmlPullParser = xmlPullParserFactory.newPullParser();
                    xmlPullParser.setInput(appFilterStream, "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (xmlPullParser != null) {
                int eventType = xmlPullParser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xmlPullParser.getName().equals("iconback")) {
                            for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
                                if (xmlPullParser.getAttributeName(i).startsWith("img")) {
                                    String drawableName = xmlPullParser.getAttributeValue(i);
                                    Bitmap iconBack = loadBitmap(drawableName);
                                    if (iconBack != null) {
                                        mBackImages.add(iconBack);
                                    }
                                }
                            }
                        } else if (xmlPullParser.getName().equals("iconmask")) {
                            if (xmlPullParser.getAttributeCount() > 0 && xmlPullParser.getAttributeName(0).equals("img1")) {
                                String drawableName = xmlPullParser.getAttributeValue(0);
                                mMaskImage = loadBitmap(drawableName);
                            }
                        } else if (xmlPullParser.getName().equals("iconupon")) {
                            if (xmlPullParser.getAttributeCount() > 0 && xmlPullParser.getAttributeName(0).equals("img1")) {
                                String drawableName = xmlPullParser.getAttributeValue(0);
                                mFrontImage = loadBitmap(drawableName);
                            }
                        } else if (xmlPullParser.getName().equals("scale")) {
                            if (xmlPullParser.getAttributeCount() > 0 && xmlPullParser.getAttributeName(0).equals("factor")) {
                                mFactor = Float.valueOf(xmlPullParser.getAttributeValue(0));
                            }
                        } else if (xmlPullParser.getName().equals("item")) {
                            String componentName = null;
                            String drawableName = null;

                            for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
                                if (xmlPullParser.getAttributeName(i).equals("component")) {
                                    componentName = xmlPullParser.getAttributeValue(i);
                                } else if (xmlPullParser.getAttributeName(i).equals("drawable")) {
                                    drawableName = xmlPullParser.getAttributeValue(i);
                                }
                            }
                            if (!mPackagesDrawables.containsKey(componentName)) {
                                mPackagesDrawables.put(componentName, drawableName);
                            }
                        }
                    }

                    eventType = xmlPullParser.next();
                }
            }

            mLoaded = true;
        } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadBitmap(String drawableName) {
        int id = iconPackRes.getIdentifier(drawableName, "drawable", mIconPackPackageName);
        if (id > 0) {
            Drawable bitmap = iconPackRes.getDrawable(id);
            if (bitmap instanceof BitmapDrawable) {
                return ((BitmapDrawable) bitmap).getBitmap();
            }
        }
        return null;
    }

    private Drawable loadDrawable(String drawableName) {
        int id = iconPackRes.getIdentifier(drawableName, "drawable", mIconPackPackageName);
        if (id > 0) {
            return iconPackRes.getDrawable(id);
        }

        return null;
    }

    public Drawable getDrawableIconForPackage(Context context, String appPackageName, Drawable defaultDrawable) {
        if (!mLoaded) {
            load(context);
        }

        PackageManager packageManager = context.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(appPackageName);
        String componentName = null;
        if (launchIntent != null)
            componentName = packageManager.getLaunchIntentForPackage(appPackageName).getComponent().toString();
        String drawable = mPackagesDrawables.get(componentName);
        if (drawable != null) {
            return loadDrawable(drawable);
        } else {
            if (componentName != null) {
                int start = componentName.indexOf("{") + 1;
                int end = componentName.indexOf("}", start);
                if (end > start) {
                    drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                    if (iconPackRes.getIdentifier(drawable, "drawable", mIconPackPackageName) > 0) {
                        return loadDrawable(drawable);
                    }
                }
            }
        }
        return defaultDrawable;
    }

    public Bitmap getIconForPackage(Context context, String appPackageName, Bitmap defaultBitmap) {
        if (!mLoaded) {
            load(context);
        }

        PackageManager packageManager = context.getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(appPackageName);
        String componentName = null;
        if (launchIntent != null)
            componentName = packageManager.getLaunchIntentForPackage(appPackageName).getComponent().toString();
        String drawable = mPackagesDrawables.get(componentName);
        if (drawable != null) {
            return loadBitmap(drawable);
        } else {
            // try to get a resource with the component filename
            if (componentName != null) {
                int start = componentName.indexOf("{") + 1;
                int end = componentName.indexOf("}", start);
                if (end > start) {
                    drawable = componentName.substring(start, end).toLowerCase(Locale.getDefault()).replace(".", "_").replace("/", "_");
                    if (iconPackRes.getIdentifier(drawable, "drawable", mIconPackPackageName) > 0) {
                        return loadBitmap(drawable);
                    }
                }
            }
        }
        return generateBitmap(defaultBitmap);
    }

    private Bitmap generateBitmap(Bitmap defaultBitmap) {
        if (mBackImages.size() == 0) {
            return defaultBitmap;
        }

        Random random = new Random();
        int backImageInd = random.nextInt(mBackImages.size());
        Bitmap backImage = mBackImages.get(backImageInd);
        int w = backImage.getWidth();
        int h = backImage.getHeight();

        // create a bitmap for the result
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        // draw the background first
        canvas.drawBitmap(backImage, 0, 0, null);

        // create a mutable mask bitmap with the same mask
        Bitmap scaledBitmap = defaultBitmap;
        if (defaultBitmap != null && (defaultBitmap.getWidth() > w || defaultBitmap.getHeight() > h)) {
            Bitmap.createScaledBitmap(defaultBitmap, (int) (w * mFactor), (int) (h * mFactor), false);
        }

        if (mMaskImage != null) {
            // draw the scaled bitmap with mask
            Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mutableMask);
            maskCanvas.drawBitmap(mMaskImage, 0, 0, new Paint());

            // paint the bitmap with mask into the result
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2, (h - scaledBitmap.getHeight()) / 2, null);
            canvas.drawBitmap(mutableMask, 0, 0, paint);
            paint.setXfermode(null);
        } else {
            // draw the scaled bitmap without mask
            canvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2, (h - scaledBitmap.getHeight()) / 2, null);
        }

        // paint the front
        if (mFrontImage != null) {
            canvas.drawBitmap(mFrontImage, 0, 0, null);
        }

        return result;
    }
}