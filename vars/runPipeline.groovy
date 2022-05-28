#!/usr/bin/groovy
import com.yoyohr.gitplus
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(gitUrl = "", gitBranch = "") {
    def workspace = "$env.workspace/"
    def defaultDeployScript = workspace + "deploy.sh"
    def projectYaml = workspace + "project.yaml"
    def util = new utils()
    def gitPlus = new gitplus()

    stage('Checkout') {
        if (gitUrl == "" && gitBranch == "") {
            checkout scm
        } else {
            gitPlus.pullBranch(gitUrl, gitBranch)
        }
    }

    stage('LoadEnv') {
        util.lsFile()
        if (util.fileExists(projectYaml)) {
            project = readYaml file: projectYaml
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
                sh "bash -ex deploy.sh test ${env.CODEBASE} ${env.JOB_NAME}"
            } else {
                echo "Default deploy.sh file does not exist!"
            }
        }
    }


    else {
        stage('Build') {
            println(project)
            println("build")
        }

        stage('Deploy') {
            println("deploy")
        }
    }
}
