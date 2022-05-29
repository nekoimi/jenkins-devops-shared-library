package com.yoyohr
/**
 * <p>完全使用外部Shell脚本的构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.build_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.build_after")
    if (hook_before == null || hook_after == null) {
        notice('Build Warning', "Warning！请使用 Hook 完成 Build 流程。如不需要，请忽略此警告。")
    } else {
        if (hook_before != null) {
            sh """
bash -ex;
${hook_before}
"""
        }

        if (hook_after != null) {
            sh """
bash -ex;
${hook_after}
"""
        }
    }
}

def dockerImage(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_image_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_image_after")
    if (hook_before == null || hook_after == null) {
        notice('Docker Image Warning', "Warning！请使用 Hook 完成 DockerImage 流程。如不需要，请忽略此警告。")
    } else {
        if (hook_before != null) {
            sh """
bash -ex;
${hook_before}
"""
        }

        if (hook_after != null) {
            sh """
bash -ex;
${hook_after}
"""
        }
    }
}

def dockerPush(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_push_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_push_after")
    if (hook_before == null || hook_after == null) {
        notice('Docker Push Warning', "Warning！请使用 Hook 完成 DockerPush 流程。如不需要，请忽略此警告。")
    } else {
        if (hook_before != null) {
            sh """
bash -ex;
${hook_before}
"""
        }

        if (hook_after != null) {
            sh """
bash -ex;
${hook_after}
"""
        }
    }
}

def deploy(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.deploy_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.deploy_after")
    if (hook_before == null || hook_after == null) {
        notice('Deploy Warning', "Warning！请使用 Hook 完成 Deploy 流程。如不需要，请忽略此警告。")
    } else {
        if (hook_before != null) {
            sh """
bash -ex;
${hook_before}
"""
        }

        if (hook_after != null) {
            sh """
bash -ex;
${hook_after}
"""
        }
    }
}
