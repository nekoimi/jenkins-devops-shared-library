import com.nekoimi.utils.YamlUtils

/**
 * <p>helmChart</p>
 *
 * @author nekoimi 2022/06/01
 */

def call(yamlConf, template) {
    syncInFileLock("${MY_GIT_HELM_CHARTS_URL}", {
        helmChart(yamlConf, template)
    })
}

def helmChart(yamlConf, template) {
    def programLanguage = YamlUtils.get(yamlConf, "programLanguage")
    def projectName = "${MY_PROJECT_NAME}"
    def projectVersion = "${MY_PROJECT_VERSION}"
    def projectDescription = "${MY_PROJECT_DESCRIPTION}"
    def k8sValueYaml = "${MY_WORKSPACE}/k8s/${MY_BUILD_ENV}-values.yaml"
    def dockerImage = "${MY_DOCKER_REGISTRY_IMAGE}"
    def dockerRepository = dockerImage.split("[:]").first()
    def dockerTag = dockerImage.split("[:]").last()
    withCredentials([gitUsernamePassword(credentialsId: "${MY_GIT_ID}")]) {
        sh """
bash -ex;

nowTime=\$(date "+%Y-%m-%d %H:%M:%S")

loopReplaceChart() {
    for path in \$(ls \$1); do
        filename="\$1/\$path"
        if [ -d \$filename ]; then
            loopReplaceChart \$filename
        elif [ -f \$filename ]; then
            sed -i "s/${template}/${projectName}/g" \$filename
        fi
    done
}

writeChartToYaml() {
        cat > \$1/Chart.yaml <<EOF

# docs https://helm.sh/zh/docs/topics/charts/
# created by Jenkins devops at \$nowTime
apiVersion: v2
name: ${projectName}
version: 0.1.0
appVersion: "${projectVersion}"
description: ${projectDescription}
type: application
keywords:
  - ${projectName}
annotations:
  name: ${projectName}
  version: ${projectVersion}
  program-language: ${programLanguage}
EOF
}

createProjectChart() {
    if [ ! -z "${template}" ]; then
        if [ ! -e "${MY_WORKSPACE}/helm-charts/${template}" ]; then
            echo 'Helm chart 模板 ${template} 未找到！'
        else
            cp -rf ${MY_WORKSPACE}/helm-charts/${template} ${MY_WORKSPACE}/helm-charts/${projectName}

            loopReplaceChart ${MY_WORKSPACE}/helm-charts/${projectName}

            writeChartToYaml ${MY_WORKSPACE}/helm-charts/${projectName}

            cd ${MY_WORKSPACE}/helm-charts
            git add . 
            git commit -m "Create ${projectName} by Jenkins ${MY_JOB_NAME}-${MY_BUILD_ENV}-${MY_BUILD_ID}" 
            git push origin master

            echo 'Helm chart created!'
        fi
    fi
}

updateChartValues() {
    mv ${k8sValueYaml} ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml
    
    image=\$(cat ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml | grep image | sed s/[[:space:]]//g)
    if [ -z "\$image" ]; then
        cat >> ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml <<EOF

image:
    repository: ${dockerRepository}
    tag: "${dockerTag}"

# updated by Jenkins devops at \$nowTime

EOF
    else
        cat >> ${MY_WORKSPACE}/helm-charts/${projectName}/values.yaml <<EOF

# updated by Jenkins devops at \$nowTime

EOF
    fi

    cd ${MY_WORKSPACE}/helm-charts 
    git add . 
    git commit -m "Update by Jenkins ${MY_JOB_NAME}-${MY_BUILD_ENV}-${MY_BUILD_ID}" 
    git push origin master
    
    echo 'Helm chart updated!'
}

if [ -f "${k8sValueYaml}" ]; then
    git clone ${MY_GIT_HELM_CHARTS_URL} helm-charts

    ls -l helm-charts
    
    if [ ! -e "${MY_WORKSPACE}/helm-charts/${projectName}" ]; then
        createProjectChart
    fi
    
    if [ -e "${MY_WORKSPACE}/helm-charts/${projectName}" ]; then
        updateChartValues
    else
        echo 'Warning! 项目缺少helm部署chart！'
        exit 1
    fi
else
    echo 'Warning! 项目缺少k8s部署配置文件！'
    exit 1
fi
"""
    }
}
