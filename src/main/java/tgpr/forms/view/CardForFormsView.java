package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.forms.controller.CardForFormsController;
import tgpr.forms.model.Form;

import static tgpr.forms.controller.CardForFormsController.cutText;

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
        pan.addComponent(new Label(form.getTitle()).setForegroundColor(TextColor.ANSI.BLUE).center());
        String description = form.getDescription() != null ? form.getDescription() : "No description";
        Label descriptionLabel = new Label(cutText(description, 32));
        descriptionLabel.center().addStyle(SGR.ITALIC).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        pan.addComponent((descriptionLabel));
        pan.addComponent(new EmptySpace());
        pan.addComponent(new Label("Creat by " +form.getOwner().getFullName()).center());
        //String startDate =
        //Derniere soumission
        //Bouton open
        // bouton Manage
        pan.setPreferredSize(new TerminalSize(35,10));

        return pan
                .withBorder(Borders.singleLine());
    }

}
