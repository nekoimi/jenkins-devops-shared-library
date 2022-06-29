import com.yoyohr.utils.StringUtils
import com.yoyohr.utils.YamlUtils

/**
 * <p>buildResultCopy</p>
 *
 * @author nekoimi 2022/06/04
 */

def call(yamlConf, buildResult, defaultNameOverride) {
    if (buildResult.toString().startsWith("/")) {
        buildResult = buildResult.toString().substring(1, buildResult.toString().length())
    }
    if (buildResult.toString().endsWith("/")) {
        buildResult = buildResult.toString().substring(0, buildResult.toString().length() - 1)
    }

    def nameOverride = YamlUtils.get(yamlConf, "${MY_BUILD_ENV}Copy.nameOverride")

    def path = YamlUtils.get(yamlConf, "${MY_BUILD_ENV}Copy.path")
    if (StringUtils.isNotEmpty(path)) {
        if (path.toString().endsWith("/")) {
            path = path.toString().substring(0, path.toString().length() - 1)
        }
        if (StringUtils.isEmpty(nameOverride)) {
            syncInFileLock(path, {
                buildResultCopyToPath(buildResult, defaultNameOverride, path)
            })
        } else {
            syncInFileLock(path, {
                buildResultCopyToPath(buildResult, nameOverride, path)
            })
        }
    }

    def git = YamlUtils.get(yamlConf, "${MY_BUILD_ENV}Copy.git")
    def branch = YamlUtils.get(yamlConf, "${MY_BUILD_ENV}Copy.branch")
    if (StringUtils.isNotEmpty(git)) {
        if (StringUtils.isEmpty(branch)) {
            branch = "master"
        }
        if (StringUtils.isEmpty(nameOverride)) {
            syncInFileLock(git, {
                doBuildResultCopyToGit(buildResult, git, branch, defaultNameOverride)
            })
        } else {
            syncInFileLock(git, {
                doBuildResultCopyToGit(buildResult, git, branch, nameOverride)
            })
        }
    }
}

def buildResultCopyToPath(buildResult, nameOverride, path) {
    sh """
bash -ex;

if [ -d "${path}" ]; then
    if [ -e "${buildResult}" ]; then
        echo 'Copy ${buildResult} To ${path}/${nameOverride} ......'
        cp -rf ${buildResult} "${path}/${nameOverride}"
    else
        echo 'Warning! Build result ${buildResult} does exists!'
        exit 1
    fi
else
    echo 'Warning! Path ${path} does exists!'
    exit 1
fi

"""
}

/**
 * <p>复制结果到指定git仓库</p>
 * @param buildResult
 * @param gitUrl
 * @param branch
 * @param nameOverride
 * @return
 */
def doBuildResultCopyToGit(buildResult, gitUrl, branch, nameOverride) {
    withCredentials([gitUsernamePassword(credentialsId: "${MY_GIT_ID}")]) {
        def warExists = fileExists "target/app.war"
        if (warExists) {
            sh """
bash -ex;
nowTime=\$(date "+%Y-%m-%d %H:%M:%S")

if [ -e "${buildResult}" ]; then
    rootWorkspace=\$(dirname \$PWD)
    repoDirName=\$(echo "${gitUrl}-${branch}" | md5sum | awk '{print \$1}' | sed s/[[:space:]]//g)
    if [ ! -e "\$rootWorkspace/resultCopy/${MY_BUILD_ENV}CopyTo" ]; then
        mkdir -p \$rootWorkspace/resultCopy/${MY_BUILD_ENV}CopyTo
    fi

    if [ ! -e "\$rootWorkspace/resultCopy/${MY_BUILD_ENV}CopyTo/\$repoDirName" ]; then
        cd \$rootWorkspace/resultCopy/${MY_BUILD_ENV}CopyTo
        echo 'Clone远程仓库......'
        git clone -b ${branch} ${gitUrl} \$repoDirName
        cd \$repoDirName && ls -l
    else
        cd \$rootWorkspace/resultCopy/${MY_BUILD_ENV}CopyTo/\$repoDirName
        echo 'Pull远程仓库......'
        git pull origin ${branch} && ls -l
    fi

    cp -rf \${MY_WORKSPACE}/${buildResult} \$rootWorkspace/resultCopy/${MY_BUILD_ENV}CopyTo/\$repoDirName/${nameOverride}

    git add .
    
    git diff --quiet && git diff --staged --quiet || git commit -m "Updated Jenkins devops at \$nowTime"
    
    echo 'Push远程仓库......'
    git push origin ${branch}
fi
"""
        }
    }
}
