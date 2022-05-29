package com.yoyohr.contract

/**
 * <p>Pipeline</p>
 *
 * @author nekoimi 2022/05/29
 */
interface Pipeline {
    void build()
    void dockerImage()
    void dockerPush()
    void deploy()
}