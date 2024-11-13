package tgpr.forms.controller;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.model.*;
import tgpr.framework.Controller;

import tgpr.forms.view.ManageSharesView;
import tgpr.framework.Model;

import java.util.ArrayList;
import java.util.List;

public class ManageSharesController extends Controller<ManageSharesView> {


    private final ManageSharesView view;
    private final Form form;
    private List<User> filteredUsers;
    private List<DistList> notAddedLists;
    private List<DistList> listAccessDisList;
    private List<DistListFormAccess> distlistAccessesFiltered;
    private List<UserFormAccess> userAccessFiltered;
    private List<UserFormAccess> formsAccesses;
    public ManageSharesController(Form form){
        this.form = form;
        view = new ManageSharesView(this,form);

    }

    public List<Model> getPotentialBeneficiaries(){
/*
        ArrayList<Model> listFull = new ArrayList<>();

        // HERE: Récupération dans distlist_form_accesses
        //       des listes d'utilisateurs qui ont accès au formulaire.
        distlistAccessesFiltered = new ArrayList<>();
        for (DistListFormAccess listAccess : DistListFormAccess.getAll()) {
            if (listAccess.getFormId() == form.getId()) {
                // récupérer tous les listes qui on accès à notre formulaire courant.
                distlistAccessesFiltered.add(listAccess);
            }
        }

        // HERE: Récupération de tous les utilisateurs des listes/Distlist_users
        //       qui ont accès à mon formualire courant.
        ArrayList<DistListUser> listDistUsers = new ArrayList<DistListUser>();
        for(int a = 0; a < distlistAccessesFiltered.size(); a++){
            for(DistListUser userFromDistList: DistListUser.getAll()){
                // si un user appartient à la dislist qui est dans la liste distlistAccessesFiltered
                // et s'il est pas  le membre courrant alors on l'ajoute.
                if(userFromDistList.getDistListId() == distlistAccessesFiltered.get(a).getDistListId() && userFromDistList.getUserId() != Security.getLoggedUserId()){
                    listDistUsers.add(userFromDistList);
                }
            }
        }

        // HERE: Récupération des user_form_access qui n'appartiennent pas
        //       à une liste qui a accès à l'utilisateur courant.
        userAccessFiltered = new ArrayList<>();
        formsAccesses = UserFormAccess.getAll();
        for (UserFormAccess userAccess : formsAccesses) {
            boolean isPresent = false;
            if  (userAccess.getFormId() == form.getId()) {
                // récupére tous les utilisateurs de la table user_from_access
                // ont accès au formulaire courant.

                // Si un utilisateur dans la table user_from_access
                // appartient déjà à une liste qui a accès au formulaire courant,
                // je ne l'ajoute pas.
                for(DistListUser elem: listDistUsers){
                    if(userAccess.getUserId() == elem.getUserId()){
                        isPresent = true;
                    }
                }
                if(!isPresent) {
                    System.out.println("taken0");
                    userAccessFiltered.add(userAccess);
                }
            }
        }


        // HERE: Récupération de la liste des utilisateurs qui ne sont ni dans les listes
        //       qui donnent accès au formualaire courant ni dans userAccessFiltered.
        var users = User.getAll();
        filteredUsers = new ArrayList<>();
        for(User user : users){
            System.out.print(user);
            // si l'utilsateur est ni l'utilisateur courrant ni un admin et qui n'est pas le propriétaire du fomrulaire.
            if(user.getId() != Security.getLoggedUserId() && user.getRole() != User.Role.Admin  && user.getId() != form.getOwnerId()){
                boolean isPresentInDistList = false;
                boolean isPresentInFormAccess = false;
                for (DistListUser userListDist: listDistUsers){
                    if(user.getId() == userListDist.getUserId()){
                        isPresentInDistList = true;
                    }
                }
                if(!isPresentInDistList){
                    filteredUsers.add(user);
                    System.out.println("Taken 1");
                    continue;
                }
                for (UserFormAccess userAccess: userAccessFiltered){
                    if(user.getId() == userAccess.getUserId()){
                        isPresentInFormAccess = true;
                    }
                }
                if(!isPresentInFormAccess){
                    filteredUsers.add(user);
                    System.out.println("Taken 2");
                }
            } else {
                System.out.println("Not taken");
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

        listFull.addAll(filteredUsers);
        listFull.addAll(notAddedLists);

*/
        return form.getPotentialBeneficiaries();
    }

    public List<Model> getAccesses() {
        var accesses = form.getAccesses();
        ArrayList<Model> accessesToDisplay = new ArrayList<>();
        for (Model access: accesses) {
            if(access instanceof UserFormAccess){
                var id = ((UserFormAccess)access).getUserId();
                if(id != form.getOwnerId() && Security.getLoggedUserId() != id) {
                    accessesToDisplay.add(access);
                }
            } else {
                accessesToDisplay.add(access);
            }
        }
        return accessesToDisplay;
    }

    public List<UserFormAccess> getUserAccess() {
        List<UserFormAccess> usersToDisplay = new ArrayList<>();
        for (UserFormAccess access: formsAccesses) {
            if(access.getFormId() == form.getId() && Security.getLoggedUserId() != access.getUserId()){
                usersToDisplay.add(access);
            }
        }
        return usersToDisplay;
    }

    public List<DistListFormAccess> getDistListFormAccess(){
        return distlistAccessesFiltered;
    }
    public static void SharesForm(){
        navigateTo(new ManageSharesController(Form.getByKey(14)));
    }

    public void addAccess(Model beneficiary, AccessType accessType) {
        if (beneficiary instanceof User) {
            form.addAccess(((User) beneficiary), accessType);
        }
        else if(beneficiary instanceof DistList){
            form.addAccess(((DistList) beneficiary), accessType);
        }
    }

    public void gotoEditConfirmationShares(Model model, KeyType type){
        navigateTo(new EditConfirmationSharesController(model,type));
    }
    @Override
    public ManageSharesView getView() {return view;}
}
