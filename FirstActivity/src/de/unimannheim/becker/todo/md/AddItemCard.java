package de.unimannheim.becker.todo.md;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.RecyclableCard;

public class AddItemCard extends RecyclableCard {

    @Override
    protected int getCardLayoutId() {
        return R.layout.add_card_layout;
    }

    @Override
    protected void applyTo(View convertView) {
        // TODO focus??
        EditText editTitle = (EditText) convertView.findViewById(R.id.editTitle);
        editTitle.setHint("Add task");
        
        EditText editDescription = (EditText) convertView.findViewById(R.id.editDescription);
        editDescription.setHint("Description");
        
        ImageButton addReminder = (ImageButton) convertView.findViewById(R.id.addReminder);
        addReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
//                Toast.makeText(CardsActivity.this, "Add reminder clicked", Toast.LENGTH_SHORT).show();                
            }
        });
    }
    
}
