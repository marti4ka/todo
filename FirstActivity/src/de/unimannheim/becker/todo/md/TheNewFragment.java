package de.unimannheim.becker.todo.md;

import java.util.Locale;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Fragment that appears in the "content_frame", change the view
 */
public class TheNewFragment extends Fragment {
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