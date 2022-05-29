#!/usr/bin/groovy

import com.yoyohr.environment.PipelineEnv
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

    stage('LoadEnv') {
        def yamlData = null
        def exists = fileExists projectYaml
        if (exists) {
            yamlData = readYaml file: "project.yaml"
            yamlData.each{ k, v ->
                echo "yamlConf: ${k} -> ${v}"
            }
        }
        doRunPipeline(yamlData, buildEnv)
    }
}

/**
 * 按照顺序执行Pipeline
 * @param yamlConf
 * @param buildEnv
 * @return
 */
def doRunPipeline(yamlConf, buildEnv) {
    def pipelineGroup = "${PipelineEnv.GroupShell}"
    if (yamlConf != null) {
        pipelineGroup = yamlConf.get("group")
    }

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
