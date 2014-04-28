package de.unimannheim.becker.todo.md;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fima.cardsui.objects.Card.OnCardSwiped;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;

public class CardsActivity extends Activity {

    private CardUI mCardView;
    private String[] mMenuListTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private ItemDAO itemDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemDAO = new ItemDAO(getApplicationContext());
        mMenuListTitles = getResources().getStringArray(R.array.menu_array);

        // TODO remove this shit
        itemDAO.deleteAll();
        // Item item = new Item();
        // item.setTitle("testTitle");
        // item.setDescription("testDescr");
        // itemDAO.storeItem(item);

        loadActivity();
    }

    private void loadDrawer() {
        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuListTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
        mDrawerLayout, /* DrawerLayout object */
        R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
        R.string.drawer_open, /* "open drawer" description for accessibility */
        R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                                         // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                                         // onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    private void loadActivity() {
        Item[] items = itemDAO.getItems();
        if (items.length == 0) {
            loadFirstView();
        } else {
            loadCardsView(items);
        }
        loadDrawer();
    }

    private void loadFirstView() {
        setContentView(R.layout.activity_first);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mTitle = mDrawerTitle = getTitle();
        View first = findViewById(R.id.fullscreen_content);

        // Set up the item creator :)
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO load cards view
                // add the addItemCard
                Item item = new Item();
                item.setTitle("testTitle");
                item.setDescription("testDescr");
                itemDAO.storeItem(item);
                loadActivity();
            }
        });
    }

    private void loadCardsView(Item[] items) {
        setContentView(R.layout.cards);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mTitle = mDrawerTitle = getTitle();
        mCardView = (CardUI) findViewById(R.id.cardsview);
        mCardView.setSwipeable(true);
        final CardStack newItemStack = new CardStack();
        newItemStack.setTitle("ADD");
        mCardView.addStack(newItemStack);
        final AddItemCard addItemCard = new AddItemCard();
        OnCardSwiped onSwipeCardListener = new OnCardSwiped() {
            @Override
            public void onCardSwiped(Card card, View layout) {
                newItemStack.add(addItemCard);
            }
        };
        addItemCard.setOnCardSwipedListener(onSwipeCardListener);
        mCardView.addCardToLastStack(addItemCard);

        CardStack stack = new CardStack();
        // TODO how to sort
        stack.setTitle("TODOS");
        mCardView.addStack(stack);

        // add cards to the view
        for (Item i : items) {
            mCardView.addCardToLastStack(new MyPlayCard(i.getTitle(), i.getDescription()));
        }

        // TODO example for set listener for update
        MyPlayCard androidViewsCard = new MyPlayCard("www.androidviews.net", "blablabla lorem impsum :D");
        androidViewsCard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.androidviews.net/"));
                startActivity(intent);
            }
        });
        mCardView.addCardToLastStack(androidViewsCard);

        // draw cards
        mCardView.refresh();
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

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = new TheNewFragment();
        Bundle args = new Bundle();
        args.putInt(TheNewFragment.ARG_MENU_ITEM_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fullscreen_content, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuListTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
