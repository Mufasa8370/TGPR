package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.CardForFormsController;
import tgpr.forms.controller.LoginController;
import tgpr.forms.model.Form;

import static tgpr.forms.controller.CardForFormsController.*;
import static tgpr.forms.model.Security.getLoggedUser;

public class CardForFormsView extends Panel{
    private final Form form;
    //private final Instance instance;
    private final CardForFormsController controller ;

    public CardForFormsView(Form form, CardForFormsController controller) {
        this.form = form;
        this.controller = controller;
        var root = new Panel().setLayoutManager(
                new LinearLayout(Direction.VERTICAL)
        );
        createCell().addTo(root);
        addComponent(root);
        setPreferredSize(new TerminalSize(35, 10));
    }
    private Border createCell() {
        Panel pan = new Panel();
        pan.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        pan.addComponent(new Label(cutText(form.getTitle(),32)).setForegroundColor(TextColor.ANSI.BLUE).center());
        String description = form.getDescription() != null ? cutText(form.getDescription(),32) : "No description";
        Label descriptionLabel = new Label(cutText(description, 32));
        descriptionLabel.center().addStyle(SGR.ITALIC).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        pan.addComponent((descriptionLabel));
        pan.addComponent(new EmptySpace());
        pan.addComponent(new Label(cutText("Creat by " +form.getOwner().getFullName(),32)).center());
        pan.addComponent(new Label(cutText(getLastInstance(getLoggedUser(),form)[0],32)).center());
        pan.addComponent(new Label(cutText(getLastInstance(getLoggedUser(),form)[1],32)).center());
        pan.addComponent(new EmptySpace());
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).center();
        //ICI POUR view_edit_instance!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if(questionInForm(form)){
            Button open = new Button("Open", controller::open).addTo(buttons);
        }
        //ICI POUR view_form !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if(accesTypeEditor(form)){
            Button manage = new Button("Manage", () -> {
                controller.manage(form);
            }
                    ).addTo(buttons);
        }



        pan.addComponent(buttons);
        //Bouton open
        // bouton Manage
        pan.setPreferredSize(new TerminalSize(35,10));

        return pan
                .withBorder(Borders.singleLine());
    }



}
