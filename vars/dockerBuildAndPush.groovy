/**
 * <p>dockerBuildAndPush</p>
 *
 * @author nekoimi 2022/05/30
 */

def call(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

    docker.withRegistry("${MY_DOCKER_REGISTRY}", "${MY_DOCKER_REGISTRY_ID}") {
        docker.build("${MY_PROJECT_GROUP}/${MY_PROJECT_NAME}").push("${MY_PROJECT_VERSION}.${MY_BUILD_ID}-${MY_BUILD_ENV}")
    }

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}
