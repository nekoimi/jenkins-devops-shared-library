package com.yoyohr

import com.yoyohr.environment.PipelineEnv

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

jarName=\$(ls target | grep .jar\$ | sed s/[[:space:]]//g)

if [ ! -z \$jarName ]; then
    if [ -f target/\$jarName ]; then
        mv target/\$jarName target/app.jar
    fi
fi

warName=\$(ls target | grep .war\$ | sed s/[[:space:]]//g)

if [ ! -z \$warName ]; then
    if [ -f target/\$warName ]; then
        mv target/\$warName target/app.war
    fi
fi

ls -l target
"""
}

/**
 * <p>复制结果到指定目录</p>
 * @param nameOverride
 * @param path
 */
def doBuildResultCopyToPath(nameOverride, path) {
    if (!path.toString().endsWith("/")) {
        path = path.toString().concat("/")
    }

    def jarExists = fileExists "target/app.jar"
    if (jarExists) {
        sh """
bash -ex;

if [ -d "${path}" ]; then
    echo 'Copy app.jar To ${path}${nameOverride}.jar......'
    cp -rf target/app.jar "${path}${nameOverride}.jar"
else
    echo 'Warning! Path ${path} does exists!'
    exit 1
fi

"""
    }

    def warExists = fileExists "target/app.war"
    if (warExists) {
        sh """
bash -ex;

if [ -d "${path}" ]; then
    echo 'Copy app.war To ${path}${nameOverride}.war......'
    cp -rf target/app.war "${path}${nameOverride}.war"
else
    echo 'Warning! Path ${path} does exists!'
    exit 1
fi

"""
    }
}

/**
 * <p>复制结果到指定git仓库</p>
 * @param nameOverride
 * @param gitUrl
 */
def doBuildResultCopyToGit(nameOverride, gitUrl) {
    withCredentials([gitUsernamePassword(credentialsId: "${MY_GIT_ID}")]) {
        def warExists = fileExists "target/app.war"
        if (warExists) {
            sh """
bash -ex;

"""
        }
    }
}

def build(yamlConf) {
    def buildImage = dataGet(yamlConf, "buildImage")
    if (stringIsNotEmpty(buildImage)) {
        def buildCommand = dataGet(yamlConf, "buildCommand")
        // commandExec
        def commandExec = "docker run --rm -w /workspace -v /root/.m2:/root/.m2 -v ${MY_PWD}:/workspace ${buildImage}"

        runHook(yamlConf, "buildBefore", commandExec)

        if (stringIsNotEmpty(buildCommand)) {
            withEnv([
                    "MY_COMMAND_EXEC=${commandExec}"
            ]) {
                doBuild(buildCommand)

                if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
                    def nameOverride = dataGet(yamlConf, "testCopy.nameOverride")
                    if (stringIsEmpty(nameOverride)) {
                        nameOverride = "${MY_PROJECT_NAME}"
                    }
                    def copyGit = dataGet(yamlConf, "testCopy.git")
                    def copyPath = dataGet(yamlConf, "testCopy.path")
                    if (stringIsNotEmpty(copyGit)) {
                        doBuildResultCopyToGit(nameOverride, copyGit)
                    }
                    if (stringIsNotEmpty(copyPath)) {
                        doBuildResultCopyToPath(nameOverride, copyPath)
                    }
                }

                if ("${MY_BUILD_ENV}" == PipelineEnv.BuildRelease) {
                    def nameOverride = dataGet(yamlConf, "releaseCopy.nameOverride")
                    if (stringIsEmpty(nameOverride)) {
                        nameOverride = "${MY_PROJECT_NAME}"
                    }
                    def copyGit = dataGet(yamlConf, "releaseCopy.git")
                    def copyPath = dataGet(yamlConf, "releaseCopy.path")
                    if (stringIsNotEmpty(copyGit)) {
                        doBuildResultCopyToGit(nameOverride, copyGit)
                    }
                    if (stringIsNotEmpty(copyPath)) {
                        doBuildResultCopyToPath(nameOverride, copyPath)
                    }
                }
            }
        }

        runHook(yamlConf, "buildAfter", commandExec)
    }
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
