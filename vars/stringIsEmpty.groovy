/**
 * <p>stringIsEmpty</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(obj) {
    if (obj == null) {
        return true
    }
    return obj.toString().trim().length() <= 0
}
