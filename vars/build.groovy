#!/usr/bin/groovy
/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call() {
    println("$params.tag")

    stage('Checkout') {
        println("checkout")
        println("$params.tag")
    }

    stage('Build') {
        println("build")
    }

    stage('Deploy') {
        println("deploy")
    }
}
