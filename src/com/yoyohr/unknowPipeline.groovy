package com.yoyohr

/**
 * <p>UnknowPipeline</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    noticeWarning("""
Warning！构建流程不支持，请使用 Hook 完成 Pipeline 流程。
""")
}

def dockerImage(yamlConf) {
    noticeWarning("""
Warning！构建流程不支持，请使用 Hook 完成 Pipeline 流程。
""")
}

def dockerPush(yamlConf) {
    noticeWarning("""
Warning！构建流程不支持，请使用 Hook 完成 Pipeline 流程。
""")
}

def deploy(yamlConf) {
    noticeWarning("""
Warning！构建流程不支持，请使用 Hook 完成 Pipeline 流程。
""")
}
