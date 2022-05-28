#!/usr/bin/groovy
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(gitUrl) {
    def util = new utils()
    def gitTag = "${params.tag}"

    // 加载配置参数
    stage('LoadEnv') {
        println("""
Current Repository: ${gitUrl}"
Current Git Tag: ${gitTag}
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
