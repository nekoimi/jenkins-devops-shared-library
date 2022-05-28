#!/usr/bin/groovy
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(gitUrl) {
    def gitTag = "${params.tag}"
    def workspace = "$env.workspace"
    // def project = readYaml file: "project.yaml"
    def util = new utils()

    // 加载配置参数
    stage('LoadEnv') {
        println("""
Workspace: ${workspace}
Repository: ${gitUrl}"
Git Tag: ${gitTag}
""")
    }

    stage('Checkout') {
        println("checkout")
        util.lsFile()
    }

    stage('Build') {
        println("build")
    }

    stage('Deploy') {
        println("deploy")
    }
}
