package com.yoyohr

/**
 * <p>git</p>
 *
 * @author nekoimi 2022/05/28
 */

/**
 * 下载指定Tag的代码
 * @param repositoryUrl
 * @param gitTag
 * @return
 */
def pullTag(repositoryUrl, gitTag) {
    git branch: gitTag, url: repositoryUrl
}
