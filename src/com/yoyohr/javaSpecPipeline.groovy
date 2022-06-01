package com.yoyohr
/**
 * <p>java项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def doBuild(command) {
    sh """
bash -ex;

\${MY_COMMAND_EXEC} ${command}

ls -l target

jarName=\$(ls target | grep .jar\\\$)

mv target/\${jarName} target/app.jar

ls -l target
"""
}

def build(yamlConf) {
    def buildImage = dataGet(yamlConf, "buildImage")
    def buildCommand = dataGet(yamlConf, "buildCommand")
    if (stringIsEmpty(buildImage)) {
        noticeWarning('buildImage未配置!')
        return
    }
    // commandExec
    def commandExec = "docker run --rm -w /workspace -v /root/.m2:/root/.m2 -v ${MY_PWD}:/workspace ${buildImage}"

    runHook(yamlConf, "buildBefore", commandExec)

    if (stringIsNotEmpty(buildCommand)) {
        withEnv([
                "MY_COMMAND_EXEC=${commandExec}"
        ]) {
            doBuild(buildCommand)
        }
    }

    runHook(yamlConf, "buildAfter", commandExec)
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
