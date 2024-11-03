package tgpr.forms.controller;

import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.view.EditFormView;
import tgpr.framework.Controller;

public class EditFormController  extends Controller<EditFormView> {

    private final EditFormView view;
    private Form form;
    private final boolean isNew;

    public EditFormController(){
        this(null);
    }
    public EditFormController(Form form) {
        this.form = form;
        this.isNew = form == null;
        view = new EditFormView(this,form);
    }

    public boolean isValidTitle(String title){
        return !(title.isEmpty() || title.isBlank());
    }
    @Override
    public EditFormView getView() {
        return view;
    }

    public boolean save(String title, String description, boolean isPublic){
        if (isNew) {
            // si c'est un nouveau formulaire
            if (Security.getLoggedUser() != null) {
                // si il es connecté
                this.form = new Form(title, description, Security.getLoggedUser(), isPublic);
                this.form.save();
                // pour s'assurer que tout a bien été effectué.
                return true;
            } else {
                return false;
            }
        } else {
            // TODO: ajouter, en mode édition, la box de confirmation quand
            // un formulaire est rendu public alors qu'au départ il est privé.
            // Rendre public un formulaire supprime tous les partages précédents.
            if (Security.getLoggedUser() != null) {
                this.form = new Form(title, description, Security.getLoggedUser(), isPublic);
                this.form.save();
                return true;
            } else {
                return false;
            }
        }
    }

}
