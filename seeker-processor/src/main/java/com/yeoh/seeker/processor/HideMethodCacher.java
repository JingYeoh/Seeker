package com.yeoh.seeker.processor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.yeoh.seeker.HideMethod;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to cache {@link com.yeoh.seeker.HideMethod} to memory .
 *
 * @author jingyeoh .
 * @since 2018-09-30
 */
class HideMethodCacher {

    private static Map<String, List<HideMethod>> mHideMethodMap = new HashMap<>();

    static void putAll(@Nullable Map<String, List<HideMethod>> hideMethodMap) {
        if (hideMethodMap == null || hideMethodMap.isEmpty()) {
            return;
        }
        for (String className: hideMethodMap.keySet()) {
            if (!mHideMethodMap.containsKey(className)) {
                mHideMethodMap.put(className, hideMethodMap.get(className));
            }
        }
    }

    static void readFromCache() throws IOException {
        File file = new File("./build/Seeker/seeker.json");
        if (!file.exists()) {
            return;
        }
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(reader);
        StringBuilder content = new StringBuilder();
        content.append(br.readLine());
        br.lines().forEach(content::append);
        String jsonStr = content.toString();
        if (jsonStr.isEmpty()) {
            return;
        }
        Gson gson = new Gson();
        Map map = gson.fromJson(jsonStr, Map.class);
        putAll(map);
    }

    static void cache() throws IOException {
        File file = new File("./build/Seeker/seeker.json");
        if (file.exists()) {
            file.delete();
        }
        Files.createParentDirs(file);
        Writer writer = Files.newWriter(file, Charsets.UTF_8);
        new Gson().toJson(mHideMethodMap, writer);
        writer.close();
    }

    @NonNull
    static Map<String, List<HideMethod>> getAll() {
        return mHideMethodMap;
    }
}
