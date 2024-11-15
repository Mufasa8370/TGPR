package tgpr.forms.controller;

import tgpr.forms.model.AccessType;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.view.EditFormView;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.Objects;

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
        return !(title.isEmpty() || title.isBlank()) && !(title.length() < 3) ;
    }
    @Override
    public EditFormView getView() {
        return view;
    }


    public boolean titleUsed(String title){
        if( isNew){
            // lors d'une creation d'un formulaire
            for( Form elem:  Form.getAll()){
                if(elem.getOwnerId() == Security.getLoggedUserId()){
                    if(Objects.equals(elem.getTitle(), title)){
                        return true;
                    }
                }
            }
        }
        else{
            // lors de l'edition
            if(Objects.equals(form.getTitle(), title)){
                return false;
            }
            else{
                for( Form elem:  Form.getAll()){
                    if(elem.getOwnerId() == Security.getLoggedUserId()){
                        if(Objects.equals(elem.getTitle(), title) ){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean save(String title, String description, boolean isPublic){
        if (isNew) {
            // si c'est un nouveau formulaire
            if (Security.getLoggedUser() != null) {
                // si il es connecté
                this.form = new Form(title, description, Security.getLoggedUser(), isPublic);
                this.form.save();
                this.form.addAccess(this.form.getOwner(), AccessType.Editor);
                // pour s'assurer que tout a bien été effectué.
                return true;
            } else {
                return false;
            }
        } else {
            if (Security.getLoggedUser() != null) {
                this.form. setTitle(title);
                this.form.setDescription(description);
                if(!this.form.getIsPublic() && isPublic){

                    if(askForConfirmation()){
                        this.form.setIsPublic(isPublic);
                        this.form.deleteAccesses();
                    }
                    else
                        return false;

                }
                else{
                    this.form.setIsPublic(isPublic);
                }
                this.form.save();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean askForConfirmation(){
        var confirmation = new FormEditConfirmationController();
        navigateTo(confirmation);
        return confirmation.answer;
    }
}
