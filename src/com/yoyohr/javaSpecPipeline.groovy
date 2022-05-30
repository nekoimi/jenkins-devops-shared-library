package com.yoyohr
/**
 * <p>java项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.build_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.build_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

    sh """
bash -ex;

sed -i 's/<packaging>war/<packaging>jar/g' pom.xml

docker run --rm -w /work -v /root/.m2:/root/.m2 -v \${MY_PWD}:/work maven:3.6-openjdk-11 mvn clean package -Dfile.encoding=UTF-8 -DskipTests=true

ls -l target

jarName=\$(ls target | grep .jar\\\$)

mv target/\${jarName} target/app.jar

ls -l target
"""

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def unitTesting(yamlConf) {
    noticeWarning("Warning！构建流程不支持。")
}

def docker(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

//    docker.withRegistry("${MY_DOCKER_REGISTRY}", "${MY_DOCKER_REGISTRY_ID}") {
//        docker.build("${MY_PROJECT_GROUP}/${MY_PROJECT_NAME}").push("${MY_PROJECT_VERSION}-${MY_BUILD_ENV}")
//    }

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def helm(yamlConf) {
    withCredentials([gitUsernamePassword(credentialsId: "${MY_GIT_ID}")]) {
        sh """
bash -ex;

git clone \${MY_GIT_HELM_CHARTS_URL} helm-charts

ls -l

ls -l helm-charts        
"""
    }
}

def deploy(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.deploy_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.deploy_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def testing(yamlConf) {
    noticeWarning("Warning！构建流程不支持。")
}
