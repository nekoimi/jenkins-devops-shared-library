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
def lsFile(path = "") {
    echo "Ls ${path}:"
    sh """
ls -l ${path}
"""
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
