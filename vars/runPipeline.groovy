#!/usr/bin/groovy
import static com.yoyohr.environment.PipelineEnv.BuildTest
import static com.yoyohr.environment.PipelineEnv.BuildRelease
import static com.yoyohr.environment.PipelineEnv.GroupShell
import static com.yoyohr.environment.PipelineEnv.GroupPhp
import static com.yoyohr.environment.PipelineEnv.GroupJava
import com.yoyohr.shellSpecTestPipeline

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
    factory = [
            "${GroupShell}-${BuildTest}": new shellSpecTestPipeline(),
            "${GroupShell}-${BuildRelease}": new shellSpecTestPipeline(),

            "${GroupPhp}-${BuildTest}": new shellSpecTestPipeline(),
            "${GroupPhp}-${BuildRelease}": new shellSpecTestPipeline(),

            "${GroupJava}-${BuildTest}": new shellSpecTestPipeline(),
            "${GroupJava}-${BuildRelease}": new shellSpecTestPipeline()
    ]

    stage('LoadEnv') {
        factory.each { k, v ->
            echo "factory: ${k} -> ${v}"
        }

        def yamlConf = null
        def exists = fileExists projectYaml
        if (exists) {
            yamlConf = readYaml file: "project.yaml"
            yamlConf.each { k, v ->
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
    def group = "${GroupShell}"
    if (yamlConf != null) {
        group = yamlConf.get("group")
    }
    if (!factory.containsKey("${group}-${buildEnv}")) {
        noticeWarning("""
Warning！构建流程不支持，请使用 Hook 完成 Pipeline 流程。
""")
        return
    }

    def pipeline = factory.get("${group}-${buildEnv}")

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
