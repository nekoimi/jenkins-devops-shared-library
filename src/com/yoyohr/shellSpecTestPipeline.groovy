package com.yoyohr
/**
 * <p>完全使用外部Shell脚本的构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def build_before = dataGet(yamlConf, "pipeline_hook.build_before")
    def build_after = dataGet(yamlConf, "pipeline_hook.build_after")
    if (build_before == null || build_after == null) {
        notice('Build Warning', """
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 Build 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
    } else {
        if (build_before != null) {
            sh """
bash -ex
${build_before}
"""
        }

        if (build_after != null) {
            sh """
bash -ex
${build_after}
"""
        }
    }
}

def dockerImage(yamlConf) {
    notice('Docker Image Warning', """
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 DockerImage 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}

def dockerPush(yamlConf) {
    notice('Docker Push Warning', """
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 dockerPush 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}

def deploy(yamlConf) {
    notice('Deploy Warning', """
Warning！当前项目 project.yaml 配置文件不存在，请使用 Hook 完成 deploy 流程。
Hook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
""")
}
