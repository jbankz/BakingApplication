package bankzworld.com.frag;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import bankzworld.com.R;
import bankzworld.com.activities.StepsActivity;
import bankzworld.com.adapters.RecipieAdapter;
import bankzworld.com.pojo.Receipie;

import static bankzworld.com.activities.MainActivity.isTablet;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements RecipieAdapter.ListItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    public static ArrayList<Receipie> bakes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipieAdapter adapter;
    private TextView no_network;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final String KEY_RECYCLER_STATE_LAND = "recycler_state_land";
    private static Parcelable mBundleRecyclerViewState;
    private static Parcelable mBundleRecyclerLan;
    RecyclerView.LayoutManager layoutManager;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view =  inflater.inflate(R.layout.fragment_main, container, false);

        no_network = (TextView) view.findViewById(R.id.no_network);
        no_network.setVisibility(View.GONE);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swip_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        recyclerView = (RecyclerView) view.findViewById(R.id.recipie_list);
        layoutManager = new GridLayoutManager(getActivity(), 3);


        networkUp();
        downloadRecipes();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            mBundleRecyclerViewState = layoutManager.onSaveInstanceState();
            outState.putParcelable(KEY_RECYCLER_STATE, mBundleRecyclerViewState);
        }
        else  if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            mBundleRecyclerLan = layoutManager.onSaveInstanceState();
            outState.putParcelable(KEY_RECYCLER_STATE_LAND, mBundleRecyclerLan);
        }

    }

    @Override
    public void onViewStateRestored( Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Retrieve list state and list/item positions
        if(savedInstanceState != null) {
            if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                mBundleRecyclerViewState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
            }
            else if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
                mBundleRecyclerLan = savedInstanceState.getParcelable(KEY_RECYCLER_STATE_LAND);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null) {
            if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                layoutManager.onRestoreInstanceState(mBundleRecyclerViewState);
            }
            else if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
                layoutManager.onRestoreInstanceState(mBundleRecyclerLan);
            }

        }
    }

    void loadViews(ArrayList<Receipie> bakes) {


        if (isTablet) {
            layoutManager = new GridLayoutManager(getActivity(), 3);
        } else {

            if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT)
            {
            layoutManager = new LinearLayoutManager(getActivity());
        }else {

                layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        }


        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipieAdapter(this, bakes);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Intent intent = new Intent(getActivity(), StepsActivity.class);
        intent.putExtra("item", clickedItemIndex);
        startActivity(intent);

    }

    @Override
    public void onRefresh() {

        no_network.setVisibility(View.GONE);
        downloadRecipes();

    }

    public class FetchRecipieTask extends AsyncTask<Void,Void,ArrayList<Receipie>> {

       @Override
       protected ArrayList<Receipie> doInBackground(Void... params) {


           HttpURLConnection urlConnection = null;
           BufferedReader reader = null;

           //Udacity Recipes
           final String UDACITY_BASE_URL_MOVIE = "https://go.udacity.com/android-baking-app-json";


           try {
               Uri builtUri = Uri.parse(UDACITY_BASE_URL_MOVIE)
                       .buildUpon()
                       .build();

               URL url = new URL(builtUri.toString());

               urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestMethod("GET");
               urlConnection.connect();

               InputStream inputStream = urlConnection.getInputStream();
               StringBuffer buffer = new StringBuffer();
               if (inputStream == null) {
                   return null;
               }
               reader = new BufferedReader(new InputStreamReader(inputStream));

               String line;
               while ((line = reader.readLine()) != null) {
                   buffer.append(line + "\n");
               }
               if (buffer.length() == 0) {
                   return null;
               }
               JSONArray movieArray = new JSONArray(buffer.toString());
               bakes = new ArrayList<>();
               for (int i = 0; i < movieArray.length(); i++) {
                   bakes.add(new Receipie(movieArray.getJSONObject(i)));
                   Log.e("name: ", bakes.get(i).getName());
               }
               return bakes;
           } catch (Exception e) {
               e.printStackTrace();
               return bakes;
           } finally {
               try {
                   if (urlConnection != null) {
                       urlConnection.disconnect();
                   }
                   if (reader != null) {
                       reader.close();
                   }
               } catch (Exception e) {

                   Log.d("MainActivityFragment", e.getMessage());
               }
           }
       }

       @Override
       protected void onPostExecute(ArrayList<Receipie> recipies) {

           loadViews(recipies);
           swipeRefreshLayout.setRefreshing(false);

           
       }
   }

    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }


  private void downloadRecipes(){

      if(networkUp()){

          swipeRefreshLayout.setRefreshing(true);

          new FetchRecipieTask().execute();
      }else {
          no_network.setVisibility(View.VISIBLE);
          swipeRefreshLayout.setRefreshing(false);
      }
  }

}
