/**
 * <p>noticeWarning</p>
 *
 * @author nekoimi 2022/05/29
 */

def call(text = "") {
    println(text)
    println(text.getClass())
    echo """
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Warning <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
${text}
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Warning <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
"""
}
