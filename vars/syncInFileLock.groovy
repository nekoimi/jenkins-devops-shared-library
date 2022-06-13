/**
 * 在同步锁里面执行，防止多个项目同时构建冲突
 * @param target 锁名称
 * @param execClosure 执行闭包函数
 */
def call(target = 'DEFAULT', Closure execClosure) {
    def lockName = md5sum(target)
    def lock = "${BASE_WORKSPACE}/.${lockName}.lock"
    echo "file-lock: ${lock}"
    try {
        def unLock = fileExists lock
        if (unLock) {
            echo '未获取到file-lock, wait......'
            sh "sleep 3"
            call(target, execClosure)
        } else {
            def tryLock = sh(returnStatus: true, script: """
if [ -f "${lock}" ]; then
    exit 1
else
    echo 'lock' > ${lock}
    echo '获取file-lock......'
fi
""")
            if (tryLock == 0) {
                echo '获取到file-lock, 同步执行 - START......'
                execClosure.call()
                echo '获取到file-lock, 同步执行 - END......'
            } else {
                echo '未获取到file-lock, wait......'
                sh "sleep 3"
                call(target, execClosure)
            }
        }
    } finally {
        sh """
if [ -f "${lock}" ]; then
    rm -rf ${lock}
fi

echo '释放file-lock......'
"""
    }
}