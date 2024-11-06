package tgpr.forms.controller;
import tgpr.forms.model.User;
import tgpr.forms.model.UserFormAccess;
import tgpr.framework.Controller;
import static tgpr.framework.Tools.*;
import tgpr.forms.view.ManageSharesView;
import tgpr.framework.Controller;

public class ManageSharesController extends Controller<ManageSharesView> {


    private final ManageSharesView view;
    private  User user;

    public ManageSharesController(){
        this(null);
    }

    public ManageSharesController(User user){
        this.user = user;
        view = new ManageSharesView(this,user);
    }

    public void SharesForm(){
        navigateTo(new ManageSharesController());

    }

    @Override
    public ManageSharesView getView() {return view;}
}
