package com.chatoyment.ahoyapp.OnlyJava;

import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

public class ForbiddenWords {
    final private List<String> words = Arrays.asList(
            "chuj", "kurwa", "pizda", "pierdol", "skurwysyn", "jebany", "zjeb", "srać",
            "wkurwiać", "jebać", "cipa", "chujowy", "kurwica", "pierdolony", "popierdolony",
            "skurwiel", "zajebisty", "zajebiście", "zasrany", "dupczyć", "czarnuch", "cwel", "debil", "idiota",
            "gówno", "dziwka", "pierdolić", "chujnia", "ciul", "dupka", "huj", "jebut", "kał",
            "kurwić", "kurwiszon", "kutafon", "kutas", "pierdalnij", "pojeb", "pojebało", "pojebany",
            "poruchać", "ruchać", "rozpierdalać", "spermiarz",
            "spierdalać", "szmata", "wyjebać", "wyjebany", "wypierdalać", "wytrzep", "zajeb", "zajebać",
            "zapierdalać", "zapierdol", "zjebany", "zjebać", "zjeby", "głupek", "pajac", "palant", "frajer",
            "imbecyl", "kretyn", "idiotka", "kutas", "fiut", "chujek", "cwaniak", "matoł", "przygłup",
            "cham", "jebak", "kurwisz", "niedojeb", "cwel", "frajer", "kujon", "gnida",
            "pierdolisz", "rucha", "japierdole", "jebnie", "zjeba", "spierdalaj", "wypierdalaj",
            "wypierdala", "spierdolił", "spierdoliła", "wyjebał", "wyjebali", "kurwo", "dupku", "kutasie",
            "pierdolca", "pierdolona", "pierdolisz", "gnojek", "gnoju", "jebaka", "jebaku",
            "popierdolone", "popierdol", "zjebane", "zjebano", "debilu", "debilko",
            "debilem", "frajerko", "frajerka", "frajerzy", "dziwką", "dziwkami", "dziwkarz",
            "jebakiem", "jebakami", "jebaków", "gówniarz", "gówniarze", "cwelu",
            "cwela", "cwele", "cwelami", "cweli", "jebakowi", "dupa", "dupami", "dup",
            "dupków", "japierdolę", "skurwił", "skurwiła", "skurwiała", "kurwie", "kurwią", "szmatą",
            "szmato", "chujnia", "fiuta", "fiuty", "fiutem", "skurwielem",
            "skurwielu", "skurwie", "skurwielem", "skurwieci", "imbecylem",
            "imbecyli", "jebaną", "jebane", "jebanego", "jebani", "chamami", "chama",
            "chamów", "fiutami", "fiutem", "jebana", "jebanego", "jebane", "jebaną",
            "jebała", "jebałaś", "fiuta", "fiut", "jebany", "jebani", "jebanymi", "fiutami",
            "skurwielami", "skurwiel", "dupa", "dupka", "dupy", "dupami", "kutasami", "kutasie",
            "fiuty", "fiutem", "jebaków", "frajerze", "frajerzy",
            "idiotko", "idioci", "fiut", "jebakami", "jebaków", "debilem", "debilką", "debilki",
            "debilką", "idiotami", "idiotów", "idiotko", "pizdą", "pizd", "kurwą", "kurwy", "kurwą",
            "pizdą", "kurwą", "pizdą", "kurwy", "kurwami", "kurwą", "fiutem", "fiuty", "dupkami", "dupki",
            "debilami", "debilko", "imbecyli", "idioci", "idiota", "idiotami", "idioci",
            "debilami", "debil", "fiutem", "fiutami", "dup", "dupą", "frajerami", "frajer", "frajerki",
            "dupą", "frajerzy", "frajerzy", "frajer", "frajerów", "jebakami", "jebak", "debilu", "debilem",
            "idioci", "fiut", "idiotami", "fiuty", "dupą", "frajerze", "idioci", "frajerami",
            "fiutami", "idioci", "debilkami", "jebanymi", "jebane", "idiotami", "frajerami",
            "kurwami", "frajerki", "jebak", "kurwami", "kurw", "fiutami", "idioci",
            "dupą", "frajerze", "kurwami", "fiutami", "frajerkami",
            "frajerami", "dup", "jebak", "frajer", "idiota", "frajerki", "jebakami", "debilkami", "fiutami",
            "debilami", "frajerze", "frajerami", "jebakami", "frajerkami", "frajerów", "jebaków", "jebak",
            "debilami", "idioci", "fiutami", "debilkami", "kurwami", "fiut", "debilami",
            "debilkami", "kurwami", "frajerze", "frajerami", "debilami", "fiutami", "frajerkami", "debilami",
            "jebakami", "frajerze", "fiut", "jebakami", "frajer", "frajer", "debilkami",



            "fuck", "shit", "asshole", "bitch", "bastard", "dick", "cunt", "motherfucker",
            "piss", "cock", "prick", "wanker", "twat", "douchebag", "bugger",
            "damn", "crap", "slut", "nigger", "faggot", "nigga", "faggots", "arse",
            "bollocks", "bloody", "bullshit", "cocksucker", "dickhead", "fag", "goddamn",
            "hoe", "jackass", "jerk", "pussy", "retard", "shithead", "sonofabitch", "whore",
            "dumbass", "dumbfuck", "dipshit", "shitbag", "shitface", "shite", "shithole",
            "shithouse", "motherfucking", "fuckface", "fuckhead", "shitfuck", "twatface",
            "cuntface", "assfuck", "bitchass", "motherfuckers", "simp", "wankstain",
            "knobhead", "bellend", "tosser", "numpty", "pillock", "plonker", "muppet",
            "gobshite", "arsehole", "slag", "prat", "scumbag", "wazzock", "dickwad",
            "dickweed", "fuckwit", "knob", "scrote", "jizz", "minge", "spunk", "bollock",
            "bellend", "pissflaps", "chav", "cockwomble", "arsewipe", "dickweasel",
            "fucknugget", "minger", "munter", "dickbag", "gash", "schmuck", "schlong",
            "schmuck", "jizzmopper", "shithawk");

    public List<String> getWords() {
        return words;
    }
    public static boolean containsForbiddenWord(EditText editText) {
        List<String> forbiddenWords = new ForbiddenWords().getWords();

        String text = editText.getText().toString().toLowerCase();

        for (String word : forbiddenWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
