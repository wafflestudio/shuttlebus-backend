package com.waffle.shattlebus.backend.searching;

import java.security.Key;
import java.util.Arrays;

public class SearchHangul {
    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588; //각자음 마다 가지는 글자수
    static String text = "가깋낗닣딯띻맇밓빟삫싷잏짛찧칳킿팋핗힣";
    static String INITIAL = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅇㅈㅉㅊㅋㅌㅍㅎ";
    static String INITIAL_BEGIN = "가까나다따라마바빠사아자짜차카타파하";

    static char[] textArr = text.toCharArray();
    static char[] INITIAL_SOUND = INITIAL.toCharArray();
    static char[] INITIAL_BEGIN_SOUND = INITIAL_BEGIN.toCharArray();

    /**
     * 해당 문자가 INITIAL_SOUND인지 검사.
     *
     * @param searchar
     * @return
     */
    private static boolean isInitialSound(char searchar){
        for(char c:INITIAL_SOUND){
            if(c == searchar){
                return true;
            }
        }
        return false;
    }

    /**
     * 해당 문자가 한글인지 검사
     *
     * @param c 문자 하나
     * @return
     */
    private static boolean isHangul(char c) {
        return isInitialSound(c) || (HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE);
    }

    /**
     * 문자 하나의 검색 범위를 찾음
     *
     * @param searchar char 하나
     * @return beginChar
     */
    static char findBeginChar(char searchar){
        for(int i=0; i<INITIAL_SOUND.length; i++){
            if(INITIAL_SOUND[i]==searchar){
                return INITIAL_BEGIN_SOUND[i];
            }
        }
        return searchar;
    }


    /**
     * 문자 하나의 검색 범위를 찾음
     *
     * @param targetChar char 하나
     * @return endChar
     */
    static char findEndChar(char targetChar){
        char endChar = '0';

        // 입력값의 끝 문자가 ㄱ...ㅎ 인 경우
        if(isInitialSound(targetChar)) {
            for(int i = 0; i < INITIAL_SOUND.length; i++) {
                if(targetChar == INITIAL_SOUND[i]) {
                    endChar = textArr[i + 1];
                }
            }
        } else {
            // 입력값의 끝 문자가 가...힣 인 경우
            for(int i = 0; i < text.length(); i++) {
                if(targetChar >= textArr[i] && targetChar < textArr[i + 1]) {
                    if((textArr[i+1]-targetChar)%28!=27){ // 종성이 있는 경우
                        endChar = targetChar;
                    }
                    else{ // 종성이 없는 경우
                        endChar = (char) (textArr[i+1]-((textArr[i+1]-targetChar)/28)*28);
                    }
                }
            }
        }
        return endChar;
    }

    /**
     * word에 keyword가 속하는지 검사
     *
     * @param word
     * @param keyword
     * @return boolean
     */
    public static int compWord(String word, String keyword){
        int wlen = word.length(), kwlen = keyword.length();
        for(int i = 0; i <= (wlen - kwlen); i++){
            boolean flag = true;
            for(int j = 0; j < kwlen; j++) {
                char a = word.charAt(i+j), b = keyword.charAt(j);
                if (isHangul(a) && isHangul(b) && (findBeginChar(b)>a || a>findEndChar(b))) flag=false;
                if ((!isHangul(a) || !isHangul(b)) && a!=b ) flag = false;
            }
            if(flag) return i+kwlen;
        }
        return -1;
    }
}
