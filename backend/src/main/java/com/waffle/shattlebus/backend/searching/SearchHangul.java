package com.waffle.shattlebus.backend.searching;

public class SearchHangul {
    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588; //각자음 마다 가지는 글자수
    static String text = "가깋낗닣딯띻맇밓빟삫싷잏짛찧칳킿팋핗힣";
    static String INITIAL = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅇㅈㅉㅊㅋㅌㅍㅎ";

    static char[] textArr = text.toCharArray();
    static char[] INITIAL_SOUND = INITIAL.toCharArray();

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
     * 해당 문자의 자음을 얻는다.
     *
     * @param c 검사할 문자
     * @return
     */
    private static char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    /**
     * 해당 문자가 한글인지 검사
     *
     * @param c 문자 하나
     * @return
     */
    private static boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }

    /**
     * 단어 검색시 범위의 EndPoint를 찾음
     *
     * @param keyword 단어 하나
     * @return endKeyword
     */
    static String findEndKeyword(String keyword){
        // 마지막 글자
        char targetChar = keyword.charAt(keyword.length() - 1);
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
        String endKeyword = keyword.substring(0, keyword.length() - 1) + endChar;

        // System.out.println(keyword + " to the end: " + endKeyword);

        return endKeyword;
    }

    // for testing
    public static void main(String[] args){
        String a = "곰나담랑밍";
        String b = "가나다라마";
        String c = "갛낳닿랗맣";
        System.out.println(a.compareTo(b)+" "+a.compareTo(c));
        System.out.println(findEndKeyword("가글"));
        System.out.println(findEndKeyword("가ㅇ"));
    }
}
