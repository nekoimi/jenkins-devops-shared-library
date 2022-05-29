#!/usr/bin/groovy
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(url = "", barch = "") {
    // jenkins上devops的git账号凭据ID
    def gitDevOpsId = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    workspace = "$env.workspace/"
    def jobName = "${env.JOB_NAME}"
    def buildId = "${env.BUILD_ID}"
    defaultDeployScript = workspace + "deploy.sh"
    projectYaml = workspace + "project.yaml"
    def buildEnv = "$params.BUILD_ENV"
    def util = new utils()

    stage('LoadEnv') {
        if (util.fileExists(projectYaml)) {
            runYaml()
        } else {
            runShell()
        }
    }
}

def runYaml() {
    project = readYaml file: "project.yaml"
    loadProjectYaml = true
    println("""
Workspace: ${workspace}
Project.yaml: ${projectYaml}
Load Project Config: ${loadProjectYaml}
Project Config: ${project}
""")

    stage('Build') {
        println(project)
        println("build")

        sh "bash -ex test.sh"
    }

    stage('Deploy') {
        println("deploy")
    }
}

def runShell() {
    stage('Build') {
        // 走 deploy.sh
        if (util.fileExists(defaultDeployScript)) {
            sh "bash -ex deploy.sh"
        } else {
            echo "Default deploy.sh file does not exist!"
        }
    }
}

return this
