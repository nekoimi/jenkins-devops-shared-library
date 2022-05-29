#!/usr/bin/groovy
import static com.yoyohr.environment.PipelineEnv.BuildTest
import static com.yoyohr.environment.PipelineEnv.BuildRelease
import static com.yoyohr.environment.PipelineEnv.GroupShell
import static com.yoyohr.environment.PipelineEnv.GroupPhp
import static com.yoyohr.environment.PipelineEnv.GroupJava
import com.yoyohr.shellSpecTestPipeline
import com.yoyohr.unknowPipeline

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call(url = "", barch = "") {
    // jenkins上devops的git账号凭据ID
    def gitDevOpsId = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    def workspace = "$env.workspace"
    def jobName = "${env.JOB_NAME}"
    def buildId = "${env.BUILD_ID}"
    def projectYaml = "project.yaml"
    def buildEnv = "$params.BUILD_ENV"
    factory = [
            "${GroupShell}-${BuildTest}"   : new shellSpecTestPipeline(),
            "${GroupShell}-${BuildRelease}": new shellSpecTestPipeline(),

            "${GroupPhp}-${BuildTest}"     : new shellSpecTestPipeline(),
            "${GroupPhp}-${BuildRelease}"  : new shellSpecTestPipeline(),

            "${GroupJava}-${BuildTest}"    : new shellSpecTestPipeline(),
            "${GroupJava}-${BuildRelease}" : new shellSpecTestPipeline()
    ]

    stage('LoadEnv') {
        def workspaceExists = fileExists workspace
        if (!workspaceExists) {
            checkout scm
        }

        def pipelineInformation = "Pipeline:\n"
        factory.each { k, v ->
            pipelineInformation = pipelineInformation.concat("${k} -> ${v}\n")
        }

        def yamlConf = null
        def exists = fileExists projectYaml
        if (exists) {
            pipelineInformation = pipelineInformation.concat("\nYamlConf: \n")
            yamlConf = readYaml file: "project.yaml"
            yamlConf.each { k, v ->
                pipelineInformation = pipelineInformation.concat("${k} -> ${v}\n")
            }
        }
        notice('Pipeline Information', pipelineInformation)
        // ls
        sh "ls -l"

        try {
            doRunPipeline(yamlConf, buildEnv)
        } finally {
            cleanWs()
        }
    }
}

/**
 * 按照顺序执行Pipeline
 * @param yamlConf
 * @param buildEnv
 * @return
 */
def doRunPipeline(yamlConf, buildEnv) {
    def group = "${GroupShell}"
    if (yamlConf != null) {
        group = dataGet(yamlConf, "pipeline")
    }
    echo "${group}-${buildEnv}"
    def pipeline = null
    if (factory.containsKey("${group}-${buildEnv}")) {
        pipeline = factory.get("${group}-${buildEnv}")
    } else {
        pipeline = new unknowPipeline()
    }

    stage('Build') {
        pipeline.build(yamlConf)
    }

    stage('Docker Image') {
        pipeline.dockerImage(yamlConf)
    }

    stage('Docker Push') {
        pipeline.dockerPush(yamlConf)
    }

    stage('Deploy') {
        pipeline.deploy(yamlConf)
    }
}

return this
