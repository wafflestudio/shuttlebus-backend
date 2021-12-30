package com.wafflestudio.shattlebus.search

object SearchHangul {
    private const val HANGUL_BEGIN_UNICODE = 44032 // 가
        .toChar()
    private const val HANGUL_LAST_UNICODE = 55203 // 힣
        .toChar()
    private const val HANGUL_BASE_UNIT = 588 // 각자음 마다 가지는 글자수
        .toChar()
    var text = "가깋낗닣딯띻맇밓빟삫싷잏짛찧칳킿팋핗힣"
    var INITIAL = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅇㅈㅉㅊㅋㅌㅍㅎ"
    var INITIAL_BEGIN = "가까나다따라마바빠사아자짜차카타파하"
    var textArr = text.toCharArray()
    var INITIAL_SOUND = INITIAL.toCharArray()
    var INITIAL_BEGIN_SOUND = INITIAL_BEGIN.toCharArray()

    /**
     * 해당 문자가 INITIAL_SOUND인지 검사.
     *
     * @param searchar
     * @return
     */
    private fun isInitialSound(searchar: Char): Boolean {
        for (c in INITIAL_SOUND) {
            if (c == searchar) {
                return true
            }
        }
        return false
    }

    /**
     * 해당 문자가 한글인지 검사
     *
     * @param c 문자 하나
     * @return
     */
    private fun isHangul(c: Char): Boolean {
        return isInitialSound(c) || HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE
    }

    /**
     * 문자 하나의 검색 범위를 찾음
     *
     * @param searchar char 하나
     * @return beginChar
     */
    fun findBeginChar(searchar: Char): Char {
        for (i in INITIAL_SOUND.indices) {
            if (INITIAL_SOUND[i] == searchar) {
                return INITIAL_BEGIN_SOUND[i]
            }
        }
        return searchar
    }

    /**
     * 문자 하나의 검색 범위를 찾음
     *
     * @param targetChar char 하나
     * @return endChar
     */
    fun findEndChar(targetChar: Char): Char {
        var endChar = '0'

        // 입력값의 끝 문자가 ㄱ...ㅎ 인 경우
        if (isInitialSound(targetChar)) {
            for (i in INITIAL_SOUND.indices) {
                if (targetChar == INITIAL_SOUND[i]) {
                    endChar = textArr[i + 1]
                }
            }
        } else {
            // 입력값의 끝 문자가 가...힣 인 경우
            for (i in 0 until text.length) {
                if (targetChar >= textArr[i] && targetChar < textArr[i + 1]) {
                    endChar = if ((textArr[i + 1] - targetChar) % 28 != 27) { // 종성이 있는 경우
                        targetChar
                    } else { // 종성이 없는 경우
                        textArr[i + 1] - (textArr[i + 1] - targetChar) / 28 * 28
                    }
                }
            }
        }
        return endChar
    }

    /**
     * word에 keyword가 속하는지 검사
     *
     * @param word
     * @param keyword
     * @return boolean
     */
    fun compWord(word: String, keyword: String): Int {
        val wlen = word.length
        val kwlen = keyword.length
        for (i in 0..wlen - kwlen) {
            var flag = true
            for (j in 0 until kwlen) {
                val a = word[i + j]
                val b = keyword[j]
                if (isHangul(a) && isHangul(b) && (findBeginChar(b) > a || a > findEndChar(b))) flag = false
                if ((!isHangul(a) || !isHangul(b)) && a.uppercaseChar() != b.uppercaseChar()) flag = false
            }
            if (flag) return i + kwlen
        }
        return -1
    }
}
