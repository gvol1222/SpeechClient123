package Utils;

/**
 * Created by bill on 11/17/17.
 */

import org.apache.commons.text.StrBuilder;

import java.util.HashMap;
import java.util.List;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class SearchStringHelper {


    public static HashMap<String, Double> getBestStringMatch(List<String> list, String query) {
        JaroWinkler jr = new JaroWinkler();
        String bestmatch = null;
        double max_match = 0.0;
        for (String elem : list) {

            if (jr.similarity(greeknorm(query), greeknorm(elem)) > max_match) {
                max_match = jr.similarity(greeknorm(query), greeknorm(elem));
                bestmatch = elem;
            }
            if (jr.similarity(greeknorm(elem), greeklishnorm(query)) > max_match) {
                max_match = jr.similarity(greeknorm(elem), greeklishnorm(query));
                bestmatch = elem;
            }
        }
        HashMap<String, Double> result = new HashMap<>();
        if (max_match < 0.9) {
            result.put("no_contact", max_match);

        } else {
            result.put(bestmatch, max_match);

        }
        //  result.put(bestmatch, max_match);

        return result;
    }

    public static double getContactSimilairty(String name, String query) {
        JaroWinkler jr = new JaroWinkler();
        String bestmatch = null;
        double max_match = 0.0;

        if (jr.similarity(greeknorm(query), greeknorm(name)) > max_match) {
            max_match = jr.similarity(greeknorm(query), greeknorm(name));
        }
        if (jr.similarity(greeknorm(name), greeklishnorm(query)) > max_match) {
            max_match = jr.similarity(greeknorm(name), greeklishnorm(query));
        }
        return max_match;
    }

    private static String greeknorm(String word) {
        String norm = word.toLowerCase();
        StrBuilder wordbld = new StrBuilder(norm);

        wordbld.replaceAll("ά", "α");
        wordbld.replaceAll("έ", "ε");
        wordbld.replaceAll("ή", "η");
        wordbld.replaceAll("ί", "ι");
        wordbld.replaceAll("ό", "ο");
        wordbld.replaceAll("ύ", "υ");
        wordbld.replaceAll("ώ", "ω");
        wordbld.replaceAll("ς", "σ");

        return wordbld.toString();
    }

    private static String greeklishnorm(String word) {
        String norm = word.toLowerCase();
        StrBuilder wordbld = new StrBuilder(norm);


//        special cases
        wordbld.replaceAll("ου", "ou");
//        accent removal
        wordbld.replaceAll("ά", "α");
        wordbld.replaceAll("έ", "ε");
        wordbld.replaceAll("ή", "η");
        wordbld.replaceAll("ί", "ι");
        wordbld.replaceAll("ό", "ο");
        wordbld.replaceAll("ύ", "υ");
        wordbld.replaceAll("ώ", "ω");
        wordbld.replaceAll("ς", "σ");
//        translation
        wordbld.replaceAll("α", "a");
        wordbld.replaceAll("β", "b");
        wordbld.replaceAll("γ", "g");
        wordbld.replaceAll("δ", "d");
        wordbld.replaceAll("ε", "e");
        wordbld.replaceAll("ζ", "z");
        wordbld.replaceAll("η", "i");
        wordbld.replaceAll("θ", "th");
        wordbld.replaceAll("ι", "i");
        wordbld.replaceAll("κ", "k");
        wordbld.replaceAll("λ", "l");
        wordbld.replaceAll("μ", "m");
        wordbld.replaceAll("ν", "n");
        wordbld.replaceAll("ξ", "x");
        wordbld.replaceAll("ο", "o");
        wordbld.replaceAll("π", "p");
        wordbld.replaceAll("ρ", "r");
        wordbld.replaceAll("σ", "s");
        wordbld.replaceAll("τ", "t");
        wordbld.replaceAll("υ", "i");
        wordbld.replaceAll("φ", "f");
        wordbld.replaceAll("χ", "ch");
        wordbld.replaceAll("ψ", "ps");
        wordbld.replaceAll("ω", "o");


        return wordbld.toString();
    }

}
