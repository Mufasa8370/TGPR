package tgpr.forms.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.CardForFormsController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Instance;

public class CardForFormsView extends Panel{
    private final Form form;
    //private final Instance instance;
    private final CardForFormsController controller;

    public CardForFormsView(Form form, CardForFormsController controller){
        this.form = form;
        this.controller = controller;

        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        addComponent(new Label(form.getTitle()));
        String description = form.getDescription() != null ? form.getDescription() : "No description";
        addComponent(new Label(description));
        addComponent(new Label("Creat by " +form.getOwner().getFullName()));
        //String startDate =
        //Derniere soumission
        //Bouton open
        // bouton Manage




    }
}
