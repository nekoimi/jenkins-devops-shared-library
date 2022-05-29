package com.yoyohr


import com.yoyohr.pipeline.ShellHookPipeline
/**
 * <p>PipelineRegistry</p>
 *
 * @author nekoimi 2022/05/29
 */
class PipelineFactory {
    private final static def registry = [:]

    Pipeline of(project, buildEnv) {
        return new ShellHookPipeline()
    }

    Pipeline ofShellPipeline(buildEnv) {
        // PipelineEnv.GroupShell, buildEnv
        return new ShellHookPipeline()
    }
}
