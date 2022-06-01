/**
 * <p>deployTestToKubernetes</p>
 *
 * @author nekoimi 2022/05/30
 */

def call() {
    def apiServerMntPath = "/mnt"
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
cd ${apiServerMntPath}

if [ ! -e "${apiServerMntPath}/helm-charts" ]; then
    git clone ${MY_GIT_HELM_CHARTS_URL} helm-charts && ls -l "${apiServerMntPath}/helm-charts"
fi

cd ${apiServerMntPath}/helm-charts && git pull origin master

if [ -e "${apiServerMntPath}/helm-charts/${MY_PROJECT_NAME}" ]; then
    status=\$(helm list --all --time-format "2006-01-02" --filter "${MY_PROJECT_NAME}" | sed -n '2p' | awk '{print \$5}' | sed s/[[:space:]]//g)
    
    if [ \${status} == 'failed' ]; then
        echo 'Uninstall Chart ......'
        
        helm uninstall ${MY_PROJECT_NAME}
    fi
    
    if [ \${status} == 'deployed' ]; then
        echo 'Upgrade Chart ......'
        
        cd "${apiServerMntPath}/helm-charts/${MY_PROJECT_NAME}"
        
        helm upgrade -f values.yaml ${MY_PROJECT_NAME} .
    else
        echo 'Install Chart ......'
        
        helm install ${MY_PROJECT_NAME} "${MY_PROJECT_NAME}/"
    fi
    
    echo '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Helm Status <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
    helm status ${MY_PROJECT_NAME}
    echo '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Helm Status <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
else
    echo 'Warning! 项目缺少helm部署chart！'
fi

"""
    }
}

