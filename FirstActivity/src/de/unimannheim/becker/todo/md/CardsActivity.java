package de.unimannheim.becker.todo.md;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.RecyclableCard;
import com.fima.cardsui.objects.Card.OnCardSwiped;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;
import de.unimannheim.becker.todo.md.model.Reminder;
import de.unimannheim.becker.todo.md.model.ReminderDAO;

public class CardsActivity extends FragmentActivity {

    private CardUI mCardView;
    private String[] mMenuListTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private ItemDAO itemDAO;
    private ReminderDAO reminderDAO;
    private AddItemCard addItemCard;
    private MyMap myMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemDAO = new ItemDAO(getApplicationContext());
        reminderDAO = new ReminderDAO(getApplicationContext());
        mMenuListTitles = getResources().getStringArray(R.array.menu_array);
        loadHomeView();
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
                if (mCardView.getTotalNumberOfCards() == 1)
                    loadFirstView();
            }
        };

        // add cards to the view
        // TODO how to sort
        for (Item i : items) {
            Card card = new MyPlayCard(i.getTitle(), i.getDescription(), i.getId());
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public void onPause() {
        if (myMap != null) {
            myMap.getLocationClient().disconnect();
            super.onPause();
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        switch (position) {
        case 0:
            loadHomeView();
            break;
        case 1:
            loadMapView();
            break;
        case 3:
            loadArchivedView();
            break;
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuListTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void loadMapView() {
        setContentView(R.layout.map_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_map);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_map);
        loadDrawer();
        // null check to confirm that we have not already instantiated the map.
        if (myMap == null) {
            myMap = new MyMap(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(),
                    getApplicationContext());
            if (myMap.getMap() != null) {
                addLocationsToMap();
            }
        }
    }

    private void addLocationsToMap() {
        Reminder[] activeReminders = reminderDAO.getActive();
        for (Reminder r : activeReminders) {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(r.getLatitude(), r.getLongtitude())).title(
                    String.valueOf(itemDAO.getItemTitle(r.getItemId())));
            // TODO change color of marker to blue
            myMap.getMap().addMarker(marker);
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
            Card card = new MyPlayCard(i.getTitle(), i.getDescription(), i.getId());
            mCardView.addCardToLastStack(card);
        }
        mCardView.refresh();
        mDrawerList = (ListView) findViewById(R.id.left_drawer_cards);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_cards);
        loadDrawer();
    }

    private class MyPlayCard extends RecyclableCard {

        private int itemId;

        public int getItemId() {
            return itemId;
        }

        public MyPlayCard(String titlePlay, String description, int id) {
            super(titlePlay, description, "#33b6ea", "#33b6ea", true, false);
            this.itemId = id;
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
                                    loadMapView();
                                    myMap.startAddingLocation(itemId, reminderDAO);
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
}
