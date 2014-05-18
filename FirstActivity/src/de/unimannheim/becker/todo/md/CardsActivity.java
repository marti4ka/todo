package de.unimannheim.becker.todo.md;

import java.text.MessageFormat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.Card.OnCardSwiped;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.SupportMapFragment;

import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;
import de.unimannheim.becker.todo.md.model.LocationDAO;
import de.unimannheim.becker.todo.md.notify.NotifyService;

public class CardsActivity extends FragmentActivity {
	public final static String LOG_TAG = "todo";

	public static final int DEFAULT_NOTIFICATION_RADIUS = 200;
	public static final String PREF_NOTIFICATION_RADIUS = "pref_notification_radius";

	private static final String RADIUS_SETTING_FEEDBACK = "Notify me about tasks in {0} m radius.";
	private static final int MAP_MENU_INDEX = 1;
	private static final int SETTINGS_MENU_INDEX = 2;
	private static final int ABOUT_MENU_INDEX = 4;
	private CardUI mCardView;
	private String[] mMenuListTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ActionBarDrawerToggle mDrawerToggle;
	private ItemDAO itemDAO;
	LocationDAO locationDAO;
	private AddItemCard addItemCard;
	MyMap myMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		itemDAO = new ItemDAO(getApplicationContext());
		locationDAO = new LocationDAO(getApplicationContext());
		mMenuListTitles = getResources().getStringArray(R.array.menu_array);
		loadHomeView();

