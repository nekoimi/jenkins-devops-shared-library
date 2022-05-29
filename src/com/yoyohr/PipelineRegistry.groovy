package com.yoyohr


import com.yoyohr.pipeline.ShellHookPipeline
/**
 * <p>PipelineRegistry</p>
 *
 * @author nekoimi 2022/05/29
 */
class PipelineRegistry {
    private final static def registry = [:]

    Pipeline of(key) {
        return new ShellHookPipeline()
    }
}
