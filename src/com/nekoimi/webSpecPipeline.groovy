package com.nekoimi
/**
 * <p>web项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    buildWithCache(yamlConf, "-v /root/.npm:/root/.npm", {})

    buildResultCopy(yamlConf, "dist", "${MY_PROJECT_NAME}")
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
