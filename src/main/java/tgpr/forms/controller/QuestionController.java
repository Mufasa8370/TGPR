package tgpr.forms.controller;

import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.forms.view.QuestionView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

import javax.naming.ldap.Control;

import static tgpr.framework.Tools.hash;
import static tgpr.framework.Tools.toDate;

public class QuestionController extends Controller<QuestionView> {

    private final QuestionView view;
    private Question question;

    public QuestionController(Question question) {
        this.question = question;
        view = new QuestionView(this, question);
    }

    public Question getQuestion() {
        return question;
    }

    public ErrorList validate(String title, String description) {
        var errors = new ErrorList();
        if (title == null || title.isEmpty()) {
            errors.add("Title required", Question.Fields.Title);
        }
        else if (title.length() < 3) {
            errors.add("Minimum 3 chars", Question.Fields.Title);
        }

        if(description.length() < 3){
            errors.add("Minimum 3 chars", Question.Fields.Description);
        }
        //ajout option list
        return errors;
    }

    //pour create
    public void create(String title, String description, Question.Type type, boolean required, OptionList optionList) {
        // Valide les entrées
        var errors = validate(title, description);

        if (errors.isEmpty()) {
            // Crée une nouvelle instance de Question
            question = new Question();
            question.setTitle(title);
            question.setDescription(description);
            question.setType(type);
            question.setRequired(required);
            //question.setOptionList(optionList != null ? optionList.getId() : null); // Assurez-vous d'avoir l'ID de la liste d'options

            // Enregistre la question
            question.save();

            // Ferme la vue
            view.close();
        } else {
            showError(String.valueOf(errors));
        }
    }

    public void add(){
    }

    //pour update
    public void save(String title, String description, Question.Type type, boolean required, OptionList optionList) {
        var errors = validate(title, description);
        if (errors.isEmpty()) {
            question.save();
            view.close();
        } else
            showErrors(errors);
    }

    @Override
    public QuestionView getView() {
        return view;
    }
}
