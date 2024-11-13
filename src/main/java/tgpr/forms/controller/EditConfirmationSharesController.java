package tgpr.forms.controller;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.model.*;
import tgpr.forms.view.EditConfirmationSharesView;
import tgpr.forms.view.ManageSharesView;
import tgpr.framework.Controller;
import tgpr.framework.Model;
import tgpr.forms.model.AccessType;
import tgpr.forms.model.DistListFormAccess;
import tgpr.forms.model.UserFormAccess;
public class EditConfirmationSharesController  extends Controller<EditConfirmationSharesView>{
    private final EditConfirmationSharesView view;
    private final Model model;
    private final KeyType type;
    public EditConfirmationSharesController(Model model, KeyType type){
        this.type = type;
        this.model = model;
        view = new EditConfirmationSharesView(this);
    }

    public KeyType getType(){
        return type;
    }

    public Model getModel(){
        return model;
    }

    public void editAccess(){
            // Si c'est un utilisateur alors ...
        System.out.println(model);
        if( model instanceof UserFormAccess){
            if(((UserFormAccess)model).getAccessType() == AccessType.Editor){
                ((UserFormAccess)model).setAccessType(AccessType.User);
                ((UserFormAccess)model).save();
            }
            else if(((UserFormAccess)model).getAccessType() == AccessType.User){
                ((UserFormAccess)model).setAccessType(AccessType.Editor);
                ((UserFormAccess)model).save();
            }
        }
        // Si c'est une liste alors...
        else if (model instanceof DistListFormAccess){
            if(((DistListFormAccess)model).getAccessType() == AccessType.Editor){
                ((DistListFormAccess)model).setAccessType(AccessType.User);
                ((DistListFormAccess)model).save();
            }
            else if(((DistListFormAccess)model).getAccessType() == AccessType.User){
                ((DistListFormAccess)model).setAccessType(AccessType.Editor);
                ((DistListFormAccess)model).save();
            }
        }
    }
    @Override
    public EditConfirmationSharesView getView() {return view;}
}
