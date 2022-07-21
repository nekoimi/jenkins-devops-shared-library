import com.nekoimi.utils.StringUtils
import com.nekoimi.utils.YamlUtils

/**
 * <p>dockerBuildAndPush</p>
 *
 * @author nekoimi 2022/05/30
 */

def call(yamlConf) {
    def dockerfileExists = fileExists "Dockerfile"
    if (dockerfileExists) {
        buildAndPush(yamlConf)
    }
}

def buildAndPush(yamlConf) {
    docker.withRegistry("${MY_DOCKER_REGISTRY}", "${MY_DOCKER_REGISTRY_ID}") {
        docker.build("${MY_PROJECT_GROUP}/${MY_PROJECT_NAME}").push("${MY_PROJECT_VERSION}.${MY_BUILD_ID}-${MY_BUILD_ENV}")
    }

    def pushList = YamlUtils.get(yamlConf, "build.push")
    for (push in pushList) {
        def name = YamlUtils.get(push, "name", "Push Image")
        def buildEnv = YamlUtils.get(push, "buildEnv", "test")
        if ("$buildEnv" == "${MY_BUILD_ENV}") {
            def repository = YamlUtils.get(push, "repository")
            def tag = YamlUtils.get(push, "tag", "${MY_PROJECT_VERSION}.${MY_BUILD_ID}-${MY_BUILD_ENV}")
            if (StringUtils.isNotEmpty(repository) && StringUtils.isNotEmpty(tag)) {
                notice("${name} - ${buildEnv}", "Push Image =======> ${repository}:${tag}")
                sh "docker tag ${MY_DOCKER_REGISTRY_IMAGE} ${repository}:${tag}"
                def image = docker.image("${repository}:${tag}")
                image.push()
            }
        }
    }
}
