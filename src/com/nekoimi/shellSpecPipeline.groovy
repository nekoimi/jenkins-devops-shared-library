package com.nekoimi
/**
 * <p>完全使用外部Shell脚本的构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    runHook(yamlConf, "buildBefore", "")
    runHook(yamlConf, "buildAfter", "")
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
