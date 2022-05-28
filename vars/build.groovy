#!/usr/bin/groovy
import com.yoyohr.gitplus
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(gitUrl) {
    parameters {
        gitParameter name: 'tag',
                type: 'PT_TAG',
                branchFilter: 'origin/(.*)',
                description: '标签名称',
                quickFilterEnabled: false,
                selectedValue: 'TOP',
                sortMode: 'DESCENDING_SMART',
                tagFilter: '*',
                defaultValue: ''
    }

    def gitTag = "${params.tag}"
    def workspace = "$env.workspace/"
    def util = new utils()
    def gitplus = new gitplus();

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
//        gitplus.pullTag(gitUrl, gitTag)
        util.lsFile()
        def project = readYaml file: "project.yaml"
        println(project)
    }

    stage('Build') {
        println("build")
    }

    stage('Deploy') {
        println("deploy")
    }
}
