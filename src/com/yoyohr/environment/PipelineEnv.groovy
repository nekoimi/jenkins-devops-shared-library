package com.yoyohr.environment

/**
 * <p>PipelineEnv</p>
 *
 * @author nekoimi 2022/05/29
 */
final class PipelineEnv {
    /**
     * 构建环境
     */
    public static final def BuildTest = "test"
    public static final def BuildRelease = "release"

    /**
     * 构建分组
     */
    public static final def PipelineGroupShellSpec = "shell-spec"
    public static final def PipelineGroupPhpSpec = "php-spec"
    public static final def PipelineGroupJavaSpec = "java-spec"
    public static final def PipelineGroupGoSpec = "go-spec"
}
