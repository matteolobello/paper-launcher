package com.matteolobello.launcher.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DictionaryUtil {

    public static List<Map.Entry<String, Integer>> sortHashMapByValues(HashMap<String, Integer> hashMap) {
        Set<Map.Entry<String, Integer>> set = hashMap.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        return list;
    }
}
