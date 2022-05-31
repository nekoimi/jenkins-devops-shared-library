/**
 * <p>runHookBefore</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(yamlConf, hook, commandExec) {
    def hookCommand = dataGet(yamlConf, "pipelineHook.${hook}")
    if (stringIsNotEmpty(hookCommand)) {
        withEnv([
                "MY_COMMAND_EXEC=${commandExec}"
        ]) {
            sh """
bash -ex;
${hookCommand}
"""
        }
    }
}
