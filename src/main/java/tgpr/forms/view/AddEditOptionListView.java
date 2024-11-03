package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.forms.controller.AddEditOptionListController;
import tgpr.forms.model.OptionList;

import java.util.List;


public class AddEditOptionListView extends DialogWindow {
    private final AddEditOptionListController controller;
    private final Panel panelForOptionListUpdate = new Panel();
    private OptionList optionList;

    private Button reorder;
    private Button delete;
    private Button save;
    private Button close;
    private Button add;


    // Attributs de classe pour les composants
    private final Panel root;
    private Panel titlePanel;

    private final TextBox txtName;
    private final TextBox txtNewValue;

    private Panel buttonsPanel;



    public AddEditOptionListView(AddEditOptionListController controller, OptionList optionList) {
        //Titre de la fenêtre
        super((optionList == null ? "Create " : "Update ") + "Option List");
        this.controller = controller;
        this.optionList = optionList;
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 20 lignes et 70 colonnes
        setFixedSize(new TerminalSize(70, 20));

        root = new Panel();

        //new Label("Name:").addTo(root);
        Label nameLabel = new Label("Name:");
        root.addComponent(nameLabel);

        if (optionList != null) {
            txtName = new TextBox(optionList.getName());
        }
        else {
            txtName = new TextBox();
        }
        root.addComponent(new EmptySpace());

        // tableau de values

        txtNewValue = new TextBox();
        add = new Button("Add", controller::add).addTo(buttonsPanel);


        root.addComponent(new EmptySpace());

        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        reorder = new Button("Reorder", controller::reorder).addTo(buttonsPanel);
        delete = new Button("Delete", controller::delete).addTo(buttonsPanel);
        save = new Button("Save", controller::save).addTo(buttonsPanel);
        close = new Button("Close", controller::close).addTo(buttonsPanel);

        buttonsPanel.addComponent(reorder);
        buttonsPanel.addComponent(delete);
        buttonsPanel.addComponent(save);
        buttonsPanel.addComponent(close);






    }


}
