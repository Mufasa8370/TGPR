package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.SignupController;
import tgpr.forms.controller.ViewEditInstanceController;
import tgpr.forms.model.Instance;

import java.util.List;

public class ViewEditInstanceView extends DialogWindow {
    private final ViewEditInstanceController controller;

    //Si instance existe
    public ViewEditInstanceView(ViewEditInstanceController controller, Instance i){
        super("Open a form");
        this.controller = controller;
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(48, 6));

        var root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);

        root.addComponent(new EmptySpace());

        Label p1 = new Label("You have already answered this form.");
        root.addComponent(p1);
        Label p2 = new Label("You can view your submission or submit again.");
        root.addComponent(p2);
        Label p3 = new Label("What would you like to do?");
        root.addComponent(p3);
        root.addComponent(new EmptySpace());
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Button viewSubmission = new Button("View Submission", controller::viewSubmission).addTo(buttons);
        Button submitAgain = new Button("Submit again", controller::submitAgain).addTo(buttons);
        Button cancel = new Button("Cancel", controller::cancel).addTo(buttons);
        buttons.addComponent(viewSubmission);
        buttons.addComponent(submitAgain);
        buttons.addComponent(cancel);
        root.addComponent(buttons);

    }

    //Si existe et veux la voir
    public ViewEditInstanceView(ViewEditInstanceController controller, Instance i, boolean view){
        super("View Answers");
        this.controller = controller;


        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(54, 11));

        var root = Panel.verticalPanel();
        setComponent(root);

    }

    //Si instance existe pas
    public ViewEditInstanceView(ViewEditInstanceController controller){
        super("Answer the Form");

        this.controller = controller;
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(54, 11));
        var root = Panel.verticalPanel();
        setComponent(root);

    }
}


