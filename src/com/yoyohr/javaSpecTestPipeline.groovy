package com.yoyohr
/**
 * <p>java项目测试环境标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.build_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.build_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

    sh """
bash -ex;

sed -i 's/<packaging>war/<packaging>jar/g' pom.xml

docker run --rm -w /work -v /root/.m2:/root/.m2 -v \${MY_PWD}:/work maven:3.6-openjdk-11 mvn clean package -Dfile.encoding=UTF-8 -DskipTests=true

ls -l target

jarName=\$(ls target | grep .jar\\\$)

mv target/\${jarName} target/app.jar

ls -l target
"""

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def docker(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

//    docker.withRegistry("${MY_DOCKER_REGISTRY}", "${MY_DOCKER_REGISTRY_ID}") {
//        docker.build("${MY_PROJECT_GROUP}/${MY_PROJECT_NAME}").push("${MY_PROJECT_VERSION}-${MY_BUILD_ENV}")
//    }

    sh """
bash -ex;

docker login -u \${MY_DOCKER_REGISTRY_USER} -p \${MY_DOCKER_REGISTRY_PASSWORD} \${MY_DOCKER_REGISTRY}

docker build -t \${MY_DOCKER_REGISTRY_IMAGE} .

docker push \${MY_DOCKER_REGISTRY_IMAGE}
"""

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def deploy(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.deploy_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.deploy_after")

    if (hook_before != null) {
        sh """
bash -ex;
${hook_before}
"""
    }

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}
