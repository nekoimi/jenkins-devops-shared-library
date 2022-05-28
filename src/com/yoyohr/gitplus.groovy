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
 * @param gitUrl
 * @param gitTag
 * @return
 */
def pullTag(gitUrl, gitTag) {
    git changelog: true, branch: gitTag, credentialsId: gitDevOpsId(), url: gitUrl
}

/**
 * 拉取指定分支代码
 * @param gitUrl
 * @param gitBranch
 * @return
 */
def pullBranch(gitUrl, gitBranch) {
    git changelog: true, branch: gitBranch, credentialsId: gitDevOpsId(), url: gitUrl
}
