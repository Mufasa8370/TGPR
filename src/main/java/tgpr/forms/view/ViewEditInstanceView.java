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

    private  Question currentQuestion;

    // Attributs de classe pour les composants
    private Panel root;
    private Panel titlePanel;
    private Panel questionPanel;
    private Panel labelPanel;
    private Panel valuesPanel;
    private Panel buttonsPanel;
    private Panel panelError; // Pour l'erreur
    private RadioBoxList<Object> radioBoxList; // Pour les options radio
    private ComboBox<OptionValue> cbo1; // Pour la combobox
    private Button close;
    private Button previous;
    private Button cancel;

    private TextBox date;
    private TextBox txtShort;
    CheckBoxList<OptionValue> lstOptionsValues;

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
        this.instance =  new Instance(form, getLoggedUser());;
        this.instance.save();
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
        questionPanel = new Panel();
        questionPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        questionPanel.setPreferredSize(new TerminalSize(83, 20));
        currentQuestion = form.getQuestions().get(current - 1);
        questionPanel.addComponent(new EmptySpace());

        // ERROR
        panelError = new Panel();


        // QUESTION
        Panel questionTitlePanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        if (currentQuestion.getRequired()) {
            questionTitlePanel.addComponent(new Label("(*)").setForegroundColor(TextColor.ANSI.RED));
        }
        questionTitlePanel.addComponent(new Label(currentQuestion.getTitle()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        questionPanel.addComponent(questionTitlePanel);

        // REPONSE
        questionPanel.addComponent(new EmptySpace());
        if (currentQuestion.getType().toString().equals("Radio")) {
            this.questionPanel.addComponent(getViewRadio());
        } else if (currentQuestion.getType().toString().equals("Check")) {
            this.questionPanel.addComponent(getViewCheckBox());
        } else if (currentQuestion.getType().toString().equals("Short")) {

           questionPanel.addComponent(getViewShort());
        } else if (currentQuestion.getType().toString().equals("Combo")) {
            this.questionPanel.addComponent(getViewCombo());
            cbo1.takeFocus();
            System.out.println("combo");
        } else if (currentQuestion.getType().toString().equals("Email")) {


            System.out.println("email");
        } else if (currentQuestion.getType().toString().equals("Date")) {
            this.date = new TextBox().setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
            this.questionPanel.addComponent(date);
            date.setTextChangeListener((txt, byUser) -> validateDate());

        } else if (currentQuestion.getType().toString().equals("Long")) {
            System.out.println("long");
        }

        if (currentQuestion.getRequired()) {
            this.questionPanel.addComponent(new EmptySpace());
            panelError.addComponent(new Label("This question is required.").setForegroundColor(TextColor.ANSI.RED));
            this.questionPanel.addComponent(panelError);
        }

        return this.questionPanel.withBorder(Borders.singleLine("Question " + current + " of " + total));
    }

    public Panel createdButtons() {
        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        controller.removePanelError(buttonsPanel);
        buttonsPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        close = new Button("Close").addTo(buttonsPanel).center();
        previous = new Button("Previous", this::previous).addTo(buttonsPanel);
        previous.setVisible(false);
        next = new Button("Next", this::next).addTo(buttonsPanel);
        cancel = new Button("Cancel").addTo(buttonsPanel);
        return buttonsPanel;
    }

    private void previous() {
        current--;
        if (current > 1) {
            previous.setVisible(true);
        }else {
            next.setVisible(false);
        }
        if (current < total) {
            next.setVisible(true);
        }else {
            next.setVisible(false);
        }
        panelForQuestion.removeAllComponents();
        panelForQuestion.addComponent(createCell());
        getFocus();
    }

    private void next() {
        System.out.println("next");
        current++;

        if (current > 1) {
            previous.setVisible(true);
        }else {
            next.setVisible(false);
        }
        if (current < total) {
           next.setVisible(true);
        }else {
            next.setVisible(false);
        }
        panelForQuestion.removeAllComponents();
        panelForQuestion.addComponent(createCell());
        getFocus();


    }


    //View pour type de réponse

    public RadioBoxList<Object> getViewRadio(){

        OptionList optionList = currentQuestion.getOptionList();
        List<OptionValue> optionValues = optionList.getOptionValues();
        radioBoxList = new RadioBoxList<>();
        //radioBoxList.setCheckedItemIndex(1);

        for (OptionValue optionValue : optionValues) {
            radioBoxList.addItem(optionValue);
        }
        if (currentQuestion.getAnswerValue(instance) != null){
            radioBoxList.setCheckedItemIndex(Integer.parseInt(currentQuestion.getAnswerValue(instance)));
        }else {
            System.out.println("existe pas ");
        }
        radioBoxList.takeFocus();
        radioBoxList.addListener(new RadioBoxList.Listener() {
            @Override
            public void onSelectionChanged(int selectedIndex, int previousSelection) {
                controller.removePanelError(panelError);
                Answer answer = new Answer(instance,currentQuestion, String.valueOf(radioBoxList.getCheckedItemIndex()));
                answer.save();

            }
        });

        return radioBoxList;

    }

    public CheckBoxList<OptionValue> getViewCheckBox(){
        lstOptionsValues = new CheckBoxList<>();
        OptionList optionList = currentQuestion.getOptionList();
        List<OptionValue> optionValues = optionList.getOptionValues();
        System.out.println(optionValues.get(2));
        lstOptionsValues.setChecked(optionValues.get(2),false);
        if (currentQuestion.getAnswerValue(instance) != null){

            String answers = currentQuestion.getAnswerValue(instance);
            List<String> answersSep = List.of(answers.split(","));
            for (String sep : answersSep){
                sep = sep.replace("[","").trim();
                sep = sep.replace("]","");

            }

            System.out.println("exit");
        }else {
            System.out.println("existe pas ");
        }


        lstOptionsValues.addListener(new CheckBoxList.Listener() {
            @Override
            public void onStatusChanged(int itemIndex, boolean checked) {
                Answer answer = new Answer(instance, currentQuestion,String.valueOf(lstOptionsValues.getCheckedItems()));
                answer.save();
            }
        });
        for (OptionValue optionValue : optionValues) {
            lstOptionsValues.addItem(optionValue);
        }
        return lstOptionsValues;
    }

    public ComboBox<OptionValue> getViewCombo(){
        cbo1 = new ComboBox<>();
        OptionList optionList = currentQuestion.getOptionList();
        List<OptionValue> optionValues = optionList.getOptionValues();
        OptionValue optionValueTemp = new OptionValue();
        optionValueTemp.setLabel("-- Please choose an option --");
        cbo1.addItem(optionValueTemp);
        for (OptionValue optionValue : optionValues) {
            cbo1.addItem(optionValue);
        }
        if (currentQuestion.getAnswerValue(instance) != null){
            System.out.println(Integer.parseInt(currentQuestion.getAnswerValue(instance)));
            cbo1.setSelectedIndex(Integer.parseInt(currentQuestion.getAnswerValue(instance)));
            controller.removePanelError(panelError);
            System.out.println("exit");
        }else {
            System.out.println("existe pas ");
        }


        cbo1.addListener((newIndex, oldIndex, byUser) -> {
            controller.removePanelError(panelError);
            Answer answer = new Answer(instance,currentQuestion, String.valueOf(newIndex));
            answer.save();
        });
        cbo1.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        return cbo1;
    }

    public TextBox getViewShort(){
        txtShort = new TextBox();
        txtShort.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        if (currentQuestion.getAnswerValue(instance) != null){

            txtShort.setText(currentQuestion.getAnswerValue(instance));
            controller.removePanelError(panelError);
        }else {
            System.out.println("existe pas ");
        }
        txtShort.setTextChangeListener(new TextBox.TextChangeListener() {
            @Override
            public void onTextChanged(String newText, boolean changedByUserInteraction) {
                controller.removePanelError(panelError);
                Answer answer = new Answer(instance,currentQuestion,txtShort.getText());
                answer.save();

            }
        });
        return txtShort;
    }




    public void validateDate(){
        panelError.removeAllComponents();
        String dateCurrent = date.getText();
        String regex = "^([0-2][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
        if(!dateCurrent.matches(regex)){
             panelError.addComponent(new Label("Date invalid format.").setForegroundColor(TextColor.ANSI.RED));
        }
        panelError.addComponent(new Label("")) ;
    }

    public void getFocus(){
        if (currentQuestion.getType().toString().equals("Radio")) {
            radioBoxList.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Check")) {
            lstOptionsValues.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Short")) {
            txtShort.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Combo")) {
            cbo1.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Email")) {




            System.out.println("email");
        } else if (currentQuestion.getType().toString().equals("Date")) {
            date.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Long")) {
            System.out.println("long");
        }

    }


}
