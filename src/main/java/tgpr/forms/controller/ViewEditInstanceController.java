package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.Panel;
import tgpr.forms.model.Answer;
import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.model.Question;
import tgpr.forms.view.ViewEditInstanceView;
import tgpr.forms.view.ViewFormsView;
import tgpr.framework.Controller;

import java.util.List;

import static tgpr.forms.model.Security.*;

public class ViewEditInstanceController extends Controller<ViewEditInstanceView> {
    private final ViewEditInstanceView view;
    private Instance instance;
    private Form form;


    @Override
    public ViewEditInstanceView getView() {
        return view;
    }

    public ViewEditInstanceController(Instance i, Form form) {
        this.form = form;
        if(i != null){
            this.instance = i;
            if(i.getCompleted() != null){

                view = new ViewEditInstanceView(this, i, form);
            }else {
                //Instance non completée
                view = new ViewEditInstanceView(this,form,instance,1);

            }

        }else {
            Instance instanceNew = new Instance(form, getLoggedUser());
            this.instance = instanceNew;

            //Aucune instance
            view = new ViewEditInstanceView(this,form, instanceNew,1);
        }

    }
    public ViewEditInstanceController(Form form, Instance instance) {
        this.form = form;
        this.instance = instance;
        view = new ViewEditInstanceView(this, form,instance);


    }


    public ViewEditInstanceController(Instance i, Form form, int current) {
        this.form = form;
        this.instance = i;
        view = new ViewEditInstanceView(this,form,instance,current);

    }
    public ViewEditInstanceController(Form form, ViewEditInstanceView view) {
        this.view = view;
    }

    //public Question getNextQuestion()

    public void viewSubmission() {
        close();
        navigateTo(new ViewEditInstanceController(form,instance));

    }

    public void submitAgain() {
        close();
        navigateTo(new ViewEditInstanceController(null,form));
    }

    public void cancel(Instance instance) {

        boolean confirmed = askConfirmation("Are you sure you want to delete this instance an its answers?", "Delete Instance ?");
        if (confirmed) {
            List<Question> questions = form.getQuestions();
            for(Question question : questions){
                Answer answer = instance.getAnswer(question);
                if(answer != null){
                    answer.delete();
                }
            }
            instance.delete();
            navigateTo(new ViewFormsController());

        }
    }
    //Fonction pour remove un panel
    public void removePanelError(Panel pan){
        pan.removeAllComponents();
    }


    public void close() {
        if(isGuest()){
            cancel(instance);
        }else{
            view.close();
        }
    }

    public void submit(Instance instance) {
        List<Question> questions = form.getQuestions();
        for(int i = 0; i < questions.size(); i++){
            if(!verifyQuestionRequired(questions.get(i),instance)){
                showMessage("You must correct all errors before submitting the form.","Error","OK");
                close();
                navigateTo(new ViewEditInstanceController(instance,form,i+1));
            }
            if(!verifyQuestionFormat(questions.get(i),instance)){
                showMessage("You must correct all errors before submitting the form.","Error","OK");
                close();
                navigateTo(new ViewEditInstanceController(instance,form,i+1));
            }

        }
        boolean confirmed = askConfirmation("Are you sure you want to submit this form?", "Submit Form");
        if (confirmed) {
            instance.submit();
            showMessage("The form has been successyfully submitted", "Information", "OK");
            close();
            navigateTo(new ViewFormsController());

        }



    }

    public boolean verifyQuestionRequired(Question question, Instance instance){
        if(question.getRequired()){
            Answer answer = instance.getAnswer(question);
            return answer != null;

        }

        return true;

    }

    public boolean verifyQuestionFormat(Question currentQuestion, Instance instance){

        Answer answer = instance.getAnswer(currentQuestion);
        if(answer != null){
            if (currentQuestion.getType().toString().equals("Email")) {
                return validateEmail(answer.getValue());
            } else if (currentQuestion.getType().toString().equals("Date")) {
                return validateDate(answer.getValue());
            }
        }


        return true;

    }
    public boolean validateDate(String dateCurrent ){;
        String regex = "^([0-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{3,}$";
        return dateCurrent.matches(regex);
    }



    public boolean validateEmail(String emailCurrent){
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return emailCurrent.matches(regex);
    }
}
