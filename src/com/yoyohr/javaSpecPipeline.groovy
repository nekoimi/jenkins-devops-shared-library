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
    def projectName = "${MY_PROJECT_NAME}"
    def k8sValueYaml = "${MY_WORKSPACE}/k8s/${MY_BUILD_ENV}-values.yaml"
    def dockerImage = "${MY_DOCKER_REGISTRY_IMAGE}"
    def dockerRepository = dockerImage.split("[:]").first()
    def dockerTag = dockerImage.split("[:]").last()
    // 更新 Helm values.yaml 文件
    withCredentials([gitUsernamePassword(credentialsId: "${MY_GIT_ID}")]) {
        sh """
bash -ex;

if [ -f "${k8sValueYaml}" ]; then
    git clone \${MY_GIT_HELM_CHARTS_URL} helm-charts && ls -l helm-charts
    
    if [ -e "${MY_WORKSPACE}/helm-charts/${projectName}" ]; then
        if [ ! -f "${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml" ]; then
            mv ${k8sValueYaml} ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml
        fi
        
        echo '' > ${MY_WORKSPACE}/helm-charts/${projectName}/upgrade.yaml

        cat > ${MY_WORKSPACE}/helm-charts/${projectName}/upgrade.yaml <<EOF
image:
    repository: ${dockerRepository}
    pullPolicy: Alawys
    tag: "${dockerTag}"
EOF

        cd helm-charts && git add . && git commit -m "${MY_JOB_NAME}-${MY_BUILD_ENV}-${MY_BUILD_ID}" && git push origin master
    else
        echo 'Warning! 项目缺少helm部署chart！'
    fi
else
    echo 'Warning! 项目缺少k8s部署配置文件！'
fi
"""
    }

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
cd /mnt

if [ ! -e "\$PWD/helm-charts" ]; then
    git clone ${MY_GIT_HELM_CHARTS_URL} helm-charts && ls -l "\$PWD/helm-charts"
fi

if [ -e "\$PWD/helm-charts/${MY_PROJECT_NAME}" ]; then
    status=\$(helm list --all --time-format "2006-01-02" --filter "${MY_PROJECT_NAME}" | sed -n '2p' | awk '{print \$5}')
    
    if [ status == 'deployed' ]; then
        echo 'Upgrade Chart ......'
        
        cd "\$PWD/helm-charts/${MY_PROJECT_NAME}"
        
        helm upgrade -f upgrade.yaml ${MY_PROJECT_NAME} "${MY_PROJECT_NAME}/"
    else
        echo 'Install Chart ......'
        
        cd "\$PWD/helm-charts"
        
        helm install ${MY_PROJECT_NAME} "${MY_PROJECT_NAME}/"
    fi
    
    helm status ${MY_PROJECT_NAME}
else
    echo 'Warning! 项目缺少helm部署chart！'
fi

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
