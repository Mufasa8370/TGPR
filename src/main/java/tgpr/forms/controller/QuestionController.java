package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.forms.view.QuestionView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

public class QuestionController extends Controller<QuestionView> {

    private final QuestionView view;
    private Question question;

    public QuestionController(Question question, Form form) {
        this.question = question;
        view = new QuestionView(this, question, form);
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
    public void create(int formId, int idx,String title, String description, Question.Type type, boolean required, OptionList optionList) {
        // Valide les entrées
        var errors = validate(title, description);

        if (errors.isEmpty()) {
            // Crée une nouvelle instance de Question
            question = new Question();
            question.setFormId(formId);
            question.setIdx(idx);
            question.setTitle(title);
            question.setDescription(description);
            question.setType(type);
            question.setRequired(required);
            question.setOptionListId(optionList != null ? optionList.getId() : null); // Assurez-vous d'avoir l'ID de la liste d'options

            // Enregistre la question
            question.save();
        } else {
            showError(String.valueOf(errors));
        }
    }

    public void add(){
    }

    //pour update
    public void update(Question question, OptionList optionList) {
        var errors = validate(question.getTitle(), question.getDescription());
        if (errors.isEmpty()) {
            if (optionList != null){
                question.setOptionListId(optionList.getId());
            }else {
                question.setOptionListId(null);
            }
            question.save();
        } else
            showErrors(errors);
    }

    @Override
    public QuestionView getView() {
        return view;
    }
}
