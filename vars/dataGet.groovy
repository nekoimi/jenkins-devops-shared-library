/**
 * <p>mapGet</p>
 *
 * @author nekoimi 2022/05/29
 */

def call(map, s) {
    if (map == null || s == null) {
        return null
    }
    String[] keys = s.split("[.]");
    int length = keys.length
    if (length == 1) {
        if (!map.containsKey(s)) {
            return null
        }
        return map.get(s)
    }

    for (int i = 0; i < length - 1; i++) {
        String key = keys[i]
        if (!map.containsKey(key)) {
            return null
        }
        Object m = map.get(key);
        if (m instanceof java.util.Map) {
            map = (Map) m
        } else {
            return null
        }
    }

    String key = keys[length - 1]
    if (!map.containsKey(key)) {
        return null
    }
    return map.get(key)
}
