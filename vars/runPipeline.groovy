#!/usr/bin/groovy
import com.yoyohr.PipelineFactory
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
    def factory = new PipelineFactory()

    stage('LoadEnv') {
        def exists = fileExists projectYaml
        def pipeline = null
        if (exists) {
            def project = readYaml file: "project.yaml"
            println(project.getClass())
            pipeline = factory.of(project, buildEnv)
        } else {
            pipeline = factory.ofShellPipeline(buildEnv)
        }
        // Run
        doRunPipeline(pipeline)
    }
}

/**
 * 按照顺序执行Pipeline
 * @param pipeline
 * @return
 */
def doRunPipeline(pipeline) {
    stage('Build') {
        pipeline.build()
    }

    stage('Docker Image') {
        pipeline.dockerImage()
    }

    stage('Docker Push') {
        pipeline.dockerPush()
    }

    stage('Deploy') {
        pipeline.deploy()
    }
}

return this
