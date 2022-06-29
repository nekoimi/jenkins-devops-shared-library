package com.yoyohr.utils

/**
 * <p>YamlUtils</p>
 *
 * @author nekoimi 2022/06/29
 */
class YamlUtils {

    /**
     * 读取数据
     * @param yamlMap
     * @param dataKey
     * @return
     */
    public static Object get(Map<String, Object> yamlMap, String dataKey) {
        if (yamlMap == null || dataKey == null) {
            return null
        }
        String[] keys = dataKey.split("[.]");
        int length = keys.length
        if (length == 1) {
            if (!yamlMap.containsKey(dataKey)) {
                return null
            }
            return yamlMap.get(dataKey)
        }

        for (int i = 0; i < length - 1; i++) {
            String key = keys[i]
            if (!yamlMap.containsKey(key)) {
                return null
            }
            Object m = yamlMap.get(key);
            if (m instanceof java.util.Map) {
                yamlMap = (Map) m
            } else {
                return null
            }
        }

        String key = keys[length - 1]
        if (!yamlMap.containsKey(key)) {
            return null
        }
        return yamlMap.get(key)
    }

}
