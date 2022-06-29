package com.yoyohr
/**
 * <p>服务端项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    buildWithCache(yamlConf, "", {})
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
