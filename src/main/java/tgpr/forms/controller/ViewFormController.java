package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.forms.view.ViewFormView;
import tgpr.framework.Controller;
import static org.mariadb.jdbc.pool.Pools.close;

public class ViewFormController extends Controller<ViewFormView> {

    private final ViewFormView view;
    private final Form form;

    public ViewFormController(Form form) {
        this.form = form;
        view = new ViewFormView(this, form);
    }

    public void delete(){
        if (askConfirmation("You are about to delete this form. Please confirm.","Delete form")){
            form.deleteAllInstances();
            form.delete();
            close();
            navigateTo(new ViewFormsController());
        }
    }

    private Question updateQuestion(Question question) {
        var controller = new QuestionController(question,form);
        navigateTo(controller);
        return controller.getQuestion();
    }

    public void newQuestion(){
        Question question = null;
        var controller = new QuestionController(question, form);
        navigateTo(controller);
    }

    @Override
    public ViewFormView getView() {
        return view;
    }
}
