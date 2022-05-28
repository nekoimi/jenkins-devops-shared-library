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

    stage('LoadEnv') {
        println("loadenv")
        println(gitTag)
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
