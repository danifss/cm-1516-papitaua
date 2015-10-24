package pt.ua.daniel.papitaua;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.io.IOException;

import pt.ua.daniel.papitaua.model.DailyOption;
import pt.ua.daniel.papitaua.model.Queries;
import pt.ua.daniel.papitaua.model.ParserUtils;
import pt.ua.daniel.papitaua.model.UAMenus;


/**
 * Created by silva on 11-10-2015.
 */
public class HeadlinesFragment extends ListFragment {
    OnHeadlineSelectedListener mCallback;

//    ArrayList<String> menu;
//    ArrayList<String> placeMenu;
//    ArrayList<String> tipoRefeicao;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        ArrayAdapter<DailyOption> menus = getMenus();

        setListAdapter(menus);

    }

    @Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        mCallback.onArticleSelected(position);

        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
    }

    public ArrayAdapter<DailyOption> getMenus() {
        //this allow to perform network tasks on the UI thread
        // you may use this just for testing, not for production
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Gets the URL from the UI's text field.
        String stringUrl = Queries.queries[0];

        // check for network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                // call the web service
                String response = ParserUtils.callEmentasWS(stringUrl);
                Log.i("EMENTAS", "Got JSON");

                UAMenus menus = UAMenus.parseJson(response);
                Log.i("EMENTAS", "JSON results parsed");

                // Filtering deactivated Canteens
                for(int i=0;i<menus.getDailyMenus().size();i++) {
                    if(!menus.getDailyMenus().get(i).isAvailable())
                        menus.getDailyMenus().remove(i);
                }
                ArrayAdapter<DailyOption> arrayAdapter = new ArrayAdapter<DailyOption>(getActivity(), android.R.layout.simple_list_item_1, menus.getDailyMenus());

//                int count = 0;
//                int countHeaders = 0;
//                menu = new ArrayList<String>();
//                placeMenu = new ArrayList<String>();
//                tipoRefeicao = new ArrayList<String>();
//                List<DailyOption> listMenus = menus.getDailyMenus();
//                for (int i=0; i<listMenus.size(); i++) { // percorre a lista de DailyOptions
//                    if (listMenus.get(i).isAvailable()) {
//                        String place = listMenus.get(i).getCanteenSite(); // get place
//                        placeMenu.add(countHeaders, place); // associar sitio ao menu i
//                        //Log.i("PLACE",place);
//                        String dailyMeal = listMenus.get(i).getDailyMeal(); // get meal
//                        tipoRefeicao.add(countHeaders,dailyMeal); // associar tipo de refeicao ao menu i
//                        //Log.i("DailyMeal",dailyMeal);
//                        countHeaders++;
//
//                        List<MealCourse> listMeals = listMenus.get(i).getMealCourseList(); // get meals course list
//                        for (int j=0; j<listMeals.size(); j++) {
//                            String meal = listMeals.get(j).getFoodOption(); // get food description
//                            menu.add(count,meal); // associar refeicoes
//                            //Log.i("MEAL",meal);
//                            count++;
//                        }
//                    }
//                }


                return arrayAdapter;
            } catch (IOException e) {
                Log.e("EMENTAS", e.getMessage());
            }
        } else {
            Log.e("EMENTAS", "Problem: no network connection available.");
        }

        return new ArrayAdapter<DailyOption>(getActivity(), android.R.layout.simple_list_item_1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}