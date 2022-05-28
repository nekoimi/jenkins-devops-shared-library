package com.yoyohr

/**
 * <p>git</p>
 *
 * @author nekoimi 2022/05/28
 */

/**
 * 拉取指定Tag的代码
 * @param repositoryUrl
 * @param gitTag
 * @return
 */
def pullTag(repositoryUrl, gitTag) {
    git tag: gitTag, credentialsId: 'devops', url: repositoryUrl
}

/**
 * 拉取指定分支代码
 * @param repositoryUrl
 * @param gitBranch
 * @return
 */
def pullBranch(repositoryUrl, gitBranch) {
    git branch: gitBranch, credentialsId: 'devops', url: repositoryUrl
}
