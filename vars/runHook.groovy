import com.yoyohr.utils.StringUtils
import com.yoyohr.utils.YamlUtils

/**
 * <p>runHookBefore</p>
 *
 * @author nekoimi 2022/05/31
 */

def call(yamlConf, hook, commandExec) {
    def hookCommand = YamlUtils.get(yamlConf, "pipelineHook.${hook}")
    if (StringUtils.isNotEmpty(hookCommand)) {
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
