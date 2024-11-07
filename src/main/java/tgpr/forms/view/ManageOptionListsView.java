package tgpr.forms.view;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ManageOptionListsController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;

import java.util.List;

public class ManageOptionListsView extends DialogWindow {
    private final ManageOptionListsController controller;
    private final Panel panelForOptionListManagement = new Panel();
    private List<OptionList> listOfOptionLists;

    private final Panel buttonsPanel;
    private Button close;
    private Button newList;

    private final Panel root;


    public ManageOptionListsView(ManageOptionListsController controller) {
        //Titre de la fenêtre
        super("Option Lists Management");
        this.controller = controller;
        this.listOfOptionLists = OptionList.getAll();
        setHints(List.of(Window.Hint.CENTERED, Window.Hint.FIXED_SIZE));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 20 lignes et 70 colonnes
        setFixedSize(new TerminalSize(70, 20));

        root = new Panel();

        // Tableau de values
        root.addComponent(tableOfOptionLists());
        root.addComponent(new EmptySpace());
        setComponent(root);
        // Boutons
        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        newList = new Button("New List", controller::newList).addTo(buttonsPanel);
        close = new Button("Close", controller::close).addTo(buttonsPanel);

        buttonsPanel.addComponent(newList);
        buttonsPanel.addComponent(close);
        root.addComponent(buttonsPanel);


    }

    private ObjectTable<OptionList> tableOfOptionLists(){
        ObjectTable<OptionList> tbl = new ObjectTable<>(
                new ColumnSpec<>("Name", OptionList::getName),
                new ColumnSpec<>("Values", OptionList::getNumberOfValues),
                new ColumnSpec<>("Owner", OptionList::getOwner)
        );
        tbl.addSelectionChangeListener(new ObjectTable.SelectionChangeListener() {
            @Override
            public void onSelectionChanged(int oldRow, int newRow, boolean byUser) {
                System.out.println("okk");
            }
            //addShortcut(viewInstances, KeyStroke.fromString("<A-v>"));

        });
        //this.listOfOptionLists = OptionList.getAll();
        tbl.add(listOfOptionLists);
        System.out.println(listOfOptionLists.size());
        root.addComponent(tbl);
        return tbl;

    }
}
