package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.Panel;
import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;
import tgpr.forms.model.Question;
import tgpr.forms.view.SignupView;
import tgpr.forms.view.ViewEditInstanceView;
import tgpr.framework.Controller;

public class ViewEditInstanceController extends Controller<ViewEditInstanceView> {
    private final ViewEditInstanceView view;
    private Instance instance;


    @Override
    public ViewEditInstanceView getView() {
        return view;
    }

    public ViewEditInstanceController(Instance i, Form form) {
        if(i != null){
            this.instance = i;
            if(i.getCompleted() != null){
                view = new ViewEditInstanceView(this, i);
            }else {
                view = new ViewEditInstanceView(this,form);
            }

        }else {
            //Change après
            view = new ViewEditInstanceView(this,form);
        }

    }
    public ViewEditInstanceController(Form form) {
        view = new ViewEditInstanceView(this,form);
    }

    //public Question getNextQuestion()

    public void viewSubmission() {
    }

    public void submitAgain() {

    }

    public void cancel() {
        view.close();
    }
    //Fonction pour remove un panel
    public void removePanelError(Panel pan){
        pan.removeAllComponents();

    }




}
