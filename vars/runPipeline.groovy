#!/usr/bin/groovy
import static com.yoyohr.environment.PipelineEnv.BuildTest
import static com.yoyohr.environment.PipelineEnv.BuildRelease
import static com.yoyohr.environment.PipelineEnv.GroupShell
import static com.yoyohr.environment.PipelineEnv.GroupPhp
import static com.yoyohr.environment.PipelineEnv.GroupJava
import static com.yoyohr.environment.PipelineEnv.GroupGo
import com.yoyohr.shellSpecPipeline
import com.yoyohr.javaSpecTestPipeline
import com.yoyohr.javaSpecReleasePipeline
import com.yoyohr.phpSpecTestPipeline
import com.yoyohr.phpSpecReleasePipeline
import com.yoyohr.goSpecTestPipeline
import com.yoyohr.goSpecReleasePipeline
import com.yoyohr.unknowPipeline

/**
 * <p>build</p>
 *
 * @author nekoimi 2022/05/28
 */

def call() {
    // jenkins上devops的git账号凭据ID
    def gitDevOpsId = "5a8151d1-6d6b-4160-8f32-122a9e9a74ba"
    def workspace = "$env.workspace"
    def jobName = "${env.JOB_NAME}"
    def buildId = "${env.BUILD_ID}"
    def myPwd = "/home/nfs/jenkins/data/jenkins_home/workspace/${jobName}"
    def projectYaml = "project.yaml"
    def buildEnv = "$params.BUILD_ENV"
    factory = [
            "${GroupShell}-${BuildTest}"   : new shellSpecPipeline(),
            "${GroupShell}-${BuildRelease}": new shellSpecPipeline(),

            "${GroupPhp}-${BuildTest}"     : new phpSpecTestPipeline(),
            "${GroupPhp}-${BuildRelease}"  : new phpSpecReleasePipeline(),

            "${GroupJava}-${BuildTest}"    : new javaSpecTestPipeline(),
            "${GroupJava}-${BuildRelease}" : new javaSpecReleasePipeline(),

            "${GroupGo}-${BuildTest}"    : new goSpecTestPipeline(),
            "${GroupGo}-${BuildRelease}" : new goSpecReleasePipeline()
    ]

    withEnv([
            "MY_PWD=${myPwd}",
    ]) {
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
            // ls
            sh "ls -l"

            // printenv
            sh "printenv"

            notice('Pipeline Information', pipelineInformation)

            try {
                doRunPipeline(yamlConf, buildEnv)
            } finally {
                cleanWs()
            }
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
