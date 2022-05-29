package com.yoyohr
/**
 * <p>完全使用外部Shell脚本的构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build() {
    noticeWarning("""
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 Build 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}

def dockerImage() {
    noticeWarning("""
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 DockerImage 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}

def dockerPush() {
    noticeWarning("""
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 dockerPush 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}

def deploy() {
    noticeWarning("""
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 deploy 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}
