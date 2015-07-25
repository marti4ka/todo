package de.unimannheim.becker.todo.md;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.google.gson.Gson;

import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.ItemDAO;

/**
 * posts the active todos to the given URL
 */
public class SyncTodosTask extends AsyncTask<String, String, StatusLine> {
	private final ItemDAO itemDAO;
	private final String url;
	private final Gson gson = new Gson();
	private final DefaultHttpClient httpClient = new DefaultHttpClient();

	public SyncTodosTask(String url, ItemDAO itemDAO) {
		this.url = url;
		this.itemDAO = itemDAO;
	}

	@Override
	protected StatusLine doInBackground(String... params) {
		publishProgress("Getting todos");
		
		Item[] items = itemDAO.getItems();
		Item[] fromServer = {};
		
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse resp = httpClient.execute(httpGet);
			InputStream is = resp.getEntity().getContent();
			fromServer = gson.fromJson(new InputStreamReader(is), Item[].class);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Map<String,Item> merged = new HashMap<String, Item>();
		for (Item item : items) {
			merged.put(item.getTitle(), item);
		}
		for (Item item : fromServer) {
			if(!merged.containsKey(item.getTitle())) {
				itemDAO.storeItem(item);
				merged.put(item.getTitle(), item);
			}
		}
		
		
		HttpPost httpPost = new HttpPost(url);

		String json = gson.toJson(itemDAO.getItems());

		try {
			HttpEntity entity = new StringEntity(json, "UTF-8");
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			HttpResponse resp = httpClient.execute(httpPost);
			return resp.getStatusLine();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
