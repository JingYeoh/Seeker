package com.yeoh.seeker.processer;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.yeoh.seeker.HideMethod;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Used to generate seeker.json file
 *
 * @author yangjing .
 * @since 2018-09-27
 */
class SeekerDelegateJsonGenerator {

    private Filer mFiler;
    private Map<String, List<HideMethod>> mHideMethodMap;

    SeekerDelegateJsonGenerator(Filer filer,
            Map<String, List<HideMethod>> hideMethodMap) {
        this.mFiler = filer;
        this.mHideMethodMap = hideMethodMap;
    }

    void generate() throws IOException {
        if (mHideMethodMap.isEmpty()) {
            Log.w("hideMethodMap is empty...");
        }
        Log.title("========== Generate JSON start ==========");
        FileObject fileObject = mFiler.getResource(StandardLocation.CLASS_OUTPUT, "",
                "com/yeoh/seeker/seeker.json");
        File file = new File(fileObject.toUri());
        if (file.exists()) {
            file.delete();
        }
        Files.createParentDirs(file);
        Writer writer = Files.newWriter(file, Charsets.UTF_8);
        new Gson().toJson(mHideMethodMap, writer);
        writer.close();
        Log.title("========== Generate JSON end ==========");
    }
}
