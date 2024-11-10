package tgpr.forms.controller;
import tgpr.forms.model.*;
import tgpr.framework.Controller;

import static tgpr.framework.Tools.*;
import tgpr.forms.view.ManageSharesView;
import tgpr.framework.Controller;
import tgpr.framework.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManageSharesController extends Controller<ManageSharesView> {


    private final ManageSharesView view;
    private final Form form;
    private List<User> filteredUsers;
    private List<DistList> notAddedLists;
    private ArrayList<Integer> listTestId = new ArrayList<Integer>();

    public ManageSharesController(Form form){
        this.form = form;
        view = new ManageSharesView(this);

    }

    public ArrayList<Model>getAll(){
        ArrayList<Model> listFull = new ArrayList<>();

        // HERE: Récupération dans distlist_form_accesses
        //       des listes d'utilisateurs qui ont accès au formulaire.
        List<DistListFormAccess> distlistAccessesFiltered = new ArrayList<>();
        for (DistListFormAccess listAccess : DistListFormAccess.getAll()) {
            if (listAccess.getFormId() == form.getId()) {
                // récupérer tous les listes qui on accès à notre formulaire courant.
                distlistAccessesFiltered.add(listAccess);
            }
        }

        // HERE: Récupération de tous les utilisateurs des listes/Distlist_users
        //       qui ont accès à mon formualire courant.
        ArrayList<DistListUser> listDistUsers = new ArrayList<DistListUser>();
        for(int a = 0; a<distlistAccessesFiltered.size();a++ ){
            for(DistListUser userFromDistList: DistListUser.getAll()){
                // si un user appartient à la dislist qui est dans la liste distlistAccessesFiltered
                // et s'il est pas  le membre courrant alors on l'ajoute.
                if(userFromDistList.getDistListId() == distlistAccessesFiltered.get(a).getDistListId() && userFromDistList.getUserId() != Security.getLoggedUserId()){
                    listDistUsers.add( userFromDistList);
                }
            }
        }

        // HERE: Récupération des user_form_access qui n'appartiennent pas
        //       à une liste qui a accès à l'utilisateur courant.
        List<UserFormAccess> userAccessFiltered = new ArrayList<>();
        for (UserFormAccess userAccess : UserFormAccess.getAll()) {
            boolean isPresent = false;
            if  (userAccess.getFormId() == form.getId() && userAccess. getUserId() != Security.getLoggedUserId()) {
                // récupére tous les utilisateurs de la table user_from_access
                // ont accès au formulaire courant.

                // Si un utilisateur dans la table  user_from_access
                // appartient déjà à une liste qui a accès au formulaire courant,
                // je ne l'ajoute pas.
                for(DistListUser elem: listDistUsers){
                    if(userAccess.getUserId() == elem.getUserId() ){
                        isPresent = true;
                    }
                }
                if(!isPresent){
                    userAccessFiltered.add(userAccess);
                }
            }
        }


        // HERE: Récupération de la liste des utilisateurs qui ne sont ni dans les listes
        //       qui donnent accès au formualaire courant ni dans userAccessFiltered.
        var users = User.getAll();
        filteredUsers = new ArrayList<>();
        for(User ux : users){
            // si l'utilsateur est ni l'utilisateur courrant ni un admin.
            if(ux.getId() != Security.getLoggedUserId() && ux.getRole() != User.Role.Admin  && ux.getId() != form.getOwnerId()){
                boolean isPresentDistList = false;
                boolean isPresentFromAccess = false;
                for (DistListUser userListDist: listDistUsers){
                    if(ux.getId() == userListDist.getUserId()){
                        isPresentDistList = true;
                    }
                }
                if(!isPresentDistList){
                    filteredUsers.add(ux);
                }
                for (UserFormAccess userAccess: userAccessFiltered){
                    if(ux.getId() == userAccess.getUserId()){
                        isPresentFromAccess = true;
                    }
                }
                if(!isPresentFromAccess){
                    filteredUsers.add(ux);
                }
            }
        }
        // HERE: Récupération des distlists
        notAddedLists = new ArrayList<>();
        for (DistList list : DistList.getAll()) {
            // si parmi la liste de liste d'accès il y a pas celle dans laquelle je suis alors je l'ajoute.
            if (distlistAccessesFiltered.stream().noneMatch((otherList) -> otherList.getDistListId() == list.getId())) {
                notAddedLists.add(list);
            }
        }

        for(int elem: listTestId){
            listFull.add(User.getByKey(elem));
        }

        listFull.addAll(filteredUsers);
        listFull.addAll(notAddedLists);

        return listFull;
    }

    public static void SharesForm(){
        navigateTo(new ManageSharesController(Form.getByKey(14)));
    }

    @Override
    public ManageSharesView getView() {return view;}
}
