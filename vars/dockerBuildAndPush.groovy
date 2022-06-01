/**
 * <p>dockerBuildAndPush</p>
 *
 * @author nekoimi 2022/05/30
 */

def call(yamlConf) {
    def dockerfileExists = fileExists "Dockerfile"
    if (dockerfileExists) {
        docker.withRegistry("${MY_DOCKER_REGISTRY}", "${MY_DOCKER_REGISTRY_ID}") {
            docker.build("${MY_PROJECT_GROUP}/${MY_PROJECT_NAME}").push("${MY_PROJECT_VERSION}.${MY_BUILD_ID}-${MY_BUILD_ENV}")
        }
    }
}
