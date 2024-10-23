package tgpr.forms.controller;


import tgpr.forms.model.*;

import java.util.List;

import static tgpr.forms.model.Question.getAll;
import static tgpr.forms.model.Security.getLoggedUser;
import static tgpr.forms.model.Security.isLogged;
import static tgpr.framework.Controller.navigateTo;
import static tgpr.framework.Controller.showMessage;


public class CardForFormsController {
    public static String cutText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String[] getLastInstance(User user, Form form){
        String pro;
        Instance i = form.getMostRecentInstance(user);
         if(i != null && !user.isGuest()){
             String hour = i.getStarted().getHour() < 10 ? "0"+i.getStarted().getHour() : String.valueOf(i.getStarted().getHour());
             String min = i.getStarted().getMinute() < 10 ? "0"+i.getStarted().getMinute() : String.valueOf(i.getStarted().getMinute());
             String sec = i.getStarted().getSecond() < 10 ? "0"+i.getStarted().getSecond() : String.valueOf(i.getStarted().getSecond());
             if(i.getCompleted() != null){
                 String hourC = i.getCompleted().getHour() < 10 ? "0"+i.getCompleted().getHour() : String.valueOf(i.getCompleted().getHour());
                 String minC = i.getCompleted().getMinute() < 10 ? "0"+i.getCompleted().getMinute() : String.valueOf(i.getCompleted().getMinute());
                 String secC = i.getCompleted().getSecond() < 10 ? "0"+i.getCompleted().getSecond() : String.valueOf(i.getCompleted().getSecond());
                 pro = "Completed on " + i.getCompleted().getDayOfMonth() + "/" +
                         i.getCompleted().getMonthValue() + "/" +
                         i.getCompleted().getYear() + " " +
                         hourC + ":" +
                         minC + ":" +
                         secC;
             }else {
                 pro = "In progress";
             }

             return new String[]{
                     "Started on " + i.getStarted().getDayOfMonth() + "/" +
                             i.getStarted().getMonthValue() + "/" +
                             i.getStarted().getYear() + " " +
                             hour + ":" +
                             min + ":" +
                             sec,
                     pro
             };

        }
         return new String[]{"Not Started", ""};
    };

    //MODEL ????????????
    public static boolean questionInForm(Form form){
        List<Question> qs = getAll();
        for( Question q : qs ){
            if (form.getId() == q.getFormId()){
                return true;
            }
        }
        return false;
    }
    public static boolean accesTypeEditor(Form form){
        return form.getAccessType(getLoggedUser()) == AccessType.Editor;
    }


    public void open(Instance i, Form form) {
        navigateTo(new ViewEditInstanceController(i, form));
     }

    public void manage(Form form) {
        showMessage("Ouverture du form "+form.getTitle()+".Le use case view_form est en préparation :)","Info","Close");
    }
}
