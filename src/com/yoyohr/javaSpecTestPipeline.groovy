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

docker run --rm -w /work -v \$PWD:/work maven:3.6-openjdk-11 mvn clean package -Dfile.encoding=UTF-8 -DskipTests=true
"""

    if (hook_after != null) {
        sh """
bash -ex;
${hook_after}
"""
    }
}

def dockerImage(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_image_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_image_after")

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

def dockerPush(yamlConf) {
    def hook_before = dataGet(yamlConf, "pipeline_hook.docker_push_before")
    def hook_after = dataGet(yamlConf, "pipeline_hook.docker_push_after")

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
