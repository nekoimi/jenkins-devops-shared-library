package com.nekoimi.environment

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
    public static final def BcsSpec = "bcs-spec"
    public static final def BuildImage = "build-image"
    public static final def ShellSpec = "shell-spec"
    public static final def SvcSpec = "svc-spec"
    public static final def WebSpec = "web-spec"

    public static final def PhpSpec = "php-spec"
    public static final def JavaSpec = "java-spec"
    public static final def GoSpec = "go-spec"
}
