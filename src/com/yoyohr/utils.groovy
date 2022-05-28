package com.yoyohr
/**
 * <p>utils</p>
 *
 * @author nekoimi 2022/05/28
 */

/**
 * 列出文件
 * @param path
 */
def lsFile(GString path = "") {
    println("ls ${path}:")
    sh """
ls -l ${path}
"""
}

/**
 * 删除文件
 * @param path
 */
def rmFile(GString path) {
    echo "rm ${path}"
    sh """
rm -rf ${path}
"""
}
