import java.util.Set;
import java.util.EnumSet;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Katalculator{

    /***
     * Found this ready-to-use code. Added here with minor adjustments
     */
    public enum Numeral {
        C(100), XC(90), L(50), XL(40), X(10), IX(9), V(5), IV(4), I(1);

        public final int weight;

        private static final Set<Numeral> SET = Collections.unmodifiableSet(EnumSet.allOf(Numeral.class));

        Numeral(int weight) {
            this.weight = weight;
        }

        public static Numeral getLargest(long weight) {
            return SET.stream()
                    .filter(numeral -> weight >= numeral.weight)
                    .findFirst()
                    .orElse(I)
                    ;
        }
        public static String encode(int n) {
            return LongStream.iterate(n, l -> l - Numeral.getLargest(l).weight)
                    .limit(Numeral.values().length)
                    .filter(l -> l > 0)
                    .mapToObj(Numeral::getLargest)
                    .map(String::valueOf)
                    .collect(Collectors.joining())
                    ;
        }
        public static int decode(String roman) {
            int result =  new StringBuilder(roman).reverse().chars()
                    .mapToObj(c -> Character.toString((char) c))
                    .map(numeral -> Enum.valueOf(Numeral.class, numeral))
                    .mapToInt(numeral -> numeral.weight)
                    .reduce(0, (a, b) -> a + (a <= b ? b : -b))
                    ;
            if (roman.length()>1 && roman.charAt(0) == roman.charAt(1)) {
                result += 2 * Enum.valueOf(Numeral.class, roman.substring(0, 1)).weight;
            }
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length<1)   throw new Exception("Отсутствуют аргументы вызова");
        String expression = args[0].trim().toUpperCase();

        //Matching expression
        String arabPat ="([1-9]|(10))";
        String romaPat ="(I{1,3}|(I?V)|(VI{1,3})|(I?X))";
        String operPat="([+\\-*/])";
        Pattern exPat = Pattern.compile("^("+ arabPat +"\\s*"+operPat+"\\s*"+ arabPat +")|"+
                                               "("+ romaPat +"\\s*"+operPat+"\\s*"+ romaPat +")$");
        Matcher deMatch = exPat.matcher(expression);
        if(!deMatch.matches()) throw new Exception("Введено некорректное выражение");

        //Decode expression to pieces
        String op;
        int a;
        int b;
        boolean isArab = deMatch.group(1)!=null;
        if(isArab) {
            op =deMatch.group(4);
            a =Integer.decode(deMatch.group(2));
            b =Integer.decode(deMatch.group(5));

        }else{
            op =deMatch.group(12);
            a =Numeral.decode(deMatch.group(8));
            b =Numeral.decode(deMatch.group(13));
        }

        //Calculating at last!
        int res = switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new Exception("Неизвестная ошибка! :(");
        };

        if(isArab && res<101) System.out.println(res);
        else if(res>100) throw new Exception("Неизвестная ошибка! :(");
        else if (res<1)  throw new Exception("В римской системе нет отрицательных чисел");
        else System.out.println(Numeral.encode(res));
    }
}