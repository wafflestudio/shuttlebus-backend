package com.waffle.shattlebus.backend.searching;

public class SearchHangul {

    static String text = "가깋낗닣딯띻맇밓빟삫싷잏짛찧칳킿팋핗힣";
    static String jamoRef = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅇㅈㅉㅊㅋㅌㅍㅎ";

    static char[] textArr = text.toCharArray();
    static char[] jamoArr = jamoRef.toCharArray();

    // 단어 검색
    static String findEndKeyword(String keyword){
        // 마지막 글자
        char targetChar = keyword.charAt(keyword.length() - 1);
        char endChar = '0';

        // 입력값의 끝 문자가 ㄱ...ㅎ 인 경우
        if(targetChar <= 12622) {
            for(int i = 0; i < jamoArr.length; i++) {
                if(targetChar == jamoArr[i]) {
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
        System.out.println(findEndKeyword("가글"));
        System.out.println(findEndKeyword("가ㅇ"));
    }
}
