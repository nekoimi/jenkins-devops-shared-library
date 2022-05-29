#!/usr/bin/groovy
import static com.yoyohr.environment.PipelineEnv.BuildTest
import static com.yoyohr.environment.PipelineEnv.BuildRelease
import static com.yoyohr.environment.PipelineEnv.GroupShell
import static com.yoyohr.environment.PipelineEnv.GroupPhp
import static com.yoyohr.environment.PipelineEnv.GroupJava
import static com.yoyohr.environment.PipelineEnv.GroupGo
import com.yoyohr.shellSpecPipeline
import com.yoyohr.javaSpecTestPipeline
import com.yoyohr.javaSpecReleasePipeline
import com.yoyohr.phpSpecTestPipeline
import com.yoyohr.phpSpecReleasePipeline
import com.yoyohr.goSpecTestPipeline
import com.yoyohr.goSpecReleasePipeline
import com.yoyohr.unknowPipeline

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
    def dockerRegistryUser = "admin"
    def dockerRegistryPassword = "123456"
    // docker registry 地址
    def dockerRegistry = "http://registry.youpin-k8s.net"
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
    // =========================================================================
    factory = [
            "${GroupShell}-${BuildTest}"   : new shellSpecPipeline(),
            "${GroupShell}-${BuildRelease}": new shellSpecPipeline(),

            "${GroupPhp}-${BuildTest}"     : new phpSpecTestPipeline(),
            "${GroupPhp}-${BuildRelease}"  : new phpSpecReleasePipeline(),

            "${GroupJava}-${BuildTest}"    : new javaSpecTestPipeline(),
            "${GroupJava}-${BuildRelease}" : new javaSpecReleasePipeline(),

            "${GroupGo}-${BuildTest}"      : new goSpecTestPipeline(),
            "${GroupGo}-${BuildRelease}"   : new goSpecReleasePipeline()
    ]


    stage('LoadEnv') {
        def workspaceExists = fileExists workspace
        if (!workspaceExists) {
            checkout scm
        }

        def pipelineInformation = "Pipeline:\n"
        factory.each { k, v ->
            pipelineInformation = pipelineInformation.concat("${k} -> ${v}\n")
        }

        def yamlConf = null

        // Project information
        def projectGroup = "library"
        def projectName = jobName
        def projectVersion = buildId

        def exists = fileExists projectYaml
        if (exists) {
            pipelineInformation = pipelineInformation.concat("\nYamlConf: \n")
            yamlConf = readYaml file: "project.yaml"
            projectGroup = dataGet(yamlConf, "group")
            projectName = dataGet(yamlConf, "name")
            projectVersion = dataGet(yamlConf, "version")
            yamlConf.each { k, v ->
                pipelineInformation = pipelineInformation.concat("${k} -> ${v}\n")
            }
        }

        // Docker image
        def dockerImage = "${projectGroup}/${projectName}:${projectVersion}-${buildEnv}"
        def dockerRegistryImage = "${dockerRegistry.replaceFirst("(http://)|(https://)", "")}/${dockerImage}"

        // Run with environment
        withEnv([
                "MY_WORKSPACE=${workspace}",
                "MY_JOB_NAME=${jobName}",
                "MY_PWD=${myPwd}",
                "MY_BUILD_ENV=${buildEnv}",
                "MY_GIT_ID=${gitCredentialId}",
                "MY_DOCKER_REGISTRY_USER=${dockerRegistryUser}",
                "MY_DOCKER_REGISTRY_PASSWORD=${dockerRegistryPassword}",
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

            notice('Pipeline Information', pipelineInformation)

            try {
                doRunPipeline(yamlConf, buildEnv)
            } finally {
                cleanWs()
            }
        }
    }
}

/**
 * 按照顺序执行Pipeline
 * @param yamlConf
 * @param buildEnv
 * @return
 */
def doRunPipeline(yamlConf, buildEnv) {
    def pipelineGroup = "${GroupShell}"
    if (yamlConf != null) {
        pipelineGroup = dataGet(yamlConf, "pipeline")
    }
    echo "Using build: ${pipelineGroup}-${buildEnv}"
    def pipeline = null
    if (factory.containsKey("${pipelineGroup}-${buildEnv}")) {
        pipeline = factory.get("${pipelineGroup}-${buildEnv}")
    } else {
        pipeline = new unknowPipeline()
    }

    stage('Build') {
        pipeline.build(yamlConf)
    }

    stage('Docker') {
        pipeline.docker(yamlConf)
    }

    stage('Deploy') {
        pipeline.deploy(yamlConf)
    }
}

return this
