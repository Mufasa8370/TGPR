package tgpr.forms.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import tgpr.forms.controller.ViewFormsController;
import tgpr.forms.model.Form;
import tgpr.forms.model.User;

import java.util.List;

import static tgpr.forms.model.Security.getLoggedUser;

public class ViewFormsView extends BasicWindow{

   private final ViewFormsController controller;
    private final TextBox txtFilter;
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
        Panel root = new Panel();
        setComponent(root);

        //Menu
        MenuBar menuBar = new MenuBar().addTo(root);
        Menu menuFile = new Menu("File");
        menuBar.add(menuFile);
        MenuItem menuViewProfile = new MenuItem("Exit", controller::menuViewProfile);
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
        ;
        filter.addComponent(new Label("Filter:"));
        txtFilter = new TextBox().setPreferredSize(new TerminalSize(30, 1));;

        txtFilter.setTextChangeListener((txt, byUser) -> reloadData());
        filter.addComponent(txtFilter);

        root.addComponent(filter);



    }

    private void reloadData() {
    }
}
