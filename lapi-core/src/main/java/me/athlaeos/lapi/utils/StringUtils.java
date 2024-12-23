package me.athlaeos.lapi.utils;

import org.bukkit.ChatColor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StringUtils {
    public static String toPascalCase(String s){
        if (s == null) return null;
        if (s.isEmpty()) return s;
        String allLowercase = s.toLowerCase(java.util.Locale.US);
        char c = allLowercase.charAt(0);
        return allLowercase.replaceFirst("" + c, "" + Character.toUpperCase(c));
    }

    private final static TreeMap<Integer, String> romanNumeralsMap = new TreeMap<>();

    static {
        romanNumeralsMap.put(1000, "M");
        romanNumeralsMap.put(900, "CM");
        romanNumeralsMap.put(500, "D");
        romanNumeralsMap.put(400, "CD");
        romanNumeralsMap.put(100, "C");
        romanNumeralsMap.put(90, "XC");
        romanNumeralsMap.put(50, "L");
        romanNumeralsMap.put(40, "XL");
        romanNumeralsMap.put(10, "X");
        romanNumeralsMap.put(9, "IX");
        romanNumeralsMap.put(5, "V");
        romanNumeralsMap.put(4, "IV");
        romanNumeralsMap.put(1, "I");
    }

    public static String toRoman(int number) {
        if (number < 0) return "-" + toRoman(-number);
        if (number == 0) return "0";
        if (number == 1) return "I";
        int l = romanNumeralsMap.floorKey(number);
        if (number == l) return romanNumeralsMap.get(number);
        return romanNumeralsMap.get(l) + toRoman(number - l);
    }

    public static Double parseDouble(String s) throws NumberFormatException {
        return Double.parseDouble(s.replace(",", "."));
    }

    public static Float parseFloat(String s) throws NumberFormatException {
        return Float.parseFloat(s.replace(",", "."));
    }

    static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static List<String> separateStringIntoLines(String string, int maxLength){
        List<String> lines = new ArrayList<>();
        String[] byNewLines = string.split("/n");
        for (String line : byNewLines){
            String[] words = line.split(" ");
            StringBuilder sentence = new StringBuilder(words[0]);
            for (String word : Arrays.copyOfRange(words, 1, words.length)){
                String rawWord = ChatColor.stripColor(Utils.chat(word.replaceAll(hexPattern.pattern(), "")));
                String rawSentence = ChatColor.stripColor(Utils.chat(sentence.toString().replaceAll(hexPattern.pattern(), "")));
                if (rawSentence.length() + rawWord.length() > maxLength){
                    lines.add(sentence.toString());
                    String previousSentence = sentence.toString();
                    sentence = new StringBuilder(Utils.chat(ChatColor.getLastColors(Utils.chat(previousSentence)))).append(word);
                } else sentence.append(" ").append(word);
            }
            lines.add(sentence.toString());
        }
        return lines;
    }

    public static boolean isEmpty(String str){
        return str == null || str.isEmpty();
    }

    /**
     * Goes through the original list and inserts the replacement list when it finds a string
     * containing the placeholder. The line containing the placeholder is itself not added back. <br>
     * Any lines not containing the placeholder are also kept in the list.
     * @param original The original list
     * @param placeholder The placeholder which lines in the original list are checked if they contain said placeholder
     * @param replaceWith The list of strings that will replace the line containing the placeholder
     * @return The modified list
     */
    public static List<String> setListPlaceholder(List<String> original, String placeholder, List<String> replaceWith){
        List<String> lore = new ArrayList<>();
        for (String s : original){
            if (s.contains(placeholder)) {
                String[] split = s.split(Pattern.quote(placeholder));
                String prefix = split.length > 0 ? split[0] : "";
                String suffix = split.length > 1 ? split[1] : "";

                lore.addAll(replaceWith.stream().map(l -> String.format("%s%s%s", prefix, l, suffix)).toList());
            }
            else lore.add(s);
        }
        return lore;
    }

    public static int getMaxStringLength(List<String> list, Function<String, String> lineFunction){
        return list.stream()
                .map(s -> lineFunction.apply(ChatColor.stripColor(Utils.chat(s))))
                .map(s -> s.chars().mapToObj(c -> (char) c))
                .map(chars -> chars.map(DefaultFontInfo::getDefaultFontInfo).mapToInt(i -> i.length).sum())
                .max(Comparator.comparingInt(Integer::intValue)).orElse(0);
    }

    public static List<String> standardCenter(List<String> list){
        return center(list, s -> s.startsWith("#c:"), s -> s.replaceAll("#c:", ""));
    }

    public static List<String> center(List<String> list, Predicate<String> condition, Function<String, String> lineFunction){
        if (list.isEmpty()) return new ArrayList<>();
        // the longest line length in the list
        return center(list, condition, lineFunction, getMaxStringLength(list, lineFunction));
    }

    public static List<String> center(List<String> list, Predicate<String> condition, Function<String, String> lineFunction, int maxListSize){
        if (list.isEmpty()) return new ArrayList<>();
        List<String> newList = new ArrayList<>();
        // This should realistically never occur
        if (maxListSize <= 0) return newList;
        for (String s : list){
            String rawString = ChatColor.stripColor(Utils.chat(s));
            if (!condition.test(rawString) && !condition.test(s)) {
                newList.add(s);
                continue;
            }
            rawString = lineFunction.apply(rawString);
            s = lineFunction.apply(s);
            // First we remove all the chat color from the string because they should not contribute
            // to a string's pixel length
            // Empty strings are added as they are
            if (rawString.isEmpty()) {
                newList.add(s);
                continue;
            }

            int size = rawString.chars().mapToObj(c -> (char) c).map(DefaultFontInfo::getDefaultFontInfo).mapToInt(i -> i.length).sum();
            int whitespace = maxListSize - size;
            int spaceCount = (int) Math.round((whitespace / 2D) / (double) DefaultFontInfo.SPACE.length);

            newList.add(" ".repeat(Math.max(0, spaceCount)) + s);
        }
        return newList;
    }

    private enum DefaultFontInfo {
        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PARENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }
}
