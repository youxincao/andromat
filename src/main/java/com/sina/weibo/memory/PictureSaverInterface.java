package com.sina.weibo.memory;

import java.awt.image.BufferedImage;

/**
 * Created by weilun on 16/2/25.
 */
public interface PictureSaverInterface {

    boolean canHandleBuffer(int width, int height, byte[] buf);

    BufferedImage savePicture(int width, int height, byte [] buf);
}
