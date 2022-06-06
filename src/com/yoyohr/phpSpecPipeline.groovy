package com.yoyohr
/**
 * <p>php项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def doBuild(command) {
    sh """
bash -ex;

\${MY_COMMAND_EXEC} ${command}

ls -l
"""
}

def build(yamlConf) {
    def buildImage = dataGet(yamlConf, "buildImage")
    if (stringIsNotEmpty(buildImage)) {
        def installCommand = dataGet(yamlConf, "installCommand")
        def buildCommand = dataGet(yamlConf, "buildCommand")
        // commandExec
        def commandExec = "docker run --rm -w /workspace -v /root/.composer:/root/.composer -v ${MY_PWD}:/workspace ${buildImage}"

        runHook(yamlConf, "buildBefore", commandExec)

        if (stringIsNotEmpty(installCommand)) {
            withEnv([
                    "MY_COMMAND_EXEC=${commandExec}"
            ]) {
                doBuild(installCommand)
            }
        }

        if (stringIsNotEmpty(buildCommand)) {
            withEnv([
                    "MY_COMMAND_EXEC=${commandExec}"
            ]) {
                doBuild(buildCommand)
            }
        }

        runHook(yamlConf, "buildAfter", commandExec)
    } else {
        runHook(yamlConf, "buildBefore", "")
        runHook(yamlConf, "buildAfter", "")
    }
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
