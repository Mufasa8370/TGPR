package tgpr.forms.controller;

import com.googlecode.lanterna.gui2.ObjectTable;
import tgpr.forms.model.DistList;
import tgpr.forms.model.User;
import tgpr.forms.view.ManageDistListsView;
import tgpr.forms.view.SignupView;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.List;

public class ManageDistListsController extends Controller<ManageDistListsView> {
    private final ManageDistListsView view;
    private List<User> usersAll =  User.getAll();

    public ManageDistListsController() {
        view = new ManageDistListsView(this);

    }

    @Override
    public ManageDistListsView getView() {
        return view;
    }

    public void listForUserOtherTable(ObjectTable<User> userOtherTable, List<User> ds){
        userOtherTable.clear();
        List<User> all = new ArrayList<>();

        for(User user : usersAll){
            if(!ds.contains(user)){
                all.add(user);
            }
        }

        userOtherTable.add(all);


    }
    public void listForUserInTable(ObjectTable<User> userInTable, List<User> ds){
        userInTable.clear();
        userInTable.add(ds);

    }
}
