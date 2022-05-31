/**
 * <p>runHookBefore</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(yamlConf, hook, envCommandExec) {
    def hook = dataGet(yamlConf, "pipelineHook.${hook}")
    if (stringIsNotEmpty(hook)) {
        withEnv([
                "MY_COMMAND_EXEC=${envCommandExec}"
        ]) {
            sh """
bash -ex;
${hook}
"""
        }
    }
}
