package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.forms.view.AddEditQuestionView;
import tgpr.forms.view.ViewFormView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;

public class AddEditQuestionController extends Controller<AddEditQuestionView> {

    private final AddEditQuestionView view;
    private Question question;

    public AddEditQuestionController(Question question, Form form, ViewFormView formView) {
        this.question = question;
        view = new AddEditQuestionView(this, question, form, formView);
    }

    public Question getQuestion() {
        return question;
    }

    public ErrorList validate(String title, String description, OptionList optionList, Question.Type type) {
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

        if (type != null && type.requiresOptionList() && optionList == null) {
            errors.add("OptionList required for this type", Question.Fields.OptionList);

        }
        //ajout option list
        return errors;
    }

    //pour create
    public boolean create(int formId, int idx,String title, String description, Question.Type type, boolean required, OptionList optionList) {
        // Valide les entrées
        var errors = validate(title, description, optionList,type);

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
            return true;
        } else {
            showError(String.valueOf(errors));
            return false;
        }
    }

    public void add(){
    }

    //pour update
    public boolean update(Question question, OptionList optionList) {
        var errors = validate(question.getTitle(), question.getDescription(), optionList,question.getType());
        if (errors.isEmpty()) {
            if (optionList != null){
                question.setOptionListId(optionList.getId());
            }else {
                question.setOptionListId(null);
            }
            question.save();
            return true;
        } else{
            showErrors(errors);
            return false;
        }

    }

    @Override
    public AddEditQuestionView getView() {
        return view;
    }
}
