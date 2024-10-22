package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.Panel;
import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.view.ViewEditInstanceView;
import tgpr.framework.Controller;

import static tgpr.forms.model.Security.getLoggedUser;

public class ViewEditInstanceController extends Controller<ViewEditInstanceView> {
    private final ViewEditInstanceView view;
    private Instance instance;


    @Override
    public ViewEditInstanceView getView() {
        return view;
    }

    public ViewEditInstanceController(Instance i, Form form) {
        System.out.println("ok2" + getLoggedUser().getFullName());
        if(i != null){
            this.instance = i;
            if(i.getCompleted() != null){
                System.out.println("ok");

                view = new ViewEditInstanceView(this, i, form);
            }else {
                //Instance non completée
                view = new ViewEditInstanceView(this,form,instance);
                System.out.println("ok1");

            }

        }else {

            //Aucune instance
            view = new ViewEditInstanceView(this,form, new Instance(form, getLoggedUser()));
        }

    }
    public ViewEditInstanceController(Form form, ViewEditInstanceView view) {
        this.view = view;
    }

    //public Question getNextQuestion()

    public void viewSubmission() {
    }

    public void submitAgain() {

    }

    public void cancel() {

        boolean confirmed = askConfirmation("Are you sure you want to delete this instance an its answers?", "Delete Instance ?");
        if (confirmed) {

        } else {
            System.out.println("L'utilisateur a annulé.");
        }
    }
    //Fonction pour remove un panel
    public void removePanelError(Panel pan){
        pan.removeAllComponents();

    }


    public void close() {
        navigateTo(new ViewFormsController());
    }

    public void submit() {
    }
}
