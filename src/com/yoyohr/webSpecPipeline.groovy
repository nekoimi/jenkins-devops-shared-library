package com.yoyohr

import com.yoyohr.environment.PipelineEnv

/**
 * <p>web项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
}

def unitTesting(yamlConf) {
}

def docker(yamlConf) {
    dockerBuildAndPush(yamlConf)
}

def deploy(yamlConf) {
}

def testing(yamlConf) {
    if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
        deployTesting()
    }
}
