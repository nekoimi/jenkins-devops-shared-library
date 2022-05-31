package com.yoyohr
/**
 * <p>web项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def hookBefore = dataGet(yamlConf, "pipelineHook.buildBefore")
    def hookAfter = dataGet(yamlConf, "pipelineHook.buildAfter")
    def buildImage = dataGet(yamlConf, "buildImage")
    if (buildImage == null || buildImage.toString().length() <= 0) {
        noticeWarning('buildImage未配置!')
        return
    }
    // commandExec
    def commandExec = "docker run --rm -w /workspace -v /root/.npm:/root/.npm -v \${MY_PWD}:/workspace"
    def installCommand = dataGet(yamlConf, "installCommand")
    def buildCommand = dataGet(yamlConf, "buildCommand")

    withEnv([
            "COMMAND_EXEC=${commandExec}"
    ]) {
        if (hookBefore != null) {
            sh """
bash -ex;
${hookBefore}
"""
        }
    }

    if (installCommand != null && installCommand.toString().length() > 0) {
        sh """
bash -ex;

${commandExec} ${buildImage} ${installCommand}

ls -l
"""
    }

    if (buildCommand != null && buildCommand.toString().length() > 0) {
        sh """
bash -ex;

${commandExec} ${buildImage} ${buildCommand}

ls -l
"""
    }

    withEnv([
            "COMMAND_EXEC=${commandExec}"
    ]) {
        if (hookAfter != null) {
            sh """
bash -ex;
${hookAfter}
"""
        }
    }
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
