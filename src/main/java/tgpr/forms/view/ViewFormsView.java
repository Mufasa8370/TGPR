package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import tgpr.forms.controller.CardForFormsController;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.Form;
import tgpr.forms.model.Security;
import tgpr.forms.model.User;

import java.util.List;

import static tgpr.forms.model.Form.countForUser;
import static tgpr.forms.model.Security.getLoggedUser;

public class ViewFormsView extends BasicWindow {

    private final ViewFormsController controller;
    Panel root = new Panel();
    private final TextBox txtFilter;
    private final Paginator paginator;
    private final Panel cardPanel = new Panel();
    private int currentPage = 1;
    User user = getLoggedUser();

    public ViewFormsView(ViewFormsController controller) {
        this.controller = controller;
        //Title
        if (!user.isGuest()) {
            setTitle("MyFomrs (" + user.getFullName() + " - " + user.getRole() + ")");
        } else {
            setTitle("MyFomrs (" + user.getRole() + ")");
        }
        setHints(List.of(Hint.EXPANDED));
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);

        //Menu
        MenuBar menuBar = new MenuBar().addTo(root);
        Menu menuFile = new Menu("File");
        menuBar.add(menuFile);
        if(!getLoggedUser().isGuest()){
            MenuItem menuViewProfile = new MenuItem("View Profile", controller::menuViewProfile);
            menuFile.add(menuViewProfile);
        }
        MenuItem menuLogout = new MenuItem("Logout", controller::logout);
        menuFile.add(menuLogout);
        MenuItem menuExit = new MenuItem("Exit", controller::exit);
        menuFile.add(menuExit);
        if (!getLoggedUser().isGuest()) {
            Menu menuParameters = new Menu("Parameters");
            menuBar.add(menuParameters);
            MenuItem menuManageOptionList = new MenuItem("Manage your Option Lists", controller::manageYourOptionList);
            menuParameters.add(menuManageOptionList);
            MenuItem menuManageDistributionList = new MenuItem("Manage your Distribution Lists", controller::manageYourDistributionList);
            menuParameters.add(menuManageDistributionList);
        }


        // new EmptySpace().addTo(root);

        //filtre
        Panel filter = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        filter.addComponent(new Label("Filter:"));
        txtFilter = new TextBox().setPreferredSize(new TerminalSize(30, 1));
        ;
        txtFilter.setTextChangeListener((txt, byUser) -> reloadData(currentPage));
        filter.addComponent(txtFilter);
        root.addComponent(filter);
        //Paginator
        paginator = new Paginator(
                this,
                9,
                this::reloadData
        );
        paginator.setCount(countForUser(user, txtFilter.getText()));

        //Donner une taille max pour qu'il prenne la place :)
        cardPanel.setPreferredSize(new TerminalSize(1000, 1000));
        root.addComponent(cardPanel).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        //Ajout du footer (d'un contenant footer)
        Panel footer = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).addTo(root);
        footer.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        if (!getLoggedUser().isGuest()) {
            Button buttonCreateForm = new Button("Create a new form", () -> {
                controller.createNewForm();
                this.reloadData(0);
            }).addTo(footer);
        }

        footer.addComponent(new EmptySpace(new TerminalSize(42, 1)));
        footer.addComponent(paginator);
        //Start page 1
        reloadData(0);
    }

    private void nagivetTo() {
    }

    private void reloadData(Integer page) {
        String filter = txtFilter.getText();
        int cardPerPage = 9;
        int start = page * cardPerPage;
        currentPage = page;
        //Vider les existante
        cardPanel.removeAllComponents();
        List<Form> forms = Form.getForUser(user, filter, start, cardPerPage);

        //Nb form par ligne
        cardPanel.setLayoutManager(new GridLayout(3));

        for (Form form : forms) {
            CardForFormsView card = new CardForFormsView(form, new CardForFormsController());
            cardPanel.addComponent(card);
        }
        if (forms.isEmpty()) {
            cardPanel.addComponent(new EmptySpace());
            cardPanel.addComponent(new Label("No form found").setForegroundColor(TextColor.ANSI.RED));
        }
        root.addComponent(cardPanel);
        root.invalidate();


    }

}
