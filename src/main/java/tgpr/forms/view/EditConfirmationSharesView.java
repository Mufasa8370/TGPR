package tgpr.forms.view;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.input.KeyType;
import tgpr.forms.controller.EditConfirmationSharesController;
import tgpr.forms.model.AccessType;
import tgpr.forms.model.DistListFormAccess;
import tgpr.forms.model.UserFormAccess;
import tgpr.framework.Controller;

import java.util.List;

public class EditConfirmationSharesView extends DialogWindow {
    private final EditConfirmationSharesController controller;
    public EditConfirmationSharesView(EditConfirmationSharesController controller ) {
        super(controller.getType() == KeyType.Backspace  ? "Delete Share" : "Modify Share");
        this.controller = controller;
        setHints(List.of(Hint.CENTERED, Hint.FIXED_SIZE));
        setFixedSize(new TerminalSize(52, 3));
        Panel root = new Panel();
        String questionsModify;
        new Label(questionsModify = controller.getType() == KeyType.Backspace ? "Are you sure you want to delete this share ?":"Are you sure you want to modify this share ?").addTo(root);
        new EmptySpace().addTo(root);
        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).addTo(root);
        new Button("Yes",()->{
            // Lorsque je presse le bouton enter dans modify shares
            // sa modifie le type d'acces
            if(controller.getType() == KeyType.Enter){
                controller.editAccess();
                this.close();
            }
            else if(controller.getType() == KeyType.Backspace){
                controller.deleteAccess();
                this.close();
            }
        }).addTo(buttons);
        new Button("No",()->{
            this.close();
        }).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        setComponent(root);
    }
}
