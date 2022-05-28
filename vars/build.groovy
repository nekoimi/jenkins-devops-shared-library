#!/usr/bin/groovy
/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call() {
    def gitTag = ${params.tag}

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
