package de.unimannheim.becker.todo;

import java.util.Locale;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class CardsActivity extends Activity {

	private CardUI mCardView;
	// @Todo rename
	private String[] mMenuListTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);

		// @Todo rename
		mMenuListTitles = getResources().getStringArray(R.array.menu_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mTitle = mDrawerTitle = getTitle();

		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		CardStack stack2 = new CardStack();
		stack2.setTitle("REGULAR CARDS");
		mCardView.addStack(stack2);

		// add AndroidViews Cards
		mCardView.addCard(new MyCard("Get the CardsUI view"));
		mCardView.addCardToLastStack(new MyCard("for Android at"));
		MyCard androidViewsCard = new MyCard("www.androidviews.net");
		androidViewsCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.androidviews.net/"));
				startActivity(intent);

			}
		});

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.androidviews.net/"));

		mCardView.addCardToLastStack(androidViewsCard);

		CardStack stackPlay = new CardStack();
		stackPlay.setTitle("GOOGLE PLAY CARDS");
		mCardView.addStack(stackPlay);

		// add one card, and then add another one to the last stack.
		mCardView.addCard(new MyCard("Google Play Cards"));
		mCardView.addCardToLastStack(new MyCard("By Androguide & GadgetCheck"));

		mCardView.addCardToLastStack(new MyPlayCard("Google Play", "This card mimics the new Google play cards look",
				"#33b6ea", "#33b6ea", true, false));

		mCardView
				.addCardToLastStack(new MyPlayCard(
						"Menu Overflow",
						"The PlayCards allow you to easily set a menu overflow on your card.\nYou can also declare the left stripe's color in a String, like \"#33B5E5\" for the holo blue color, same for the title color.",
						"#e00707", "#e00707", false, true));

		// add one card
		mCardView.addCard(new MyPlayCard("Different Colors for Title & Stripe",
				"You can set any color for the title and any other color for the left stripe", "#f2a400", "#9d36d0",
				false, false));

		mCardView
				.addCardToLastStack(new MyPlayCard(
						"Set Clickable or Not",
						"You can easily implement an onClickListener on any card, but the last boolean parameter of the PlayCards allow you to toggle the clickable background.",
						"#4ac925", "#222222", true, true));

		// draw cards
		mCardView.refresh();

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

		final View contentView = mCardView;

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getApplicationContext(), "Touched", Toast.LENGTH_SHORT).show();
			}
		});

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

	/**
	 * Fragment that appears in the "content_frame", change the view
	 */
	public static class TheNewFragment extends Fragment {
		public static final String ARG_MENU_ITEM_NUMBER = "planet_number";

		public TheNewFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// @todo change the view
			View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
			// here we get the id of the clicked item from the list
			// switch/case
			int i = getArguments().getInt(ARG_MENU_ITEM_NUMBER);
			// get the title from the strings
			String planet = getResources().getStringArray(R.array.menu_array)[i];

			// load the image and the layout
			int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()), "drawable",
					getActivity().getPackageName());
			((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);

			getActivity().setTitle(planet);
			return rootView;
		}
	}
}