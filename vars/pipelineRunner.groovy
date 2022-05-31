import com.yoyohr.goSpecPipeline
import com.yoyohr.javaSpecPipeline
import com.yoyohr.phpSpecPipeline
import com.yoyohr.shellSpecPipeline
import com.yoyohr.webSpecPipeline
import com.yoyohr.unknowPipeline

import static com.yoyohr.environment.PipelineEnv.GoSpec
import static com.yoyohr.environment.PipelineEnv.JavaSpec
import static com.yoyohr.environment.PipelineEnv.PhpSpec
import static com.yoyohr.environment.PipelineEnv.ShellSpec
import static com.yoyohr.environment.PipelineEnv.WebSpec

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
        def hookBefore = dataGet(yamlConf, "pipelineHook.buildBefore")
        def hookAfter = dataGet(yamlConf, "pipelineHook.buildAfter")

        if (hookBefore != null) {
            sh """
bash -ex;
${hookBefore}
"""
        }

        pipeline.build(yamlConf)

        if (hookAfter != null) {
            sh """
bash -ex;
${hookAfter}
"""
        }
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

        pipeline.docker(yamlConf)

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

        pipeline.deploy(yamlConf)

        if (hookAfter != null) {
            sh """
bash -ex;
${hookAfter}
"""
        }
    }

    stage('Testing') {
        pipeline.testing(yamlConf)
    }
}
