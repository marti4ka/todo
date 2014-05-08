package de.unimannheim.becker.todo.md;

import android.graphics.Color;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

class MyPlayCard extends RecyclableCard {

	private int itemId;
	private CardsActivity cardsActivity;

	public int getItemId() {
		return itemId;
	}

	public MyPlayCard(String titlePlay, String description, int id, CardsActivity activity) {
		super(titlePlay, description, "#33b6ea", "#33b6ea", true, false);
		this.itemId = id;
		this.cardsActivity = activity;
	}

	public MyPlayCard(CardsActivity cardsActivity) {
		this.cardsActivity = cardsActivity;
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.card_play;
	}

	@Override
	protected void applyTo(View convertView) {
		((TextView) convertView.findViewById(R.id.title)).setText(titlePlay);
		((TextView) convertView.findViewById(R.id.title)).setTextColor(Color.parseColor(titleColor));
		((TextView) convertView.findViewById(R.id.description)).setText(description);
		((ImageView) convertView.findViewById(R.id.stripe)).setBackgroundColor(Color.parseColor(color));

		if (isClickable == true)
			((LinearLayout) convertView.findViewById(R.id.contentLayout))
					.setBackgroundResource(R.drawable.selectable_background_cardbank);

		if (hasOverflow == true) {
			ImageView overflow = (ImageView) convertView.findViewById(R.id.overflow);
			overflow.setVisibility(View.VISIBLE);
			overflow.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					PopupMenu popup = new PopupMenu(v.getContext(), v);
					MenuInflater inflater = popup.getMenuInflater();
					inflater.inflate(R.menu.actions, popup.getMenu());
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							switch (item.getItemId()) {
							case R.id.menu_add_location:
								cardsActivity.loadMapView();
								cardsActivity.myMap.startAddingLocation(itemId, cardsActivity.reminderDAO);
								return true;
							case R.id.menu_edit:
								return false;
							default:
								return false;
							}
						}
					});
					popup.show();
				}
			});
		} else
			((ImageView) convertView.findViewById(R.id.overflow)).setVisibility(View.GONE);
	}

}