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
