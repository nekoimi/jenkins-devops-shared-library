/**
 * <p>noticeWarning</p>
 *
 * @author nekoimi 2022/05/29
 */

def call(text = "") {
    def testClazz = text.getClass().toString()
    if (testClazz == "java.util.LinkedHashMap") {
        text = text.get("text")
    }
    echo """
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Warning <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
${text}
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Warning <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
"""
}
