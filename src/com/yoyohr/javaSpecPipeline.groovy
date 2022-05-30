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

    docker.withRegistry("${MY_DOCKER_REGISTRY}", "${MY_DOCKER_REGISTRY_ID}") {
        docker.build("${MY_PROJECT_GROUP}/${MY_PROJECT_NAME}").push("${MY_PROJECT_VERSION}.${MY_BUILD_ID}-${MY_BUILD_ENV}")
    }

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def deployTestToKubernetes() {
    def server = [:]
    server.name = "api-server"
    server.host = "${MY_K8S_HOST}"
    server.allowAnyHosts= true
    withCredentials([sshUserPrivateKey(
            credentialsId: "${MY_K8S_ID}",
            usernameVariable: 'user',
            keyFileVariable: 'identity')]) {
        server.user = user
        server.identityFile = identity
        // -------------------------------------------------------
        sshCommand remote: server, command: """
bash -ex;

chartStatus=\$(helm list --all --time-format "2006-01-02" --filter "${MY_PROJECT_NAME}" | sed -n '2p' | awk '{print \$5}')

echo chartStatus
"""
    }


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

    if ("${IS_TEST}") {
        deployTestToKubernetes()
    }

    if ("${IS_RELEASE}") {
        notice('生产环境部署', '生产环境忽略部署')
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
