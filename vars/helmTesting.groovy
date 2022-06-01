/**
 * <p>deployTesting</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(yamlConf) {
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

if [ -e "${apiServerMntPath}/helm-charts/${MY_PROJECT_NAME}" ]; then
    status=\$(helm list --all --time-format "2006-01-02" --filter "${MY_PROJECT_NAME}" | sed -n '2p' | awk '{print \$5}' | sed s/[[:space:]]//g)
    
    echo '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Helm Test <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
    if [[ \${status} == 'failed' ]]; then
        echo 'Deploy To Kubernetes failed!'
    fi
    
    if [[ \${status} == 'deployed' ]]; then
        helm test ${MY_PROJECT_NAME}
    fi
    echo '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Helm Test <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<'
fi

"""
    }
}
