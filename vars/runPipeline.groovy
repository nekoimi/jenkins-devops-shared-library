#!/usr/bin/groovy
import com.yoyohr.gitplus
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call() {
    def gitTag = "${params.tag}"
    def workspace = "$env.workspace/"
    def util = new utils()

    // 加载配置参数
    stage('LoadEnv') {
        println("""
Workspace: ${workspace}
""")
    }

    stage('Checkout') {
        checkout scm
        // Pull
//        gitplus.pullTag(gitUrl, gitTag)
        util.lsFile()
        project = readYaml file: "project.yaml"
    }

    stage('Build') {
        println(project)
        println("build")
    }

    stage('Deploy') {
        println("deploy")
    }
}
