/**
 * md5加密
 * @param text 待加密
 */
def call(text) {
    md5Value = sh(returnStdout: true, script: """
echo \$(echo "${text}" | md5sum | awk '{print \$1}' | sed s/[[:space:]]//g)
""")
    return md5Value.toString().trim()
}