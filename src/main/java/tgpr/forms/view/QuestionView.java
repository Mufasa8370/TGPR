package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.QuestionController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuestionView extends DialogWindow {

    private final QuestionController controller;
    private final TextBox txtTitle;
    private final TextBox txtDescription;
    private final ComboBox<Question.Type> cboType;
    private final CheckBox chkRequired;
    private final ComboBox<OptionList> cboOptionList;
    private final Button btnAdd;
    private final Button btnCreate;
    private final Label errTitle;
    private final Label errDescription;

    private final Question question;

    public QuestionView(QuestionController controller, Question question) {
        super("Add a Question");

        this.controller = controller;
        this.question = question;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(70, 14));

        Panel root = new Panel();
        setComponent(root);

        Panel fields = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        new Label("Title:").addTo(fields);
        txtTitle = new TextBox(new TerminalSize(40,1))
                .addTo(fields).takeFocus();
        new EmptySpace().addTo(fields);
        errTitle = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
        txtTitle.setTextChangeListener((newText, changedText) -> validate());


        new Label("Description:").addTo(fields);
        txtDescription = new TextBox(new TerminalSize(50, 3), "", TextBox.Style.MULTI_LINE).addTo(fields);
        new EmptySpace().addTo(fields);
        errDescription = new Label("").setForegroundColor(TextColor.ANSI.RED).addTo(fields);
        txtDescription.setTextChangeListener((newText, changedText) -> validate());

        new Label("Type:").addTo(fields);
        List<Question.Type> sortedTypes = new ArrayList<>(List.of(Question.Type.values()));
        sortedTypes.sort(Comparator.comparing(Enum::name));
        cboType = new ComboBox<>(sortedTypes).addTo(fields);


        new EmptySpace().addTo(fields);
        new EmptySpace().addTo(fields);

        new Label("Required:").addTo(fields);
        chkRequired = new CheckBox().addTo(fields);

        new EmptySpace().addTo(fields);
        new EmptySpace().addTo(fields);

        new Label("Options List:").addTo(fields);
        cboOptionList = new ComboBox<OptionList>().addTo(fields);

        btnAdd = new Button("Add", this::add).addTo(fields);

        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        btnCreate = new Button("Create", this::create);
        btnCreate.setEnabled(false);
        btnCreate.addTo(buttons);
        new Button("Cancel", this::close).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
    }

    private void validate(){
        var errors = controller.validate(
                txtTitle.getText(),
                txtDescription.getText()
        );

        errTitle.setText(errors.getFirstErrorMessage(Question.Fields.Title));
        errDescription.setText(errors.getFirstErrorMessage(Question.Fields.Description));

        btnCreate.setEnabled(errors.isEmpty());
        //ajout option list
    }

    private void create() {
        controller.create(
                txtTitle.getText(),
                txtDescription.getText(),
                cboType.getSelectedItem(),
                chkRequired.isChecked(),
                cboOptionList.getSelectedItem()
        );
    }

    private void add() {
        Controller.showMessage("use case en cours de dev", "message", "ok");
    }


}


