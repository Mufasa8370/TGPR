package tgpr.forms.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.EditFormController;
import tgpr.forms.controller.FormEditConfirmationController;
import tgpr.forms.model.Form;
import tgpr.framework.Controller;

import java.util.List;

public class FormEditConfirmationView extends DialogWindow {
    private final FormEditConfirmationController controller;
    public FormEditConfirmationView(FormEditConfirmationController controller) {
        super("Confirmation");
        this.controller = controller;


        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(54, 4));

        Panel root = new Panel();
        new Label("Are you sure you want to make this form public ?").addTo(root);
        new Label("This will delete all existing shares.").addTo(root);
        new EmptySpace().addTo(root);
        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        new Button("Yes",()->{
            controller.answer = true;
            this.close();
        }).addTo(buttons);
         new Button("No", this::close).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        setComponent(root);

    }
}
