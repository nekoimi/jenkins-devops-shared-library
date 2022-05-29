package com.yoyohr

import com.yoyohr.environment.PipelineEnv

/**
 * <p>PipelineRunner</p>
 *
 * @author nekoimi 2022/05/29
 */
class PipelineRunner {
    private PipelineRegistry registry;

    PipelineRunner() {
        this.registry = new PipelineRegistry()
    }

    /**
     * 运行shell构建流程
     * @param buildEnv
     */
    void runShellPipeline(buildEnv) {
        run(PipelineEnv.GroupShell, buildEnv)
    }

    /**
     * 运行 Pipeline 构建
     * @param group 构建分组
     * @param buildEnv 构建环境
     */
    void run(group, buildEnv) {
        def pipelineKey = group + buildEnv
        def pipeline = registry.of(pipelineKey)

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
}
