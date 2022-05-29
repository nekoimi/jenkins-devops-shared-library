package com.yoyohr.contract

/**
 * <p>PipelineHook</p>
 *
 * @author nekoimi 2022/05/29
 */
interface Hook {
    void buildBefore()
    void buildAfter()
    void dockerBefore()
    void dockerAfter()
    void deployBefore()
    void deployAfter()
}