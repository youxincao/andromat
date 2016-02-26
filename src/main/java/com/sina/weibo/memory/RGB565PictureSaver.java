package com.sina.weibo.memory;

import java.awt.image.BufferedImage;

/**
 * Created by weilun on 16/2/26.
 */
public class RGB565PictureSaver implements PictureSaverInterface {
    public boolean canHandleBuffer(int width, int height, byte[] buf) {
        return width * height * 2 == buf.length;
    }

    public BufferedImage savePicture(int width, int height, byte[] buf) {
        if( ! canHandleBuffer(width,height,buf))
            return null;

        int len = width * height;
        int [] rgb565_buf = new int [len];

        for (int i = 0; i < len; i++){
            int red = (buf[2*i + 1] & 0xF8);
            int green = ((buf[2*i + 1] & 0x07) << 5 )| ((buf[2*i] & 0xE0) >> 3);
            int blue = (buf[2*i] & 0x1F) << 3;

            rgb565_buf[i] = 0xFF << 24 | red << 16 | green << 8 | blue ;
        }

        BufferedImage image  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0,width, height, rgb565_buf , 0, width);
        return image;
    }
}
