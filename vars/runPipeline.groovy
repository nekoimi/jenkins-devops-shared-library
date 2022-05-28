#!/usr/bin/groovy
import com.yoyohr.utils
import com.yoyohr.deployEnv

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(gitUrl = "", gitBranch = "") {
    // jenkins上devops的git账号凭据ID
    def gitDevOpsId = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    def workspace = "$env.workspace/"
    def jobName = "${env.JOB_NAME}"
    def buildId = "${env.BUILD_ID}"
    def defaultDeployScript = workspace + "deploy.sh"
    def projectYaml = workspace + "project.yaml"
    def util = new utils()
    def buildEnv = "$params.BUILD_ENV"

    stage('Checkout') {
        if (gitUrl == "" && gitBranch == "") {
            checkout scm
        } else {
            git changelog: true,
                    branch: gitBranch,
                    credentialsId: gitDevOpsId,
                    url: gitUrl
        }
    }

    stage('LoadEnv') {
        util.lsFile()
        if (util.fileExists(projectYaml)) {
            project = readYaml file: "project.yaml"
            loadProjectYaml = true
        } else {
            project = "load fail!"
            loadProjectYaml = false
        }
        println("""
Workspace: ${workspace}
Project.yaml: ${projectYaml}
Load Project Config: ${loadProjectYaml}
Project Config: ${project}
""")
    }

    if (!loadProjectYaml) {
        stage('Build') {
            // 走 deploy.sh
            if (util.fileExists(defaultDeployScript)) {
                sh "bash -ex deploy.sh"
            } else {
                echo "Default deploy.sh file does not exist!"
            }
        }
    }

    // 读取项目目录下 project.yaml
    // 解析 yaml 配置
    // 决定后续CI/CD流程
    else {
        stage('Build') {
            println(project)
            println("build")

            sh "bash -ex test.sh"
        }

        stage('Deploy') {
            println("deploy")
        }
    }
}
