package org.eclipse.mat.tests;


import com.sina.weibo.memory.PictureExtracter;
import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.parser.internal.SnapshotFactory;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.util.ConsoleProgressListener;
import org.eclipse.mat.util.IProgressListener;

import java.io.File;
import java.util.HashMap;

/**
 * Created by weilun on 16/2/26.
 */
public class TestPictureSaver {
    public static void main(String[] args) throws SnapshotException {

        String fileName = args[args.length - 1];

        IProgressListener listener = new ConsoleProgressListener(System.out);

        SnapshotFactory sf = new SnapshotFactory();
        ISnapshot snapshot = sf.openSnapshot(new File(fileName),
                new HashMap<String, String>(), listener);

        PictureExtracter extracter = new PictureExtracter(snapshot, null);
        extracter.extractPicture("/Users/weilun/tmp");
    }
}
