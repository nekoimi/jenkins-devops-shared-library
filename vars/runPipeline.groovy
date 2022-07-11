#!/usr/bin/groovy
import com.nekoimi.environment.PipelineEnv
import com.nekoimi.utils.YamlUtils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call() {
    // =========================================================================
    // jenkins凭据ID，git账号
    def gitCredentialId = "${env.GITLAB_CREDENTIAL}"
    // jenkins凭据ID，dockerhub 账号
    def dockerRegistryId = "${env.DOCKER_CREDENTIAL}"
    // docker registry 地址
    def dockerRegistry = "${env.DOCKER_REGISTRY}"
    // k8s api server 证书ID，证书生成：ssh-keygen -t rsa -b 4096
    def k8sCredential = "${env.K8S_API_SERVER_CREDENTIAL}"
    // k8s api server host
    def k8sHost = "${env.K8S_API_SERVER_HOST}"
    // 当前工作空间
    def workspace = "${env.workspace}"
    // 任务名称
    def jobName = "${env.JOB_NAME}"
    // 任务 Build ID
    def buildId = "${env.BUILD_ID}"
    // 根目录空间
    def baseWorkspace = workspace.toString().replaceAll(jobName, "")
    if (baseWorkspace.endsWith("/")) {
        baseWorkspace = baseWorkspace.substring(0, baseWorkspace.length() - 1)
    }
    // 当前项目在宿主机目录，用来 docker in docker 时 volume 映射
    def myPwd = "${env.JENKINS_HOME_VOLUME}/workspace/${jobName}"
    // 项目构建环境
    def buildEnv = "$params.BUILD_ENV"
    // Git helm-charts 仓库地址，专门存放管理helm-charts的仓库地址
    def gitHelmChartsUrl = "${env.HELM_CHART_REPOSITORY}"
    // 项目信息及构建配置
    def projectYaml = "project.yaml"
    // =========================================================================


    stage('Checkout') {
        // 清除工作区
        echo ">>>>>>>>>>>>>>> 初始化工作区 - START <<<<<<<<<<<<<<<<"
        cleanWs()
        echo ">>>>>>>>>>>>>>> 初始化工作区 - END <<<<<<<<<<<<<<<<"

        def workspaceExists = fileExists workspace
        if (!workspaceExists) {
            checkout scm
        }

        // ls
        sh "ls -l"

        // pwd
        sh "pwd"

        def yamlConf = null

        // Project information
        def projectGroup = "library"
        def projectName = jobName
        def projectVersion = buildId
        def projectDescription = jobName

        def exists = fileExists "project.yaml"
        if (!exists) {
            exists = fileExists "project.yml"
            if (exists) {
                projectYaml = "project.yml"
            }
        }
        if (exists) {
            yamlConf = readYaml file: "${projectYaml}"
            projectGroup = YamlUtils.get(yamlConf, "group")
            projectName = YamlUtils.get(yamlConf, "name")
            projectVersion = YamlUtils.get(yamlConf, "version")
            projectDescription = YamlUtils.get(yamlConf, "description")
            def log = ""
            yamlConf.each { k, v ->
                log = log.concat("${k} -> ${v}\n")
            }
            notice('YamlConf', log)
        } else {
            sh """
echo 'project.yaml does not exists!' && exit 1
"""
        }

        // Docker image
        def dockerImage = "${projectGroup}/${projectName}:${projectVersion}.${buildId}-${buildEnv}"
        def dockerRegistryImage = "${dockerRegistry.replaceFirst("(http://)|(https://)", "")}/${dockerImage}"

        // Build env
        def isTest = (buildEnv == PipelineEnv.BuildTest)
        def isRelease = (buildEnv == PipelineEnv.BuildRelease)

        // Run with environment
        withEnv([
                "BASE_WORKSPACE=${baseWorkspace}",
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
                "MY_PROJECT_VERSION=${projectVersion}",
                "MY_PROJECT_DESCRIPTION=${projectDescription}"
        ]) {
            // ls
            sh "ls -l"

            // printenv
            sh "printenv"

            try {
                // Run stages
                runPipelineStages(yamlConf)
            }

//            catch (Exception e) {
//                println(e.message)
//            }

            finally {
                echo ">>>>>>>>>>>>>>> 清除工作区 - START <<<<<<<<<<<<<<<<"
                cleanWs()
                echo ">>>>>>>>>>>>>>> 清除工作区 - END <<<<<<<<<<<<<<<<"
            }
        }
    }
}

return this
