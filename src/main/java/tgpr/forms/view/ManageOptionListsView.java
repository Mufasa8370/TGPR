package tgpr.forms.view;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyStroke;
import tgpr.forms.controller.ManageOptionListsController;
import tgpr.forms.model.OptionList;
import tgpr.forms.model.OptionValue;

import java.util.ArrayList;
import java.util.List;

import static tgpr.forms.model.Security.getLoggedUser;

public class ManageOptionListsView extends DialogWindow {
    private final ManageOptionListsController controller;
    private final Panel root;
    private List<OptionList> listOfOptionLists = new ArrayList<>();
    private List<OptionList> listOfOptionListsAll;


    private final Panel buttonsPanel;
    private ObjectTable<OptionList> tbl;
    private Button close;
    private Button newList;



    public ManageOptionListsView(ManageOptionListsController controller) {
        //Titre de la fenêtre
        super("Option Lists Management");
        this.controller = controller;
        this.listOfOptionListsAll = OptionList.getAll();
        if(getLoggedUser().isAdmin()){
            listOfOptionLists = listOfOptionListsAll;

        }else {
            for (OptionList optionList : listOfOptionListsAll){
                if (optionList.getOwner() == null || optionList.getOwner().equals(getLoggedUser())) {
                    listOfOptionLists.add(optionList);
                }
            }
        }
        setHints(List.of(Window.Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);

        root = new Panel();

        // Tableau de values
        root.addComponent(tableOfOptionLists());
        root.addComponent(new EmptySpace());
        setComponent(root);

        // Boutons
        buttonsPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).center();

        newList = new Button("New List", controller::newList).addTo(buttonsPanel);
        close = new Button("Close", controller::close).addTo(buttonsPanel);

        buttonsPanel.addComponent(newList);
        buttonsPanel.addComponent(close);
        root.addComponent(buttonsPanel);

    }

    private Panel tableOfOptionLists(){
        tbl = new ObjectTable<>(
                new ColumnSpec<>("Name", OptionList::getName),
                new ColumnSpec<>("Values", OptionList::getNumberOfValues),
                new ColumnSpec<>("Owner", OptionList::getOwnerForManage)
        );
        tbl.setSelectAction(this::editList);

        tbl.add(listOfOptionLists);
        return Panel.gridPanel(1, Margin.of(1)).addComponent(tbl);
    }

    private void editList() {
        //System.out.println(tbl.getSelected()); // imprime pour vérifier
        controller.editList(tbl.getSelected());
    }

}
