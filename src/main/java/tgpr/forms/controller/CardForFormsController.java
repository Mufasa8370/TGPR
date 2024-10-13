package tgpr.forms.controller;


public class CardForFormsController {
    public static String cutText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

}
