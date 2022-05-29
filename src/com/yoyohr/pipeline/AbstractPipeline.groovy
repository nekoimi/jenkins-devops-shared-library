package com.yoyohr.pipeline

import com.yoyohr.Pipeline
import com.yoyohr.Hook

/**
 * <p>对Pipeline的抽象类</p>
 *
 * @author nekoimi 2022/05/29
 */
abstract class AbstractPipeline implements Pipeline {
    protected def project;
    protected Hook hook;

    @java.lang.Override
    void build() {

    }

    @java.lang.Override
    void dockerImage() {

    }

    @java.lang.Override
    void dockerPush() {

    }

    @java.lang.Override
    void deploy() {

    }
}
