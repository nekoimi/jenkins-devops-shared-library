/**
 * <p>mapGet</p>
 *
 * @author nekoimi 2022/05/29
 */

def call(map, s) {
    if (map == null || s == null) {
        noticeWarning('获取配置为空!')
        return null
    }
    String[] keys = s.split("[.]");
    int length = keys.length
    if (length == 1) {
        if (!map.containsKey(s)) {
            noticeWarning("键 ${s} 不存在!")
            return null
        }
        return map.get(s)
    }

    for (int i = 0; i < length - 1; i++) {
        String key = keys[i]
        if (!map.containsKey(key)) {
            noticeWarning("键 ${key} 不存在!")
            return null
        }
        Object m = map.get(key);
        if (m instanceof java.util.Map) {
            map = (Map) m
        } else {
            noticeWarning("键 ${key} 不存在!")
            return null
        }
    }

    String key = keys[length - 1]
    if (!map.containsKey(key)) {
        noticeWarning("键 ${key} 不存在!")
        return null
    }
    def result = map.get(key)
    if (result == null || result.toString().length() <= 0) {
        noticeWarning("键 ${key} 为空!")
    }
    return result
}
