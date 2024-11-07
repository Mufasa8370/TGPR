package tgpr.forms.controller;
import tgpr.forms.model.*;
import tgpr.framework.Controller;
import static tgpr.framework.Tools.*;
import tgpr.forms.view.ManageSharesView;
import tgpr.framework.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManageSharesController extends Controller<ManageSharesView> {


    private final ManageSharesView view;
    private final Form form;
    // private final List<User>;

    public ManageSharesController(Form form){
        view = new ManageSharesView(this);
        this.form = form;

        // HERE: Récupération des distlists_access
        List<DistListFormAccess> distlistAccessesFiltered = new ArrayList<>();
        for (DistListFormAccess listAccess : DistListFormAccess.getAll()) {
            if (listAccess.getFormId() == form.getId()) {
                // récupérer tous les listes qui on accès à notre formulaire courant.
                distlistAccessesFiltered.add(listAccess);
            }
        }

        // HERE: Récupération des user_form_access
        List<UserFormAccess> userAccessFiltered = new ArrayList<>();
        for (UserFormAccess userAccess : UserFormAccess.getAll()) {
            if (userAccess.getFormId() == form.getId()) {
                // récupére tous les utilisateurs qui ont accès à la liste courant.
                userAccessFiltered.add(userAccess);
            }
        }


        // HERE: Récupération de la liste des utilisateurs
        var users = User.getAll();
        List<User> filteredUsers = new ArrayList<>();
        for(User ux : users){
            if(ux.getId() != Security.getLoggedUserId() && ux.getRole() != User.Role.Admin ){
                // si l'utilsateur est ni un admin ni l'utilisateur courrant.
                for(UserFormAccess userAccess : userAccessFiltered){
                    // si il a pas déjà accès à ce formulaire.
                    if(ux.getId() != userAccess.getUserId()){
                        filteredUsers.add(ux);
                    }
                }
    }
        }
        // HERE: Récupération des distlists
        List<DistList> notAddedLists = new ArrayList<>();
        for (DistList list : DistList.getAll()) {
            // si parmi la liste de liste d'accès il y a pas celle dans laquelle je suis alors je l'ajoute.
            if (distlistAccessesFiltered.stream().noneMatch((otherList) -> otherList.getDistListId() == list.getId())) {
                notAddedLists.add(list);
            }
        }
    }

    public static void SharesForm(){
        navigateTo(new ManageSharesController(Form.getByKey(14)));
    }

    @Override
    public ManageSharesView getView() {return view;}
}
