package com.yoyohr

import com.yoyohr.environment.PipelineEnv

/**
 * <p>java项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def buildImage = dataGet(yamlConf, "buildImage")
    def buildCommand = dataGet(yamlConf, "buildCommand")
    sh """
bash -ex;

sed -i 's/<packaging>war/<packaging>jar/g' pom.xml

docker run --rm -w /workspace -v /root/.m2:/root/.m2 -v \${MY_PWD}:/workspace ${buildImage} ${buildCommand}

ls -l target

jarName=\$(ls target | grep .jar\\\$)

mv target/\${jarName} target/app.jar

ls -l target
"""
}

def unitTesting(yamlConf) {
}

def docker(yamlConf) {
    dockerBuildAndPush(yamlConf)
}

def deploy(yamlConf) {
    if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
        deployTestToKubernetes()
    }

    if ("${MY_BUILD_ENV}" == PipelineEnv.BuildRelease) {
        notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
    }
}

def testing(yamlConf) {
    if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
        deployTesting()
    }
}
