#!/usr/bin/groovy
import com.yoyohr.environment.PipelineEnv
/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call() {
    // =========================================================================
    // jenkins凭据ID，git账号
    def gitCredentialId = "gitCredential"
    // jenkins凭据ID，docker registry账号
    def dockerRegistryId = "dockerRegistryCredential"
    // docker registry 地址
    def dockerRegistry = "http://registry.youpin-k8s.net"
    // k8s api server 证书ID，证书生成：ssh-keygen -t rsa -b 4096
    def k8sCredential = "k8sCredential"
    // k8s api server host
    def k8sHost = "192.168.2.209"
    // 当前工作控件
    def workspace = "${env.workspace}"
    // 任务名称
    def jobName = "${env.JOB_NAME}"
    // 任务 Build ID
    def buildId = "${env.BUILD_ID}"
    // 当前项目在宿主机目录，用来 docker in docker 时 volume 映射
    def myPwd = "/home/nfs/jenkins/data/jenkins_home/workspace/${jobName}"
    // 项目信息及构建配置
    def projectYaml = "project.yaml"
    // 项目构建环境
    def buildEnv = "$params.BUILD_ENV"
    // Git helm-charts 仓库地址，专门存放管理helm-charts的仓库地址
    def gitHelmChartsUrl = "http://code-base.yoyohr.com/kubernetes/helm-charts.git"
    // =========================================================================


    stage('Load Env') {
        def workspaceExists = fileExists workspace
        if (!workspaceExists) {
            checkout scm
        }

        def yamlConf = null

        // Project information
        def projectGroup = "library"
        def projectName = jobName
        def projectVersion = buildId

        def exists = fileExists projectYaml
        if (exists) {
            yamlConf = readYaml file: "project.yaml"
            projectGroup = dataGet(yamlConf, "group")
            projectName = dataGet(yamlConf, "name")
            projectVersion = dataGet(yamlConf, "version")
            def log = ""
            yamlConf.each { k, v ->
                log = log.concat("${k} -> ${v}\n")
            }
            notice('YamlConf', log)
        }

        // Docker image
        def dockerImage = "${projectGroup}/${projectName}:${projectVersion}.${buildId}-${buildEnv}"
        def dockerRegistryImage = "${dockerRegistry.replaceFirst("(http://)|(https://)", "")}/${dockerImage}"

        // Build env
        def isTest = (buildEnv == PipelineEnv.BuildTest)
        def isRelease = (buildEnv == PipelineEnv.BuildRelease)

        // Run with environment
        withEnv([
                "MY_WORKSPACE=${workspace}",
                "MY_JOB_NAME=${jobName}",
                "MY_BUILD_ID=${buildId}",
                "MY_PWD=${myPwd}",
                "MY_BUILD_ENV=${buildEnv}",
                "MY_GIT_ID=${gitCredentialId}",
                "MY_GIT_HELM_CHARTS_URL=${gitHelmChartsUrl}",
                "MY_K8S_ID=${k8sCredential}",
                "MY_K8S_HOST=${k8sHost}",
                "MY_DOCKER_REGISTRY=${dockerRegistry}",
                "MY_DOCKER_REGISTRY_ID=${dockerRegistryId}",
                "MY_DOCKER_IMAGE=${dockerImage}",
                "MY_DOCKER_REGISTRY_IMAGE=${dockerRegistryImage}",
                "MY_PROJECT_GROUP=${projectGroup}",
                "MY_PROJECT_NAME=${projectName}",
                "MY_PROJECT_VERSION=${projectVersion}"
        ]) {
            // ls
            sh "ls -l"

            // printenv
            sh "printenv"

            try {
                // Run
                pipelineRunner(yamlConf)
            }

//            catch (Exception e) {
//                noticeWarning(e.message)
//            }

            finally {
                cleanWs()
            }
        }
    }
}

return this
