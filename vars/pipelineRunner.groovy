import com.yoyohr.goSpecPipeline
import com.yoyohr.javaSpecPipeline
import com.yoyohr.phpSpecPipeline
import com.yoyohr.shellSpecPipeline
import com.yoyohr.unknowPipeline

import static com.yoyohr.environment.PipelineEnv.PipelineGroupGoSpec
import static com.yoyohr.environment.PipelineEnv.PipelineGroupJavaSpec
import static com.yoyohr.environment.PipelineEnv.PipelineGroupPhpSpec
import static com.yoyohr.environment.PipelineEnv.PipelineGroupShellSpec

/**
 * <p>pipelineRunner</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(yamlConf) {
    factory = [
            "${PipelineGroupShellSpec}": new shellSpecPipeline(),
            "${PipelineGroupPhpSpec}"  : new phpSpecPipeline(),
            "${PipelineGroupJavaSpec}" : new javaSpecPipeline(),
            "${PipelineGroupGoSpec}"   : new goSpecPipeline()
    ]
    def pipelineGroup = null
    if (yamlConf != null) {
        pipelineGroup = dataGet(yamlConf, "pipeline")
    }

    def pipeline = new unknowPipeline()
    if (pipelineGroup != null && factory.containsKey(pipelineGroup)) {
        echo "Using build: ${pipelineGroup}"

        pipeline = factory.get(pipelineGroup)
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
