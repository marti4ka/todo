package de.unimannheim.becker.todo.md;

import android.view.View;
import android.widget.EditText;

import com.fima.cardsui.objects.RecyclableCard;

public class AddItemCard extends RecyclableCard {

    public AddItemCard() {
        super("", "", "#33b6ea", null, true, false);
    }
    
    @Override
    protected int getCardLayoutId() {
        return R.layout.add_card_layout;
    }

    @Override
    protected void applyTo(View convertView) {
        EditText editTitle = (EditText) convertView.findViewById(R.id.editTitle);
        editTitle.setHint("Add task");
    }
    
}
