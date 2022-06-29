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

    def isBuildImage = false
    def pipeline = null
    switch (pgroup) {
        case SvcSpec:
            pipeline = new svcSpecPipeline()
            break
        case ShellSpec:
            pipeline = new shellSpecPipeline()
            break
        case WebSpec:
            pipeline = new webSpecPipeline()
            break
        case PhpSpec:
            pipeline = new phpSpecPipeline()
            break
        case JavaSpec:
            pipeline = new javaSpecPipeline()
            break
        case GoSpec:
            pipeline = new goSpecPipeline()
            break
        case BcsSpec:
            pipeline = new bscSpecPipeline()
            break
        case BuildImage:
            isBuildImage = true
        default:
            pipeline = new shellSpecPipeline()
            break
    }
    ///////////////////////////////////////////////////////////////////////////////////
    // Do steps
    ///////////////////////////////////////////////////////////////////////////////////

    if (isBuildImage) {
        stage('Build And Push Image') {
            runHook(yamlConf, "dockerBefore", "")

            dockerBuildAndPush(yamlConf)

            runHook(yamlConf, "dockerAfter", "")
        }
    } else {

        stage('Project Build') {
            pipeline.build(yamlConf)
        }

        stage('Unit Testing') {
            pipeline.unitTesting(yamlConf)
        }

        stage('Build And Push Image') {
            runHook(yamlConf, "dockerBefore", "")

            dockerBuildAndPush(yamlConf)

            runHook(yamlConf, "dockerAfter", "")
        }

        stage('Helm Chart') {
            if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
                switch (pgroup) {
                    case WebSpec:
                        helmChart(yamlConf, "template-web-spec")
                        break
                    case SvcSpec:
                    case PhpSpec:
                    case JavaSpec:
                    case GoSpec:
                        helmChart(yamlConf, "template-svc-spec")
                        break
                    case BcsSpec:
                        helmChart(yamlConf, "template-bcs-spec")
                        break
                    default:
                        helmChart(yamlConf, "")
                        break
                }
            }
        }

        stage('Deploy To Kubernetes') {
            runHook(yamlConf, "deployBefore", "")

            if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
                deployTestToKubernetes()
            }

            if ("${MY_BUILD_ENV}" == PipelineEnv.BuildRelease) {
                pipeline.deployRelease(yamlConf)
            }

            runHook(yamlConf, "deployAfter", "")
        }

        stage('Helm Testing') {
            if ("${MY_BUILD_ENV}" == PipelineEnv.BuildTest) {
                helmTesting(yamlConf)
            }
        }
    }

}
