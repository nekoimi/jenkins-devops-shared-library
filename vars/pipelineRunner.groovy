import com.yoyohr.*
import com.yoyohr.environment.PipelineEnv

import static com.yoyohr.environment.PipelineEnv.*
/**
 * <p>pipelineRunner</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(yamlConf) {
    def pgroup = dataGet(yamlConf, "pipeline")

    echo "Using build: ${pgroup}"

    def pipeline = null
    if (pgroup == null) {
        pipeline = new shellSpecPipeline()
    }

    if (pgroup == ShellSpec) {
        pipeline = new shellSpecPipeline()
    }

    if (pgroup == WebSpec) {
        pipeline = new webSpecPipeline()
    }

    if (pgroup == PhpSpec) {
        pipeline = new phpSpecPipeline()
    }

    if (pgroup == JavaSpec) {
        pipeline = new javaSpecPipeline()
    }

    if (pgroup == GoSpec) {
        pipeline = new goSpecPipeline()
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Do steps
    ///////////////////////////////////////////////////////////////////////////////////

    stage('Project Build') {
        pipeline.build(yamlConf)
    }

    stage('Unit Testing') {
        pipeline.unitTesting(yamlConf)
    }

    stage('Build And Push Image') {
        def hookBefore = dataGet(yamlConf, "pipelineHook.dockerBefore")
        def hookAfter = dataGet(yamlConf, "pipelineHook.dockerAfter")
        if (hookBefore != null) {
            sh """
bash -ex;
${hookBefore}
"""
        }

        dockerBuildAndPush(yamlConf)

        if (hookAfter != null) {
            sh """
bash -ex;
${hookAfter}
"""
        }
    }

    stage('Deploy To Kubernetes') {
        def hookBefore = dataGet(yamlConf, "pipelineHook.deployBefore")
        def hookAfter = dataGet(yamlConf, "pipelineHook.deployAfter")
        if (hookBefore != null) {
            sh """
bash -ex;
${hookBefore}
"""
        }

        if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
            deployTestToKubernetes()
        }

        if ("${MY_BUILD_ENV}" == PipelineEnv.BuildRelease) {
            pipeline.deployRelease(yamlConf)
        }

        if (hookAfter != null) {
            sh """
bash -ex;
${hookAfter}
"""
        }
    }

    stage('Testing') {
        if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
            deployTesting()
        }
    }
}
