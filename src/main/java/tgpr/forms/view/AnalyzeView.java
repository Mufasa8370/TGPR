package tgpr.forms.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.AnalyzeController;
import tgpr.forms.model.Stat;
import tgpr.forms.model.Form;
import tgpr.forms.model.Question;
import tgpr.framework.ViewManager;
import java.util.List;

public class AnalyzeView extends DialogWindow {
    private final AnalyzeController controller;
    private Form form;
    private final Label lblTitle = new Label("");
    private final Label lblDescription = new Label("");
    private final Label lblNbInstances = new Label("");
    private ObjectTable<Question> questionsTable;
    private ObjectTable<Stat> answersTable;
    private Panel pnlQuestions;
    private Panel pnlAnswers;

    public AnalyzeView(AnalyzeController controller, Form form){
        super("Statistical Analysis of Submitted Instances");
        this.controller = controller;
        this.form = form;

        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(90, 20));

        var root = Panel.verticalPanel();
        setComponent(root);

        createFields().addTo(root);
        createQuestionsAndAnswersPanel().sizeTo(ViewManager.getTerminalColumns(),15).addTo(root);
        createButtonsPanel().addTo(root);

        refresh();
    }

    private Panel createFields(){
        var panel = Panel.gridPanel(2, Margin.of(1));

        new Label("Title:").addTo(panel);
        lblTitle.addTo(panel).addStyle(SGR.BOLD);
        new Label("Description:").addTo(panel);
        lblDescription.addTo(panel).addStyle(SGR.BOLD);
        new Label("Number of Submitted Instances:").addTo(panel);
        lblNbInstances.addTo(panel).addStyle(SGR.BOLD);

        return panel;
    }

    private Panel createQuestionsAndAnswersPanel() {
        var panel = Panel.gridPanel(2, Margin.of(1));

        createQuestionsPanel().addTo(panel);
        createAnswersPanel().addTo(panel);

        return panel;
    }

    private Panel createQuestionsPanel() {
        var panel = pnlQuestions = Panel.gridPanel(1, Margin.of(1));

        questionsTable = new ObjectTable<>(
                new ColumnSpec<Question>("Index", Question::getIdx)
                        .setMinWidth(5).alignRight(),
                new ColumnSpec<Question>("Title", Question::getTitle)
                        .setMinWidth(27).alignLeft()
        ).addTo(panel);

        questionsTable.addSelectionChangeListener(this::onQuestionSelectionChanged);

        return panel;
    }

    private void onQuestionSelectionChanged(int oldValue, int newValue, boolean byUser) {
        Question selectedQuestion = questionsTable.getSelected();
        if (selectedQuestion != null) {
            controller.answersPanel(answersTable, selectedQuestion);
        }
    }

    private Panel createAnswersPanel() {
        var panel = pnlAnswers = Panel.gridPanel(1, Margin.of(1));

        answersTable = new ObjectTable<>(
                new ColumnSpec<Stat>("Value", Stat::getValue )
                        .setMinWidth(24).alignLeft(),
                new ColumnSpec<Stat>("Nb Occ.", Stat::getCount)
                        .setMinWidth(3).alignRight(),
                new ColumnSpec<Stat>("Ratio", Stat::getRatio)
                        .setMinWidth(8).alignRight()
        ).addTo(panel);

        return panel;
    }

    private Panel createButtonsPanel() {
        var panel = new Panel()
                .setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        Button btnClose = new Button("Close", this::close).addTo(panel);

        Button viewInstances = new Button("View Instances").addTo(panel);

        addShortcut(btnClose, KeyStroke.fromString("<A-c>"));
        addShortcut(viewInstances, KeyStroke.fromString("<A-v>"));

        return panel;
    }

    private void refresh() {
        if (form != null) {
            lblTitle.setText(form.getTitle());
            lblDescription.setText(form.getDescription());
            lblNbInstances.setText(String.valueOf(controller.getNbSubmittedInstances()));
            controller.questionsPanel(questionsTable);
            onQuestionSelectionChanged(-1, questionsTable.getSelectedRow(), false);
        }
    }
}