/**
 * <p>deployTestToKubernetes</p>
 *
 * @author nekoimi 2022/05/30
 */

def call() {
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
        mv ${k8sValueYaml} ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml

        cat >> ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml <<EOF
image:
    repository: ${dockerRepository}
    tag: "${dockerTag}"
EOF

        cat > ${MY_WORKSPACE}/helm-charts/${projectName}/upgrade.yaml <<EOF
image:
    repository: ${dockerRepository}
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
            usernameVariable: 'serverUser',
            keyFileVariable: 'serverIdentity')]) {
        server.user = serverUser
        server.identityFile = serverIdentity
        // -------------------------------------------------------
        sshCommand remote: server, command: """
cd /mnt

if [ ! -e "\$PWD/helm-charts" ]; then
    git clone ${MY_GIT_HELM_CHARTS_URL} helm-charts && ls -l "\$PWD/helm-charts"
fi

cd \$PWD/helm-charts && git pull origin master

if [ -e "\$PWD/${MY_PROJECT_NAME}" ]; then
    status=\$(helm list --all --time-format "2006-01-02" --filter "${MY_PROJECT_NAME}" | sed -n '2p' | awk '{print \$5}' | sed s/[[:space:]]//g)
    
    if [ \${status} == 'failed' ]; then
        echo 'Uninstall Chart ......'
        
        helm uninstall ${MY_PROJECT_NAME}
    fi
    
    if [ \${status} == 'deployed' ]; then
        echo 'Upgrade Chart ......'
        
        cd "\$PWD/${MY_PROJECT_NAME}"
        
        helm upgrade -f upgrade.yaml ${MY_PROJECT_NAME} .
    else
        echo 'Install Chart ......'
        
        helm install ${MY_PROJECT_NAME} "${MY_PROJECT_NAME}/"
    fi
    
    helm status ${MY_PROJECT_NAME}
else
    echo 'Warning! 项目缺少helm部署chart！'
fi

"""
    }
}

