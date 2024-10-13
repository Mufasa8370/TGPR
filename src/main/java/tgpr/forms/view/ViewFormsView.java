package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import tgpr.forms.controller.CardForFormsController;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.Form;
import tgpr.forms.model.User;

import java.util.List;

import static tgpr.forms.model.Security.getLoggedUser;

public class ViewFormsView extends BasicWindow{

   private final ViewFormsController controller;
    Panel root = new Panel();
    private final TextBox txtFilter;
    private final Paginator paginator;
    private final Panel cardPanel = new Panel();
    private int currentPage = 1;
    User user  = getLoggedUser();
    public ViewFormsView(ViewFormsController controller) {
        this.controller = controller;
        //Title
        if (!user.isGuest()){
            setTitle("MyFomrs ("+ user.getFullName() +" - "+user.getRole()+")");
        }else {
            setTitle("MyFomrs ("+user.getRole()+")");
        }
        setHints(List.of(Hint.EXPANDED));
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        setComponent(root);

        //Menu
        MenuBar menuBar = new MenuBar().addTo(root);
        Menu menuFile = new Menu("File");
        menuBar.add(menuFile);
        MenuItem menuViewProfile = new MenuItem("View Profile", controller::menuViewProfile);
        menuFile.add(menuViewProfile);
        MenuItem menuLogout = new MenuItem("Logout", controller::logout);
        menuFile.add(menuLogout);
        MenuItem menuExit = new MenuItem("Exit", controller::exit);
        menuFile.add(menuExit);
        Menu menuParameters = new Menu("Parameters");
        menuBar.add(menuParameters);

        new EmptySpace().addTo(root);
        
        //filtre
        Panel filter = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        filter.addComponent(new Label("Filter:"));
        txtFilter = new TextBox().setPreferredSize(new TerminalSize(30, 1));;
        txtFilter.setTextChangeListener((txt, byUser) -> reloadData(currentPage));
        filter.addComponent(txtFilter);
        root.addComponent(filter);
        //Paginator
         paginator = new Paginator(
                this,
                9,
                this::reloadData
        );
         //Ajout du footer

         root.addComponent(cardPanel).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        Panel footer = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End,LinearLayout.GrowPolicy.CanGrow));

         Button buttonCreateForm = new Button("Creat a new form", controller::createNewForm).addTo(footer);
         footer.addComponent(paginator);
         //Start page 1
         reloadData(1);


    }

    private void reloadData(Integer page) {
        String filter = txtFilter.getText();
        int cardPerPage = 9;
        int start = (page - 1) * cardPerPage;
        currentPage = page;
        //Vider les existante
        cardPanel.removeAllComponents();

        List<Form> forms = Form.getForUser(user,filter,start,cardPerPage);
        paginator.setCount(forms.size());
        //Nb form par ligne
        cardPanel.setLayoutManager(new GridLayout(3).setVerticalSpacing(1).setHorizontalSpacing(1));


        for (Form form : forms){
            CardForFormsView card = new CardForFormsView(form,new CardForFormsController());
            cardPanel.addComponent(card);
        }
        root.addComponent(cardPanel);
        root.invalidate();

    }

}
