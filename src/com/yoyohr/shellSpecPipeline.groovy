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

def unitTesting(yamlConf) {
    noticeWarning("Warning！构建流程不支持，请忽略此警告。")
}

def docker(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_after")
    if (hook_before == null || hook_after == null) {
        notice('Docker Warning', "Warning！请使用 Hook 完成 Docker 流程。如不需要，请忽略此警告。")
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

def helm(yamlConf) {
    noticeWarning("Warning！构建流程不支持，请忽略此警告。")
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

def testing(yamlConf) {
    noticeWarning("Warning！构建流程不支持，请忽略此警告。")
}
