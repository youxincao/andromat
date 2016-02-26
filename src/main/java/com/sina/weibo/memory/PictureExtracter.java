package com.sina.weibo.memory;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.parser.model.PrimitiveArrayImpl;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by weilun on 16/2/25.
 */
public class PictureExtracter {

    private static final String REF_ANDROID_BITMAP = "android.graphics.Bitmap";

    private Set<String> mReferenceSet = null;
    private Set<PictureSaverInterface> mPictureHandlerSet = null;
    private ISnapshot mSnapshot = null;

    public PictureExtracter(ISnapshot snapshot, Set<String> customRefs) {
        mSnapshot = snapshot;

        /**
         * Add class reference
         */
        mReferenceSet = new HashSet<String>();
        mReferenceSet.add(REF_ANDROID_BITMAP);
        if (customRefs != null && customRefs.size() != 0)
            mReferenceSet.addAll(customRefs);

        /**
         * Add picture handler
         */
        mPictureHandlerSet = new HashSet<PictureSaverInterface>();
        mPictureHandlerSet.add(new ARGBPictureSaver());
        mPictureHandlerSet.add(new RGB565PictureSaver());
    }

    /**
     * 抽取图片
     *
     * @return 抽取的图片的张数
     */
    public int extractPicture(String picSavePath) {
        int res = 0;
        for (String refName : mReferenceSet) {
            Collection<IClass> classes;
            try {
                classes = mSnapshot.getClassesByName(refName, false);
            } catch (SnapshotException e) {
                e.printStackTrace();
                continue;
            }
            if (classes == null || classes.size() == 0) {
                continue;
            }

            for (IClass clazz : classes) {
                int[] objIds;
                try {
                    objIds = clazz.getObjectIds();
                } catch (SnapshotException e) {
                    e.printStackTrace();
                    continue;
                }

                for (int objId: objIds){
                    try {
                        IObject bmp = mSnapshot.getObject(objId);
                        String address = Long.toHexString(mSnapshot.mapIdToAddress(objId));
                        int height = ((Integer)bmp.resolveValue("mHeight")).intValue();
                        int width = ((Integer)bmp.resolveValue("mWidth")).intValue();
                        if((height<=-1) || (width<=0))
                        {
                            System.out.println(String.format("Bitmap address=%s has bad height %d or width %d!", address, height, width));
                            continue;
                        }

                        PrimitiveArrayImpl array = (PrimitiveArrayImpl)bmp.resolveValue("mBuffer");
                        if(array == null)
                        {
                            System.out.println(String.format("Bitmap address=%s has null buffer value!", address));
                            continue;
                        }
                        byte []buffer  = (byte[])array.getValueArray();
                        for (PictureSaverInterface pictureSaver: mPictureHandlerSet ){
                            if( pictureSaver.canHandleBuffer(width, height, buffer)){
                                BufferedImage image = pictureSaver.savePicture(width,height,buffer);
                                if( image != null )
                                     savePicture(width, height, address, buffer.length, image, picSavePath);
                            }
                        }

                    } catch (SnapshotException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
        return 0;
    }

    private void savePicture(int width, int height, String address,int length, BufferedImage image, String picSavePath) {
        String filename = generateFileName(width, height, address, length, picSavePath);
        File file  = new File(filename);
        try {
            ImageIO.write(image, "png" , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileName(int width, int height, String address,int length, String picSavePath){
        return picSavePath + "/" + width + "_" + height + "_" + length + "_" + address + ".png";
    }

}
