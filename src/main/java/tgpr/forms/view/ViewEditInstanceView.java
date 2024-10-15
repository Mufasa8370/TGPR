package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.ViewEditInstanceController;
import tgpr.forms.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static tgpr.forms.model.Security.getLoggedUser;

public class ViewEditInstanceView extends DialogWindow {
    private final ViewEditInstanceController controller;
    private Instance instance;
    private Form form;
    private Panel panelForQuestion = new Panel();
    private int current = 1;
    private int total;
    private Button next;

    private List<Question> questions;

    // Attributs de classe pour les composants
    private Panel root;
    private Panel titlePanel;
    private Panel labelPanel;
    private Panel valuesPanel;
    private Panel buttonsPanel;
    private Panel panelError; // Pour l'erreur
    private RadioBoxList<Object> radioBoxList; // Pour les options radio
    private ComboBox<OptionValue> cbo1; // Pour la combobox
    private Button close;
    private Button previous;
    private Button cancel;

    // Si instance existe
    public ViewEditInstanceView(ViewEditInstanceController controller, Instance i) {
        super("Open a form");
        this.controller = controller;
        this.instance = i;
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(48, 6));

        root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);
        root.addComponent(new EmptySpace());

        Label p1 = new Label("You have already answered this form.");
        root.addComponent(p1);
        Label p2 = new Label("You can view your submission or submit again.");
        root.addComponent(p2);
        Label p3 = new Label("What would you like to do?");
        root.addComponent(p3);
        root.addComponent(new EmptySpace());

        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Button viewSubmission = new Button("View Submission", controller::viewSubmission).addTo(buttonsPanel);
        Button submitAgain = new Button("Submit again", controller::submitAgain).addTo(buttonsPanel);
        Button cancel = new Button("Cancel", controller::cancel).addTo(buttonsPanel);
        buttonsPanel.addComponent(viewSubmission);
        buttonsPanel.addComponent(submitAgain);
        buttonsPanel.addComponent(cancel);
        root.addComponent(buttonsPanel);
    }

    // Si instance existe pas
    public ViewEditInstanceView(ViewEditInstanceController controller, Form form) {
        super("Answer the Form");
        this.controller = controller;
        this.form = form;
        instance = new Instance(form, getLoggedUser());
        this.instance = instance;
        questions = form.getQuestions();
        total = questions.size();
        instance.setStarted(LocalDateTime.now());
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(85, 28));

        root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);

        // Panel pour les infos dessus
        titlePanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        root.addComponent(titlePanel);

        labelPanel = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        labelPanel.addComponent(new Label("Title:"));
        labelPanel.addComponent(new Label("Description:"));
        labelPanel.addComponent(new Label("Started on:"));
        titlePanel.addComponent(labelPanel);
        titlePanel.addComponent(new EmptySpace());

        valuesPanel = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        valuesPanel.addComponent(new Label(form.getTitle()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        valuesPanel.addComponent(new Label(form.getDescription()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        valuesPanel.addComponent(new Label(instance.getStarted().format(formatter)).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        titlePanel.addComponent(valuesPanel);

        root.addComponent(titlePanel);
        root.addComponent(new EmptySpace());
        root.addComponent(panelForQuestion);

        // Création réponses
        panelForQuestion.addComponent(createCell());

        // Boutons
        root.addComponent(createdButtons());
    }

    private Border createCell() {
        Panel pan = new Panel();
        pan.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        pan.setPreferredSize(new TerminalSize(83, 20));
        Question question = form.getQuestions().get(current - 1);
        pan.addComponent(new EmptySpace());

        // ERROR
        panelError = new Panel();

        // QUESTION
        Panel questionPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        if (question.getRequired()) {
            questionPanel.addComponent(new Label("(*)").setForegroundColor(TextColor.ANSI.RED));
        }
        questionPanel.addComponent(new Label(question.getTitle()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        pan.addComponent(questionPanel);

        // REPONSE
        pan.addComponent(new EmptySpace());

        if (question.getType().toString().equals("Radio")) {
            System.out.println("radio");
            OptionList optionList = question.getOptionList();
            List<OptionValue> optionValues = optionList.getOptionValues();
            radioBoxList = new RadioBoxList<>();

            for (OptionValue optionValue : optionValues) {
                radioBoxList.addItem(optionValue);
            }

            radioBoxList.takeFocus();
            radioBoxList.addListener(new RadioBoxList.Listener() {
                @Override
                public void onSelectionChanged(int selectedIndex, int previousSelection) {
                    controller.removePanelError(panelError);
                }
            });

            pan.addComponent(radioBoxList);
        } else if (question.getType().toString().equals("Check")) {
            System.out.println("Check");
        } else if (question.getType().toString().equals("Short")) {
            System.out.println("Short");
            TextBox txtShort = new TextBox().addTo(pan);
            txtShort.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
            txtShort.takeFocus();

        } else if (question.getType().toString().equals("Combo")) {
            cbo1 = new ComboBox<>();
            OptionList optionList = question.getOptionList();
            List<OptionValue> optionValues = optionList.getOptionValues();
            OptionValue optionValueTemp = new OptionValue();
            optionValueTemp.setLabel("-- Please choose an option --");
            cbo1.addItem(optionValueTemp);
            for (OptionValue optionValue : optionValues) {
                cbo1.addItem(optionValue);
            }
            cbo1.takeFocus();

            cbo1.addListener((newIndex, oldIndex, byUser) -> {
                controller.removePanelError(panelError);
            });
            cbo1.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

            pan.addComponent(cbo1);

            System.out.println("Combo");
        } else if (question.getType().toString().equals("Email")) {
            System.out.println("email");
        } else if (question.getType().toString().equals("Date")) {
            System.out.println("date");
        } else if (question.getType().toString().equals("Long")) {
            System.out.println("long");
        }

        if (question.getRequired()) {
            pan.addComponent(new EmptySpace());
            panelError.addComponent(new Label("This question is required.").setForegroundColor(TextColor.ANSI.RED));
            pan.addComponent(panelError);
        }

        return pan.withBorder(Borders.singleLine("Question " + current + " of " + total));
    }

    public Panel createdButtons() {
        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        controller.removePanelError(buttonsPanel);
        buttonsPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        close = new Button("Close").addTo(buttonsPanel).center();
        if (current > 1) {
            previous = new Button("Previous", this::previous).addTo(buttonsPanel);
        }
        if (current < total) {
            next = new Button("Next", this::next).addTo(buttonsPanel);
        }

        cancel = new Button("Cancel").addTo(buttonsPanel);
        return buttonsPanel;
    }

    private void previous() {
        current--;
        panelForQuestion.removeAllComponents();
        panelForQuestion.addComponent(createCell());
        panelForQuestion.invalidate();
    }

    private void next() {
        System.out.println("next");
        current++;
        panelForQuestion.removeAllComponents();
        panelForQuestion.addComponent(createCell());
        panelForQuestion.invalidate();
    }
}
