package com.yoyohr.utils
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
    public static def lsFile(path = "") {
        echo "Ls ${path}:"
        sh """
ls -l ${path}
"""
    }

    /**
     * 删除文件
     * @param path
     */
    public static def rmFile(path) {
        echo "Rm ${path}"
        sh """
rm -rf ${path}
"""
    }
}
