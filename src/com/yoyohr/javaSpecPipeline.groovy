package com.yoyohr
/**
 * <p>java项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def doBuild(command) {
    sh """
bash -ex;

\${MY_COMMAND_EXEC} ${command}

ls -l target

jarName=\$(ls target | grep .jar\$ | sed s/[[:space:]]//g)

if [ ! -z \$jarName ]; then
    if [ -f target/\$jarName ]; then
        mv target/\$jarName target/app.jar
    fi
fi

warName=\$(ls target | grep .war\$ | sed s/[[:space:]]//g)

if [ ! -z \$warName ]; then
    if [ -f target/\$warName ]; then
        mv target/\$warName target/app.war
    fi
fi

ls -l target
"""
}

/**
 * <p>复制结果到指定目录</p>
 * @param nameOverride
 * @param path
 */
def doBuildResultCopyToPath(nameOverride, path) {
    def jarExists = fileExists "target/app.jar"
    if (jarExists) {
        sh """
bash -ex;

if [ -d "${path}" ]; then
    echo 'Copy app.jar To ${path}/${nameOverride}.jar......'
    cp -rf target/app.jar "${path}/${nameOverride}.jar"
else
    echo 'Warning! Path ${path} does exists!'
    exit 1
fi

"""
    }

    def warExists = fileExists "target/app.war"
    if (warExists) {
        sh """
bash -ex;

if [ -d "${path}" ]; then
    echo 'Copy app.war To ${path}/${nameOverride}.war......'
    cp -rf target/app.war "${path}/${nameOverride}.war"
else
    echo 'Warning! Path ${path} does exists!'
    exit 1
fi

"""
    }
}

/**
 * <p>复制结果到指定git仓库</p>
 * @param nameOverride
 * @param gitUrl
 * @param branch
 */
def doBuildResultCopyToGit(nameOverride, gitUrl, branch) {
    withCredentials([gitUsernamePassword(credentialsId: "${MY_GIT_ID}")]) {
        def warExists = fileExists "target/app.war"
        if (warExists) {
            sh """
bash -ex;
nowTime=\$(date "+%Y-%m-%d %H:%M:%S")

jenkinsWorkspace=\$(dirname \$PWD)

repoMd5=\$(echo "${gitUrl}-${branch}" | md5sum | awk '{print \$1}' | sed s/[[:space:]]//g)
if [ ! -e "\$jenkinsWorkspace/devopsCopyTo" ]; then
    mkdir -p \$jenkinsWorkspace/devopsCopyTo
fi

cd \$jenkinsWorkspace/devopsCopyTo

if [ ! -e "\$jenkinsWorkspace/devopsCopyTo/\$repoMd5" ]; then
    echo 'Clone远程仓库......'
    git clone -b ${branch} ${gitUrl} \$repoMd5 && ls -l \$repoMd5
else
    echo 'Pull远程仓库......'
    git pull origin ${branch} && ls -l \$repoMd5
fi

cp -rf \${MY_WORKSPACE}/target/app.war \$jenkinsWorkspace/devopsCopyTo/\$repoMd5/${nameOverride}.war

cd \$repoMd5

git add .

git diff --quiet && git diff --staged --quiet || git commit -m "Updated Jenkins devops at \$nowTime"

echo 'Push远程仓库......'
git push origin ${branch}

"""
        }
    }
}

def build(yamlConf) {
    def buildImage = dataGet(yamlConf, "buildImage")
    if (stringIsNotEmpty(buildImage)) {
        def buildCommand = dataGet(yamlConf, "buildCommand")
        // commandExec
        def commandExec = "docker run --rm -w /workspace -v /root/.m2:/root/.m2 -v ${MY_PWD}:/workspace ${buildImage}"

        runHook(yamlConf, "buildBefore", commandExec)

        if (stringIsNotEmpty(buildCommand)) {
            withEnv([
                    "MY_COMMAND_EXEC=${commandExec}"
            ]) {
                doBuild(buildCommand)
            }
        }

        runHook(yamlConf, "buildAfter", commandExec)
    } else {
        runHook(yamlConf, "buildBefore", "")
        runHook(yamlConf, "buildAfter", "")
    }

    def nameOverride = dataGet(yamlConf, "${MY_BUILD_ENV}Copy.nameOverride")
    if (stringIsEmpty(nameOverride)) {
        nameOverride = "${MY_PROJECT_NAME}"
    }
    def copyPath = dataGet(yamlConf, "${MY_BUILD_ENV}Copy.path")
    if (stringIsNotEmpty(copyPath)) {
        if (copyPath.toString().endsWith("/")) {
            copyPath = copyPath.toString().substring(0, copyPath.toString().length() - 1)
        }
        doBuildResultCopyToPath(nameOverride, copyPath)
    }

    def copyGit = dataGet(yamlConf, "${MY_BUILD_ENV}Copy.git")
    def copyBranch = dataGet(yamlConf, "${MY_BUILD_ENV}Copy.branch")
    if (stringIsNotEmpty(copyGit)) {
        if (stringIsEmpty(copyBranch)) {
            copyBranch = "master"
        }
        doBuildResultCopyToGit(nameOverride, copyGit, copyBranch)
    }
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
