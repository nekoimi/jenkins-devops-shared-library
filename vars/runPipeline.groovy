#!/usr/bin/groovy

import static com.yoyohr.environment.PipelineEnv.PipelineGroupShellSpec
import static com.yoyohr.environment.PipelineEnv.PipelineGroupPhpSpec
import static com.yoyohr.environment.PipelineEnv.PipelineGroupJavaSpec
import static com.yoyohr.environment.PipelineEnv.PipelineGroupGoSpec
import com.yoyohr.shellSpecPipeline
import com.yoyohr.javaSpecPipeline
import com.yoyohr.phpSpecPipeline
import com.yoyohr.goSpecPipeline
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
    // Git helm-charts 仓库地址，专门存放管理helm-charts的仓库地址
    def gitHelmChartsUrl = "http://code-base.yoyohr.com/kubernetes/helm-charts.git"
    // =========================================================================
    factory = [
            "${PipelineGroupShellSpec}": new shellSpecPipeline(),
            "${PipelineGroupPhpSpec}"  : new phpSpecPipeline(),
            "${PipelineGroupJavaSpec}" : new javaSpecPipeline(),
            "${PipelineGroupGoSpec}": new goSpecPipeline()
    ]


    stage('Load Env') {
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
                "MY_GIT_HELM_CHARTS_URL=${gitHelmChartsUrl}",
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
                doRunPipeline(yamlConf)
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

/**
 * 按照顺序执行Pipeline
 * @param yamlConf
 * @return
 */
def doRunPipeline(yamlConf) {
    def pipelineGroup = "${PipelineGroupShellSpec}"
    if (yamlConf != null) {
        pipelineGroup = dataGet(yamlConf, "pipeline")
    }
    echo "Using build: ${pipelineGroup}"
    def pipeline = null
    if (factory.containsKey("${pipelineGroup}")) {
        pipeline = factory.get("${pipelineGroup}")
    } else {
        pipeline = new unknowPipeline()
    }

    stage('Build Project') {
        pipeline.build(yamlConf)
    }

    stage('Unit Testing') {
        pipeline.unitTesting(yamlConf)
    }

    stage('Build And Push Image') {
        pipeline.docker(yamlConf)
    }

    stage('Helm') {
        pipeline.helm(yamlConf)
    }

    stage('Deploy To Kubernetes') {
        pipeline.deploy(yamlConf)
    }

    stage('Testing') {
        pipeline.testing(yamlConf)
    }
}

return this
