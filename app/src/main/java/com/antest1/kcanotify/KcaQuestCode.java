package com.antest1.kcanotify;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class KcaQuestCode {
    public final static String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String convert_to_code(String key_, JsonArray count) {
        String text = "";
        int key = Integer.parseInt(key_);
        int key_high = key / chars.length();
        int key_low = key % chars.length();

        StringBuilder sb = new StringBuilder();
        sb.append(chars.charAt(key_high));
        sb.append(chars.charAt(key_low));

        for (int i = 0; i < count.size(); i++) {
            if (count.get(i).getAsInt() >= chars.length()) {
                if (key == 214) {
                    sb.append("=");
                } else if (key == 221) {
                    sb.append("=");
                    sb.append(chars.charAt(count.get(i).getAsInt() % chars.length()));
                }
            } else {
                sb.append(chars.charAt(count.get(i).getAsInt()));
            }
        }
        String value = sb.toString();
        String size = String.valueOf(value.length());
        return size.concat(value);
    }

    public static JsonArray decode_code(String code) {
        JsonArray code_list = new JsonArray();
        int key = 0;
        while (key < code.length()) {
            StringBuilder sb = new StringBuilder();
            int size = Integer.parseInt(code.substring(key, key+1));
            key += 1;
            for (int i = 0; i < size; i++) {
                sb.append(code.charAt(key+i));
            }
            char[] sb_result = sb.toString().trim().toCharArray();
            String quest_code = String.valueOf(chars.indexOf(sb_result[0]) * chars.length() + chars.indexOf(sb_result[1]));
            JsonArray quest_count = new JsonArray();
            for (int i = 2; i < sb_result.length; i++) {
                if (sb_result[i] == '=') {
                    if (quest_code.equals("214")) {
                        quest_count.add(chars.length());
                    } else if (quest_code.equals("221")) {
                        int count_low = chars.indexOf(sb_result[i+1]);
                        quest_count.add(chars.length() + count_low);
                        break;
                    }
                } else {
                    quest_count.add(chars.indexOf(sb_result[i]));
                }
            }
            JsonObject item = new JsonObject();
            item.addProperty("code", quest_code);
            item.add("cond", quest_count);
            code_list.add(item);
            key += size;
        }
        return code_list;
    }
}
