package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * DictionaryLoader
 * مسؤول عن قراءة ملف الـ JSON وتحويله إلى HashMap في الذاكرة
 */
public class DictionaryLoader {

    private HashMap<String, String> dictionary;

    public DictionaryLoader(String filePath) throws IOException {
        this.dictionary = loadFromFile(filePath);
        System.out.println("✅ Dictionary loaded: " + dictionary.size() + " words.");
    }

    /**
     * يقرأ ملف JSON ويحوله إلى HashMap
     * المفتاح: الكلمة (lowercase)
     * القيمة: التعريف
     */
    private HashMap<String, String> loadFromFile(String filePath) throws IOException {

        // 1. قراءة محتوى الملف كامل
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        // 2. تحويل JSON إلى Map باستخدام Gson
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> rawMap = gson.fromJson(content.toString(), mapType);

        if (rawMap == null) {
            throw new IOException("❌ Failed to parse dictionary file: " + filePath);
        }

        // 3. تحويل كل الكلمات إلى lowercase لتسهيل البحث
        HashMap<String, String> normalizedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : rawMap.entrySet()) {
            normalizedMap.put(entry.getKey().toLowerCase().trim(), entry.getValue());
        }

        return normalizedMap;
    }

    /**
     * البحث عن كلمة في القاموس
     * بترجع التعريف أو null إذا ما لقت الكلمة
     */
    public String search(String word) {
        if (word == null || word.trim().isEmpty()) {
            return null;
        }
        return dictionary.get(word.toLowerCase().trim());
    }

    /**
     * بترجع عدد الكلمات الموجودة في القاموس
     */
    public int getSize() {
        return dictionary.size();
    }
}
