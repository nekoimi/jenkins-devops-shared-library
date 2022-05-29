#!/usr/bin/groovy
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(url = "", barch = "") {
    // jenkins上devops的git账号凭据ID
    gitDevOpsId = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    workspace = "$env.workspace/"
    jobName = "${env.JOB_NAME}"
    buildId = "${env.BUILD_ID}"
    projectYaml = "project.yaml"
    buildEnv = "$params.BUILD_ENV"
    util = new utils()

    stage('LoadEnv') {
        def exists = fileExists projectYaml
//        if (exists) {
//            runYaml()
//        } else {
            runShell()
//        }
    }
}

def runYaml() {
    project = readYaml file: "project.yaml"
    loadProjectYaml = true
    echo """
Workspace: ${workspace}
Project.yaml: ${projectYaml}
Load Project Config: ${loadProjectYaml}
Project Config: ${project}
"""

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
        noticeWarning text: """
Warning！当前项目 project.yaml 配置文件不存在，请使用 BuildHook 完成 Build 流程。
BuildHook 使用参见：http://code-base.yoyohr.com/kubernetes/no-jenkinsfile
"""
    }
}

return this
