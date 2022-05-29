#!/usr/bin/groovy
import com.yoyohr.PipelineRunner

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(url = "", barch = "") {
    // jenkins上devops的git账号凭据ID
    def gitDevOpsId = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    def workspace = "$env.workspace/"
    def jobName = "${env.JOB_NAME}"
    def buildId = "${env.BUILD_ID}"
    def projectYaml = "project.yaml"
    def buildEnv = "$params.BUILD_ENV"
    def runner = new PipelineRunner()

    stage('LoadEnv') {
        def exists = fileExists projectYaml
        if (exists) {
            def project = readYaml file: "project.yaml"
            println(project.getClass())
            runner.run("java-spec", buildEnv)
        } else {
            runner.runShellPipeline(buildEnv)
        }
    }
}
//
//def runYaml() {
//    project = readYaml file: "project.yaml"
//    println(project.getClass())
//    def pipeline = registry.of("")
//    echo """
//Workspace: ${workspace}
//Project.yaml: ${projectYaml}
//Load Project Config: ${loadProjectYaml}
//Project Config: ${project}
//"""
//
//    stage('Build') {
//        println(project)
//        println("build")
//
//        sh "bash -ex test.sh"
//    }
//
//    stage('Docker Image') {
//        println(project)
//        println("build")
//
//        sh "bash -ex test.sh"
//    }
//
//    stage('Deploy') {
//        println("deploy")
//    }
//}

return this
