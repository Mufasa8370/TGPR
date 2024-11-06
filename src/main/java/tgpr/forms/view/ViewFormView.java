package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.AnalyzeController;
import tgpr.forms.controller.QuestionController;
import tgpr.forms.controller.ViewFormController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.Controller;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static com.googlecode.lanterna.input.KeyType.*;

public class ViewFormView extends DialogWindow {

    private final ViewFormController controller;
    private final Form form;
    private final Label lblTitle;
    private final Label lblDescription;
    private final Label lblCreatedBy;
    private final Label lblIsPublic;
    private final Panel pnlQuestions;
    private final ObjectTable<Question> questionTable;
    private boolean existInstance;


    public ViewFormView(ViewFormController controller, Form form) {
        super("View Form Details");

        this.controller = controller;
        this.form = form;

        if(form.getInstances().isEmpty()){
            existInstance = false;
        }else{
            existInstance = true;
        }

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(100, 18));

        Panel root = new Panel();
        setComponent(root);

        Panel fields = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        fields.addComponent(new Label("Title:"));
        lblTitle = new Label(form.getTitle()).addTo(fields);

        fields.addComponent(new Label("Description:"));
        lblDescription = new Label(form.getDescription()).addTo(fields);

        fields.addComponent(new Label("Created By:"));
        lblCreatedBy = new Label(String.valueOf(form.getOwner())).addTo(fields);

        fields.addComponent(new Label("Is Public:"));
        lblIsPublic = new Label(String.valueOf(form.getIsPublic())).addTo(fields);

        if(existInstance){
            root.addComponent(new Label("This form is read only because it has already been answered("+form.getInstances().size()+" instance(s)).").setForegroundColor(TextColor.ANSI.BLUE));
        }

        pnlQuestions = new Panel();
        questionTable = new ObjectTable<>(
                new ColumnSpec<>("Index", Question::getIdx ).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Title", Question::getTitle).setWidth(30).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Type", Question::getType).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Required", Question::getRequired).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap),
                new ColumnSpec<>("Option List", Question::getOptionList).setOverflowHandling(ColumnSpec.OverflowHandling.Wrap)
        ).addTo(pnlQuestions);

        List<Question> questions = form.getQuestions();
        questionTable.setSelectAction(this::displayQuestion);

        Label lblNoQuestions = new Label("No question yet").setForegroundColor(TextColor.ANSI.RED);
        lblNoQuestions.setVisible(false);
        lblNoQuestions.addTo(pnlQuestions);

        if (questions == null || questions.isEmpty()) {
            lblNoQuestions.setVisible(true);
        } else {
            lblNoQuestions.setVisible(false);
            questionTable.add(questions);
        }

        pnlQuestions.setPreferredSize(new TerminalSize(500,500));
        root.addComponent(pnlQuestions);

        new EmptySpace().addTo(fields);

        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        if(!existInstance){
            Button btnNewQuestion = new Button("New Question", this::newQuestion).addTo(buttons);
            Button btnEditForm = new Button("Edit Form", this::editForm).addTo(buttons);
        }
        Button btnDeleteForm = new Button("Delete Form", this::deleteForm).addTo(buttons);
        if(existInstance){
            if(form.getIsPublic()){
                Button btnMakePrivate = new Button("Make Private", this::makePublic).addTo(buttons);
            }else{
                Button btnMakePublic = new Button("Make Public", this::makePublic).addTo(buttons);
            }
        }
        Button btnShare = new Button("Share", this::share).addTo(buttons);
        if(existInstance){
            Button btnClearInstances = new Button("Clear Instances",this::clearInstances).addTo(buttons);
        }
        if(!existInstance){
            Button btnReorder = new Button("Reorder", this::reorder).addTo(buttons);
        }
        Button btnAnalyse = new Button("Analyse", this::analyse).addTo(buttons);
        new Button("Close", this::close).addTo(buttons);

        root.addComponent(buttons);

    }

    private void clearInstances() {
        Controller.showMessage("use case en cours de dev", "message", "ok");
    }

    private void makePublic() {
        form.setIsPublic(!form.getIsPublic());
        form.save();
        //temp
        close();
        Controller.navigateTo(new ViewFormController(form));
    }

    private void analyse() {
        //ppas dinstance ? pas danalyse?
        Controller.navigateTo(new AnalyzeController(form));
    }

    private void reorder() {

    }

    private void share() {
        Controller.showMessage("use case en cours de dev","Message", "ok");
    }

    private void deleteForm() {
        controller.delete();
    }

    private void editForm() {
        Controller.showMessage("use case en cours de dev","Message", "ok");
    }

    private void newQuestion() {
        controller.newQuestion();
    }

    private void displayQuestion() {
    }

}
