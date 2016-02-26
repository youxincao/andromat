package com.sina.weibo.memory;

import java.awt.image.BufferedImage;

/**
 * Created by weilun on 16/2/25.
 */
public class ARGBPictureSaver implements PictureSaverInterface {

    public boolean canHandleBuffer(int width, int height, byte[] buf) {
        return width * height * 4 == buf.length;
    }

    public BufferedImage savePicture(int width, int height, byte[] buf) {
        if( ! canHandleBuffer(width,height,buf))
            return null;
        int [] rgba = new int[width * height];
        for (int i = 0; i < width * height; i ++){
            rgba[i] =  ((buf[i*4]<<16) | (buf[i*4+1]<<8) | (buf[i*4+2]) | (buf[i*4+3]<<24));
        }
        BufferedImage image  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0,width, height, rgba, 0, width);
        return image;
    }
}
