#!/usr/bin/groovy
import com.yoyohr.git
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(gitUrl) {
    def gitTag = "${params.tag}"
    def workspace = "$env.workspace/"
    // def project = readYaml file: "project.yaml"
    def util = new utils()
    def git = new git();

    // 加载配置参数
    stage('LoadEnv') {
        println("""
Workspace: ${workspace}
Repository: ${gitUrl}"
Git Tag: ${gitTag}
""")
    }

    stage('Checkout') {
        // Pull
        git.pullByTag(gitUrl, gitTag)

        util.lsFile()
    }

    stage('Build') {
        println("build")
    }

    stage('Deploy') {
        println("deploy")
    }
}
