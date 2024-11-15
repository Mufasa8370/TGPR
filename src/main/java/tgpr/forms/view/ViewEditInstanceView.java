package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.ViewEditInstanceController;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static tgpr.framework.Controller.navigateTo;

public class ViewEditInstanceView extends DialogWindow {
    private final ViewEditInstanceController controller;
    private final Instance instance;
    private Form form;
    private final Panel panelForQuestion = new Panel();
    private int current = 1;
    private int total;
    private Button next;

    private List<Question> questions;

    private  Question currentQuestion;

    // Attributs de classe pour les composants
    private final Panel root;
    private Panel titlePanel;
    private Panel questionPanel;
    private Panel labelPanel;
    private Panel valuesPanel;
    private Panel buttonsPanel;
    private Panel panelError; // Pour l'erreur
    private RadioBoxList<Object> radioBoxList; // Pour les options radio
    private ComboBox<OptionValue> cbo1; // Pour la combobox
    private CheckBoxList<OptionValue> check; // Pour la combobox
    private Button close;
    private Button previous;
    private Button cancel;
    private Button submission;

    private TextBox date;
    private TextBox txtShort;
    private TextBox email;
    CheckBoxList<OptionValue> lstOptionsValues;
    private TextBox longTextBox;

    private List<Integer> indexed = new ArrayList<>();


    // Si instance existe
    public ViewEditInstanceView(ViewEditInstanceController controller, Instance i,Form form) {
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
        Button cancel = new Button("Cancel", controller::close).addTo(buttonsPanel);
        buttonsPanel.addComponent(viewSubmission);
        buttonsPanel.addComponent(submitAgain);
        buttonsPanel.addComponent(cancel);
        root.addComponent(buttonsPanel);
        viewSubmission.takeFocus();
    }


