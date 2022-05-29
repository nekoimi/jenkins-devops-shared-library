/**
 * <p>noticeWarning</p>
 *
 * @author nekoimi 2022/05/29
 */

def call(text = "") {
    def testClazz = text.getClass()
    if (testClazz == "java.util.LinkedHashMap") {
        text = text.get("text")
    }
    println(text)
    println(text.getClass())
    echo """
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Warning <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
${text}
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Warning <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
"""
}
