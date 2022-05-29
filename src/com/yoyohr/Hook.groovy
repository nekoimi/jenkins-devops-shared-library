package com.yoyohr

/**
 * <p>PipelineHook</p>
 *
 * @author nekoimi 2022/05/29
 */
interface Hook {
    void buildBefore()
    void buildAfter()
    void deployBefore()
    void deployAfter()
}