    //Voir instance
    public ViewEditInstanceView(ViewEditInstanceController controller, Form form, Instance instance){
        super("View Answers");
        this.controller = controller;
        this.form = form;
        this.instance =  instance;
        this.instance.save();
        questions = form.getQuestions();
        total = questions.size();
        if (instance.getStarted() == null){
            instance.setStarted(LocalDateTime.now());
        }
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(false);
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
        labelPanel.addComponent(new Label("Submitted on:"));
        labelPanel.addComponent(new Label("Submitted by:"));
        titlePanel.addComponent(labelPanel);
        titlePanel.addComponent(new EmptySpace());

        valuesPanel = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        valuesPanel.addComponent(new Label(form.getTitle()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        valuesPanel.addComponent(new Label(form.getDescription()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        valuesPanel.addComponent(new Label(instance.getStarted().format(formatter)).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        valuesPanel.addComponent(new Label(instance.getCompleted().format(formatter)).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        valuesPanel.addComponent(new Label(instance.getUser().getName()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        titlePanel.addComponent(valuesPanel);

        root.addComponent(titlePanel);
        root.addComponent(new EmptySpace());
        root.addComponent(panelForQuestion);


        // Création réponses
        panelForQuestion.addComponent(createCellForCompleted());


        // Boutons
        root.addComponent(createdButtonsForCompleted());


    }
    // Si instance existe pas
    public ViewEditInstanceView(ViewEditInstanceController controller, Form form, Instance instance, int current) {
        super("Answer the Form");
        this.controller = controller;
        this.form = form;
        this.current = current;
        System.out.println(instance.getId());
        this.instance =  instance;
        this.instance.save();
        questions = form.getQuestions();
        total = questions.size();
        if (instance.getStarted() == null){
            instance.setStarted(LocalDateTime.now());
        }
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        setCloseWindowWithEscape(false);
        setFixedSize(new TerminalSize(85, 28));

        root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);
        addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    if(Security.isGuest()){
                        controller.cancel(instance);
                    }else{
                        navigateTo(new ViewFormsController());
                    }

                }
            }
        });

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
        panelError = new Panel();


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
            System.out.println("Short");
           questionPanel.addComponent(getViewShort());
        } else if (currentQuestion.getType().toString().equals("Combo")) {
            this.questionPanel.addComponent(getViewCombo());
            System.out.println("combo");
        } else if (currentQuestion.getType().toString().equals("Email")) {
            questionPanel.addComponent(getViewEmail());
        } else if (currentQuestion.getType().toString().equals("Date")) {
            questionPanel.addComponent(getViewDate());
        } else if (currentQuestion.getType().toString().equals("Long")) {
            questionPanel.addComponent(getViewLong());
            System.out.println("long");
        }

        questionPanel.addComponent(new EmptySpace());
        questionPanel.addComponent(panelError);
        return this.questionPanel.withBorder(Borders.singleLine("Question " + current + " of " + total));
    }

    private Border createCellForCompleted() {
        questionPanel = new Panel();
        questionPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        questionPanel.setPreferredSize(new TerminalSize(83, 20));
        currentQuestion = form.getQuestions().get(current - 1);
        questionPanel.addComponent(new EmptySpace());

        // QUESTION
        Panel questionTitlePanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        if (currentQuestion.getRequired()) {
            questionTitlePanel.addComponent(new Label("(*)").setForegroundColor(TextColor.ANSI.RED));
        }
        questionTitlePanel.addComponent(new Label(currentQuestion.getTitle()).setForegroundColor(TextColor.ANSI.BLACK_BRIGHT));
        questionPanel.addComponent(questionTitlePanel);

        // REPONSE
        questionPanel.addComponent(new EmptySpace());

        //Reponse String
        String reponse = currentQuestion.getAnswerValue(instance);
        if (reponse != null){
            if (currentQuestion.getType().toString().equals("Check")){
                List<OptionValue> optionValues = currentQuestion.getAnswerValues(instance);
                for (OptionValue op : optionValues){
                    questionPanel.addComponent(new Label(op.getLabel()));
                }
            }else if (currentQuestion.getType().toString().equals("Combo")){
                OptionValue o = OptionValue.getByKey(Integer.parseInt(reponse),currentQuestion.getOptionList().getId());
                questionPanel.addComponent(new Label(o.getLabel()));
            }else if (currentQuestion.getType().toString().equals("Radio")){
                OptionValue o = OptionValue.getByKey(Integer.parseInt(reponse),currentQuestion.getOptionList().getId());
                questionPanel.addComponent(new Label(o.getLabel()));
            }



            else{
                questionPanel.addComponent(new Label(reponse));
            }

        }




        questionPanel.addComponent(new EmptySpace());
        return this.questionPanel.withBorder(Borders.singleLine("Question " + current + " of " + total));
    }

    public Panel createdButtons() {
        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        controller.removePanelError(buttonsPanel);
        buttonsPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        close = new Button("Close", controller::close).addTo(buttonsPanel).center();
        previous = new Button("Previous", this::previous).addTo(buttonsPanel);
        previous.setVisible(false);
        next = new Button("Next", this::next).addTo(buttonsPanel);
        submission = new Button("Submit", () -> controller.submit(instance)).addTo(buttonsPanel);
        submission.setVisible(false);
        cancel = new Button("Cancel", () -> controller.cancel(instance)).addTo(buttonsPanel);

        return buttonsPanel;
    }
    public Panel createdButtonsForCompleted() {
        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        controller.removePanelError(buttonsPanel);
        buttonsPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        close = new Button("Close", controller::close).addTo(buttonsPanel).center();
        previous = new Button("Previous", this::previous).addTo(buttonsPanel);
        previous.setVisible(false);
        next = new Button("Next", this::next).addTo(buttonsPanel);

        return buttonsPanel;
    }

    private void previous() {
        current--;
        visibleOfHiddenButton();
    }

    private void next() {
        System.out.println("next");
        current++;
        visibleOfHiddenButton();


    }

    private void visibleOfHiddenButton() {
        previous.setVisible(current > 1);
        next.setVisible(current < total);
        if(submission != null){
            submission.setVisible(current == total);
            panelForQuestion.removeAllComponents();
            removeError();
            panelForQuestion.addComponent(createCell());
            getFocus();
        }else {
            panelForQuestion.removeAllComponents();
            panelForQuestion.addComponent(createCellForCompleted());
            if (next.isVisible()){
                next.takeFocus();
            }else{
                previous.takeFocus();
            }
        }




    }


    //View pour type de réponse

    public Panel getViewRadio(){
        Panel panelRadio= new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        OptionList optionList = currentQuestion.getOptionList();
        List<OptionValue> optionValues = optionList.getOptionValues();
        radioBoxList = new RadioBoxList<>();

        for (OptionValue optionValue : optionValues) {
            radioBoxList.addItem(optionValue);
        }

        radioBoxList.takeFocus();
        radioBoxList.addListener((selectedIndex, previousSelection) -> {
            if(radioBoxList.getCheckedItem() != null){
                Answer answer = new Answer(instance,currentQuestion, String.valueOf(radioBoxList.getCheckedItemIndex()+1));
                answer.save();
                removeError();

            }else {
                if (currentQuestion.getRequired()){
                    getRequired();
                }
                Answer answer = instance.getAnswer(currentQuestion);
                answer.delete();
            }


        });
        panelRadio.addComponent(radioBoxList);
        if (currentQuestion.getAnswerValue(instance) != null){
            radioBoxList.setCheckedItemIndex(Integer.parseInt(currentQuestion.getAnswerValue(instance)));
        }else {
            if(currentQuestion.getRequired()){
                getRequired();

            }
        }

        return panelRadio;

    }

    public CheckBoxList<OptionValue> getViewCheckBox(){
        lstOptionsValues = new CheckBoxList<>();
        OptionList optionList = currentQuestion.getOptionList();
        List<OptionValue> optionValues = optionList.getOptionValues();



        lstOptionsValues.addListener((itemIndex, checked) -> {
            if( !lstOptionsValues.getCheckedItems().isEmpty() && checked){
                removeError();
                indexed.add(itemIndex + 1);
                for (Integer i : indexed){
                    System.out.println(i);
                }
                currentQuestion.saveAnswer(instance,indexed);
            }else {


                indexed.clear();
                int indexTemp = 1;
                for (OptionValue optionValue : optionValues){
                    if(lstOptionsValues.isChecked(optionValue)){
                        indexed.add(indexTemp);
                    }
                    indexTemp++;
                }
                for (Integer i : indexed){
                    System.out.println(i);
                }
                currentQuestion.saveAnswer(instance,indexed);


            }
            if (currentQuestion.getRequired() && indexed.isEmpty()){
                getRequired();
            }

        });

        for (OptionValue optionValue : optionValues) {
            lstOptionsValues.addItem(optionValue);
        }
        if (currentQuestion.getAnswerValue(instance) != null){
            List<OptionValue> optionValuesChecked = currentQuestion.getAnswerValues(instance);
            for (OptionValue optionValue : optionValuesChecked) {
                lstOptionsValues.setChecked(optionValue, true);
            }
        }else {
            if (currentQuestion.getRequired()){
                getRequired();
            }
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
            cbo1.setSelectedIndex(Integer.parseInt(currentQuestion.getAnswerValue(instance)));

        }else {
            if (currentQuestion.getRequired()){
                getRequired();
            }
        }


        cbo1.addListener((newIndex, oldIndex, byUser) -> {
            if(cbo1.getSelectedItem() != null && cbo1.getSelectedIndex() != 0){
                removeError();
                Answer answer = new Answer(instance,currentQuestion, String.valueOf(newIndex + 1));
                answer.save();
            }else {
                Answer answer = instance.getAnswer(currentQuestion);
                answer.delete();
                if (currentQuestion.getRequired()){
                    getRequired();
                }
            }

        });
        cbo1.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        return cbo1;
    }

    public TextBox getViewShort(){
        txtShort = new TextBox();
        txtShort.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        if (currentQuestion.getAnswerValue(instance) != null){
            txtShort.setText(currentQuestion.getAnswerValue(instance));
            removeError();
        }else {
            if (currentQuestion.getRequired()){
                getRequired();
            }
        }
        txtShort.setTextChangeListener(new TextBox.TextChangeListener() {
            @Override
            public void onTextChanged(String newText, boolean changedByUserInteraction) {
                if(!txtShort.getText().isEmpty()){
                    removeError();
                    Answer answer = new Answer(instance,currentQuestion,txtShort.getText());
                    answer.save();
                }else {
                    Answer answer = instance.getAnswer(currentQuestion);
                    answer.delete();
                    if (currentQuestion.getRequired())
                        getRequired();
                }


            }
        });
        return txtShort;
    }

    public TextBox getViewDate(){
        date = new TextBox().setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        if (currentQuestion.getAnswerValue(instance) != null){
            date.setText(currentQuestion.getAnswerValue(instance));
            removeError();
            if(!controller.validateDate(date.getText())){
                getErrorValidateDate();
            }
        }else {
            if (currentQuestion.getRequired()){
                getRequired();
            }
        }
        date.setTextChangeListener((txt, byUser) -> {
            if (!date.getText().isEmpty()){
                Answer answer = new Answer(instance,currentQuestion,date.getText());
                answer.save();
                removeError();
                if(!controller.validateDate(date.getText())){
                    getErrorValidateDate();
                }
            }else {
                removeError();
                Answer answer = instance.getAnswer(currentQuestion);
                answer.delete();
                if (currentQuestion.getRequired()){

                    getRequired();
                }

            }
        });
        return date;
    }


    public TextBox getViewEmail(){
        email  = new TextBox().setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        if (currentQuestion.getAnswerValue(instance) != null){
            email.setText(currentQuestion.getAnswerValue(instance));
            if(!controller.validateEmail(email.getText())){
                getErrorValidateEmail();
            }
        }else {
           if(currentQuestion.getRequired()){
               getRequired();
           }
        }
        email.setTextChangeListener((txt, byUser) -> {
            if (!email.getText().isEmpty()){
                Answer answer = new Answer(instance,currentQuestion,email.getText());
                answer.save();
                removeError();
                if(!controller.validateEmail(email.getText())){
                    getErrorValidateEmail();
                }
            }else {
                Answer answer = instance.getAnswer(currentQuestion);
                answer.delete();
                removeError();
                if (currentQuestion.getRequired()){

                    getRequired();
                }

            }


        });
        return email;
    }

    public TextBox getViewLong(){
        longTextBox  = new TextBox(new TerminalSize(40, 10)).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        if (currentQuestion.getAnswerValue(instance) != null){
            longTextBox.setText(currentQuestion.getAnswerValue(instance));
        }else {
            if (currentQuestion.getRequired()){
                getRequired();
            }
        }

        longTextBox.setTextChangeListener((newText, changedByUserInteraction) -> {
            if (!longTextBox.getText().isEmpty()){
                Answer answer = new Answer(instance,currentQuestion,longTextBox.getText());
                answer.save();
                removeError();

            }else {
                removeError();
                Answer answer = instance.getAnswer(currentQuestion);
                answer.delete();
                if (currentQuestion.getRequired()){

                    getRequired();
                }

            }
        });
        return longTextBox;
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

            email.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Date")) {
            date.takeFocus();
        } else if (currentQuestion.getType().toString().equals("Long")) {
            longTextBox.takeFocus();
        }

    }




    public void getRequired(){
       panelError.addComponent(new Label("This question is required.").setForegroundColor(TextColor.ANSI.RED));
    }
    public void getErrorValidateDate(){
        panelError.addComponent(new Label("Date format invalid.").setForegroundColor(TextColor.ANSI.RED));
    }
    public void getErrorValidateLong(){
        panelError.addComponent(new Label("Long format invalid.").setForegroundColor(TextColor.ANSI.RED));
    }
    public void getErrorValidateEmail(){
        panelError.addComponent(new Label("Email format invalid.").setForegroundColor(TextColor.ANSI.RED));
    }
    public void removeError(){
        panelError.removeAllComponents();
    }


}
