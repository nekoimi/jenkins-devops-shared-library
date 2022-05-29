package com.yoyohr
/**
 * <p>utils</p>
 *
 * @author nekoimi 2022/05/28
 */

class Utils {

    /**
     * 列出文件
     * @param path
     */
    def lsFile(path = "") {
        echo "Ls ${path}:"
        sh """
ls -l ${path}
"""
    }

    /**
     * 文件是否存在
     * @param path
     */
    def fileExists(path) {
        File file = new File(path)
        if (file.exists()) {
            echo "Exists: ${path}"
            return true
        }
        echo "Not Exists: ${path}"
        return false
    }

    /**
     * 删除文件
     * @param path
     */
    def rmFile(path) {
        echo "Rm ${path}"
        sh """
rm -rf ${path}
"""
    }
}