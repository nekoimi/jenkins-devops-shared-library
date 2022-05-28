package com.yoyohr

/**
 * <p>git</p>
 *
 * @author nekoimi 2022/05/28
 */

/**
 * jenkins上devops的git账号凭据ID
 * @return
 */
def gitDevOpsId() {
    def devops = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    return devops
}

/**
 * 拉取指定Tag的代码
 * @param repositoryUrl
 * @param gitTag
 * @return
 */
def pullTag(repositoryUrl, gitTag) {
    git tag: gitTag, credentialsId: gitDevOpsId(), url: repositoryUrl
}

/**
 * 拉取指定分支代码
 * @param repositoryUrl
 * @param gitBranch
 * @return
 */
def pullBranch(repositoryUrl, gitBranch) {
    git branch: gitBranch, credentialsId: gitDevOpsId(), url: repositoryUrl
}
