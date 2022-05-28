#!/usr/bin/groovy
import com.yoyohr.utils

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(GString gitUrl) {
    def util = new utils()
    def gitTag = "${params.tag}"

    stage('LoadEnv') {
        util.lsFile()
        println("loadenv")
        println(gitTag)
    }

    stage('Checkout') {
        println("checkout")
        println(gitTag)
    }

    stage('Build') {
        println("build")
    }

    stage('Deploy') {
        println("deploy")
    }
}
