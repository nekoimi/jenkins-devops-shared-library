/**
 * <p>buildWithCache</p>
 *
 * @author nekoimi 2022/06/04
 */
import com.yoyohr.utils.StringUtils
import com.yoyohr.utils.YamlUtils

def call(yamlConf, dockerVolumeOpts, Closure buildCallback) {
    buildV1(yamlConf, dockerVolumeOpts, buildCallback)

    // 执行构建命令
    def buildImage = YamlUtils.get(yamlConf, "build.image")
    if (StringUtils.isEmpty(buildImage)) {
        runHook(yamlConf, "buildBefore", "")
        runHook(yamlConf, "buildAfter", "")
    } else {
        // commandExec
        def commandExec = createCommandExec(dockerVolumeOpts, buildImage)

        def commands = YamlUtils.get(yamlConf, "build.commands")

        println(commands)
        println(commands.toString())
        println(commands.getClass())

        runHook(yamlConf, "buildBefore", commandExec)

        for (command in commands) {
            println("exec-command: " + command)

            if (StringUtils.isNotEmpty(command)) {
                withEnv([
                        "MY_COMMAND_EXEC=${commandExec}"
                ]) {
                    doBuild(command)
                }
            }
        }

        // 执行完成回调
        buildCallback.call()

        runHook(yamlConf, "buildAfter", commandExec)
    }
}

// Deprecated
// 兼容旧版本
def buildV1(yamlConf, dockerVolumeOpts, buildCallback) {
    def buildImage = YamlUtils.get(yamlConf, "buildImage")
    if (StringUtils.isNotEmpty(buildImage)) {
        def installCommand = YamlUtils.get(yamlConf, "installCommand")
        def buildCommand = YamlUtils.get(yamlConf, "buildCommand")
        // commandExec
        def commandExec = createCommandExec(dockerVolumeOpts, buildImage)

        runHook(yamlConf, "buildBefore", commandExec)

        if (StringUtils.isNotEmpty(installCommand)) {
            withEnv([
                    "MY_COMMAND_EXEC=${commandExec}"
            ]) {
                doBuild(installCommand)
            }
        }

        if (StringUtils.isNotEmpty(buildCommand)) {
            withEnv([
                    "MY_COMMAND_EXEC=${commandExec}"
            ]) {
                doBuild(buildCommand)
            }
        }

        // 执行完成回调
        buildCallback.call()

        runHook(yamlConf, "buildAfter", commandExec)
    } else {
        runHook(yamlConf, "buildBefore", "")
        runHook(yamlConf, "buildAfter", "")
    }
}

def doBuild(command) {
    sh """
bash -ex;

\${MY_COMMAND_EXEC} ${command}

ls -l
"""
}

def createCommandExec(dockerVolumeOpts, buildImage) {
    return "docker run --rm -w /workspace ${dockerVolumeOpts} -v ${MY_PWD}:/workspace ${buildImage}"
}
