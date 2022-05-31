package com.yoyohr
/**
 * <p>完全使用外部Shell脚本的构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def hookBefore = dataGet(yamlConf, "pipelineHook.buildBefore")
    def hookAfter = dataGet(yamlConf, "pipelineHook.buildAfter")
    if (hookBefore != null) {
        sh """
bash -ex;
${hookBefore}
"""
    }

    if (hookAfter != null) {
        sh """
bash -ex;
${hookAfter}
"""
    }
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
