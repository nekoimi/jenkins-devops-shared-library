package com.nekoimi
/**
 * <p>java项目标准构建流程</p>
 *
 * @author nekoimi 2022/05/29
 */

def build(yamlConf) {
    buildWithCache(yamlConf, "-v /root/.m2:/root/.m2", {
        sh """
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
    })

    buildResultCopy(yamlConf, "target/app.jar", "${MY_PROJECT_NAME}.jar")
    buildResultCopy(yamlConf, "target/app.war", "${MY_PROJECT_NAME}.war")
}

def unitTesting(yamlConf) {
}

def deployRelease(yamlConf) {
    notice("${MY_BUILD_ENV}", '>>>>>>>>>>>>>>>>> 忽略部署 <<<<<<<<<<<<<<<<<')
}
