package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.EditFormController;
import tgpr.forms.controller.FormEditConfirmationController;
import tgpr.forms.model.Form;

import java.util.List;

import static tgpr.framework.Controller.navigateTo;
import static tgpr.framework.Tools.asString;
import static tgpr.framework.Tools.ifNull;

public class EditFormView extends DialogWindow {
    private final Form form;
    private final EditFormController controller;
    private final TextBox txtTitle;
    private final Label errTitle;
    private final TextBox txtDescription;
    private final CheckBox isPublicBox;

    private final Button btnCreate;
    private final Button btnCancel;

    public EditFormView(EditFormController controller, Form form) {

        super(form == null ? "Add a form" : "Edit a form");

        this.form = form;
        this.controller = controller;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 11));

        Panel root = new Panel();
        root.setLayoutManager(new GridLayout(2).setTopMarginSize(1));

        new Label("Title:").addTo(root);
        txtTitle = new TextBox(new TerminalSize(11, 1)).addTo(root).takeFocus()
                .setTextChangeListener((txt, byUser) -> isValid());
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
        new EmptySpace(new TerminalSize(37, 1)).addTo(buttons);
        btnCreate = new Button(form != null ? "Update" : "Create", this::save).addTo(buttons);
        btnCancel = new Button("Cancel", this::close).addTo(buttons);

        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        setComponent(root);


        if (form != null) {
            txtTitle.setText(form.getTitle());
            txtDescription.setText(ifNull(form.getDescription(), ""));
            isPublicBox.setChecked(form.getIsPublic());
        }
    }



    public void save(){
        if (isValid()) {
            if (this.controller.save(txtTitle.getText(), txtDescription.getText(), isPublicBox.isChecked())) {
                this.close();
            }
        }
    }

    public boolean isValid() {

        if (!controller.isValidTitle(
                txtTitle.getText()
        )) {
            errTitle.setText("title required and minimum 3 characters.");
            return false;
        }
        else if(controller.titleUsed(txtTitle.getText())){
            errTitle.setText("title already used.");
            return false;
        }
        else {
            errTitle.setText("");
            return true;
        }
    }



}
