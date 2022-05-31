package com.yoyohr
/**
 * <p>java项目标准构建流程</p>
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
    def commandExec = "docker run --rm -w /workspace -v /root/.m2:/root/.m2 -v \${MY_PWD}:/workspace ${buildImage}"
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

    if (buildCommand != null && buildCommand.toString().length() > 0) {
        sh """
bash -ex;

sed -i 's/<packaging>war/<packaging>jar/g' pom.xml

${commandExec} ${buildCommand}

ls -l target

jarName=\$(ls target | grep .jar\\\$)

mv target/\${jarName} target/app.jar

ls -l target
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
