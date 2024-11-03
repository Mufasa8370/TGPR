package tgpr.forms.view;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.EditFormController;
import tgpr.forms.model.Form;
import tgpr.framework.Controller;

import java.util.List;
import java.util.regex.Pattern;
public class EditFormView  extends DialogWindow {
    private final Form form;
    private final Controller controller;
    private final TextBox txtTitle;
    private final Label errTitle;
    private final TextBox txtDescription;
    private final CheckBox isPublicBox;

    private final Button btnCreate;
    private final Button btnCancel;
    public EditFormView(EditFormController controller, Form form) {

        super(form == null ? "Add a form": "Edit a form");

        this.form = form;
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 11));

        Panel root = new Panel();
        root.setLayoutManager(new GridLayout(2).setTopMarginSize(1));

        new Label("Title:").addTo(root);

        txtTitle = new TextBox(new TerminalSize(11, 1)).addTo(root).takeFocus()
                .setTextChangeListener((txt, byUser) -> validate());
        new EmptySpace().addTo(root);
        errTitle = new Label("").addTo(root)
                .setForegroundColor(TextColor.ANSI.RED);

        new Label("Description:").addTo(root);
        txtDescription = new TextBox(new TerminalSize(55, 4), "", TextBox.Style.MULTI_LINE)
                .addTo(root);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);
        new Label("Public:").addTo(root);
        isPublicBox = new CheckBox().addTo(root);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);

        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        new EmptySpace(new TerminalSize(50,1)).addTo(buttons);
        btnCreate = new Button(form != null ? "Update" : "Create").addTo(buttons);
        btnCancel = new Button("Cancel").addTo(buttons);

        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));

        setComponent(root);
    }





    public void validate() {

    }
}
