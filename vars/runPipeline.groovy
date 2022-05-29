#!/usr/bin/groovy
import com.github.javaparser.utils.ClassUtils
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
        def yamlConf = null
        def exists = fileExists projectYaml
        if (exists) {
            yamlConf = readYaml file: "project.yaml"
            yamlConf.each{ k, v ->
                echo "yamlConf: ${k} -> ${v}"
            }
        }
        doRunPipeline(yamlConf, buildEnv)
    }
}

/**
 * 按照顺序执行Pipeline
 * @param yamlConf
 * @param buildEnv
 * @return
 */
def doRunPipeline(yamlConf, buildEnv) {
    def group = "${PipelineEnv.GroupShell}"
    if (yamlConf != null) {
        group = yamlConf.get("group")
    }
    GroovyShell shell = new GroovyShell()
    def pipeline = shell.parse(new File("com.yoyohr.shellSpecTestPipeline.groovy"))
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
