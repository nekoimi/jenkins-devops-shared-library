package com.yoyohr

/**
 * <p>PipelineHook</p>
 *
 * @author nekoimi 2022/05/29
 */
interface PipelineHook {
    void buildBefore()
    void buildAfter()
    void deployBefore()
    void deployAfter()
}