		Log.v(CardsActivity.LOG_TAG, "starting service...");
		Intent service = new Intent(this, NotifyService.class);
		service.putExtra(NotifyService.SKIP_NOTIFICATION, true);
		startService(service);
	}

	private void loadDrawer() {
		mTitle = mDrawerTitle = getTitle();
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuListTitles));

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuListTitles));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// TODO here something bad happens button animation and onBack don't
		// work

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /*
							 * "close drawer" description for accessibility
							 */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	private void loadHomeView() {
		Item[] items = itemDAO.getItems();
		if (items.length == 0) {
			loadFirstView();
		} else {
			loadCardsView(items);
		}
	}

	private void loadFirstView() {
		setContentView(R.layout.activity_first);
		View first = findViewById(R.id.fullscreen_content);

		// Set up the item creator :)
		first.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				loadCardsViewAndAddFirstCard(false);
				mDrawerList = (ListView) findViewById(R.id.left_drawer_cards);
				mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_cards);
				loadDrawer();
			}
		});
		mDrawerList = (ListView) findViewById(R.id.left_drawer_first);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_first);
		loadDrawer();
	}

	// TODO fix keyboard bug
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				EditText editTitle = (EditText) findViewById(R.id.editTitle);
				EditText editDesc = (EditText) findViewById(R.id.editDescription);
				String title = editTitle.getText().toString();
				String desc = editDesc.getText().toString();
				if ((title != null && !title.equals("")) || (desc != null && !desc.equals(""))) {
					Item newItem = new Item();
					newItem.setTitle(title);
					newItem.setDescription(desc);
					itemDAO.storeItem(newItem);
					loadCardsView(itemDAO.getItems());
				}
			}
		}
		return ret;
	}

	private void loadCardsView(Item[] items) {
		loadCardsViewAndAddFirstCard(true);

		CardStack stack = new CardStack();
		stack.setTitle("TODOS");
		mCardView.addStack(stack);

		OnCardSwiped onSwipeCardListener = new OnCardSwiped() {
			@Override
			public void onCardSwiped(Card card, View layout) {
				itemDAO.archiveItem(((MyPlayCard) card).getItemId());
				mCardView.removeCard(card);
				Toast.makeText(getApplicationContext(), "Item archived", Toast.LENGTH_LONG).show();
				if (mCardView.getTotalNumberOfCards() == 1)
					loadFirstView();
			}
		};

		if (myMap != null && myMap.getLocationClient() != null) {
			LocationClient locationClient = myMap.getLocationClient();
			LocationSorter.sort(locationDAO, locationClient, items);
		}
		// add cards to the view
		// TODO 2 stacks: with and without locations
		for (Item i : items) {
			Card card = new MyPlayCard(i.getTitle(), i.getDescription(), i.getId(), this);
			card.setOnCardSwipedListener(onSwipeCardListener);
			mCardView.addCardToLastStack(card);
		}
		// draw cards
		mCardView.refresh();
		mDrawerList = (ListView) findViewById(R.id.left_drawer_cards);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_cards);
		loadDrawer();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			return false;
		}
	};
	private View mapFragment;

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	@Override
	public void onPause() {
		if (myMap != null && myMap.getLocationClient() != null && myMap.getLocationClient().isConnected()) {
			myMap.getLocationClient().disconnect();
		}
		super.onPause();
	}

	public void showMap(long itemId) {
		loadMapView(itemId);
		updateMenu(MAP_MENU_INDEX);
	}

	private void updateMenu(int menuIndex) {
		mDrawerList.setItemChecked(menuIndex, true);
		setTitle(mMenuListTitles[menuIndex]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		switch (position) {
		case 0:
			loadHomeView();
			break;
		case MAP_MENU_INDEX:
			showMap(MyMap.NO_ITEM);
			return;
		case SETTINGS_MENU_INDEX:
			showSettings();
			break;
		case 3:
			loadArchivedView();
			break;
		case ABOUT_MENU_INDEX:
			showAbout();
			break;
		}

		updateMenu(position);
	}

	private void showAbout() {
		setContentView(R.layout.about);
		
		mDrawerList = (ListView) findViewById(R.id.left_drawer_first);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_about);
		loadDrawer();
	}

	private void showSettings() {
		setContentView(R.layout.settings);

		final TextView feedback = (TextView) findViewById(R.id.distance_feedback);
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_distance);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CardsActivity.this);
		seekBar.setProgress(prefs.getInt(PREF_NOTIFICATION_RADIUS, DEFAULT_NOTIFICATION_RADIUS));

		feedback.setText(MessageFormat.format(RADIUS_SETTING_FEEDBACK,
				new String[] { String.valueOf(seekBar.getProgress()) }));
		OnSeekBarChangeListener l = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				progress /= 50;
				progress *= 50;
				Editor edit = prefs.edit();
				edit.putInt(PREF_NOTIFICATION_RADIUS, progress);
				edit.commit();
				feedback.setText(MessageFormat.format(RADIUS_SETTING_FEEDBACK,
						new String[] { String.valueOf(progress) }));
			}
		};
		seekBar.setOnSeekBarChangeListener(l);

		mDrawerList = (ListView) findViewById(R.id.left_drawer_first);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
		loadDrawer();
	}

	void loadMapView(long itemId) {
		if (mapFragment == null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mapFragment = inflater.inflate(R.layout.map_layout, null);
		}
		setContentView(mapFragment);
		mDrawerList = (ListView) findViewById(R.id.left_drawer_map);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_map);
		loadDrawer();
		// null check to confirm that we have not already instantiated the map.
		if (myMap == null) {
			myMap = new MyMap(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(),
					getApplicationContext(), locationDAO);
		}
		myMap.setItemId(itemId);
		if (myMap.getMap() != null) {
			myMap.addLocationsToMap();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	private void loadCardsViewAndAddFirstCard(final boolean itemsAvailable) {
		setContentView(R.layout.cards);
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		final CardStack newItemStack = new CardStack();
		newItemStack.setTitle("ADD");
		mCardView.addStack(newItemStack);
		addItemCard = new AddItemCard();
		OnCardSwiped onSwipeCardListener = new OnCardSwiped() {
			@Override
			public void onCardSwiped(Card card, View layout) {
				if (itemsAvailable) {
					newItemStack.add(addItemCard);
				} else {
					loadFirstView();
				}
			}
		};
		addItemCard.setOnCardSwipedListener(onSwipeCardListener);
		mCardView.addCardToLastStack(addItemCard);
		mCardView.refresh();
	}

	private void loadArchivedView() {
		setContentView(R.layout.cards);
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(false);

		CardStack stack = new CardStack();
		stack.setTitle("ARCHIVED");
		mCardView.addStack(stack);
		Item[] archivedItems = itemDAO.getArchived();
		for (Item i : archivedItems) {
			Card card = new MyPlayCard(i.getTitle(), i.getDescription(), i.getId(), this);
			mCardView.addCardToLastStack(card);
		}
		mCardView.refresh();
		mDrawerList = (ListView) findViewById(R.id.left_drawer_cards);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_cards);
		loadDrawer();
	}
}
