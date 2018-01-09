package my.edu.tarc.madassignment.teacherSubjectActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.madassignment.MainActivity;
import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.SaveSharedPreferences;
import my.edu.tarc.madassignment.entities.FileCategory;
import my.edu.tarc.madassignment.entities.Subject;

public class teacherSubjectActionActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public FloatingActionButton fab;
    public String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout){

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                animateFab(position);
            }

        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());

            }
        });


        Subject selectedSubject = (Subject) getIntent().getSerializableExtra("clickedsubject");
        subjectId=selectedSubject.getId();
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);




        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //android.app.Fragment fragment = getFragmentManager().findFragmentByTag("pos");
                final Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.container+":"+mViewPager.getCurrentItem());
                if(fragment!=null&&mViewPager.getCurrentItem()==0) {
                    Intent intent = new Intent(getApplicationContext(), MakeAnnouncementActivity.class);
                    intent.putExtra("clickedsubject", subjectId);
                    startActivity(intent);
                }
                else if(fragment!=null&&mViewPager.getCurrentItem()==1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(teacherSubjectActionActivity.this);
                    builder.setTitle("New category");
                    final EditText input = new EditText(getApplicationContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //FragmentManager fm = getSupportFragmentManager();
                            //uploadDocumentActivity fragment = (uploadDocumentActivity)fm.findFragmentById(R.id.container);
                            //Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.container+":"+mViewPager.getCurrentItem());
                            String categoryName = input.getText().toString();
                            String url = getString(R.string.insert_category_url);
                            FileCategory fileCategory = new FileCategory(subjectId, categoryName);
                            insertCategory(getApplicationContext(), url, fileCategory);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else if(fragment!=null&&mViewPager.getCurrentItem()==2){

                }
            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
        return true;
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
        else if(id == R.id.action_logout){
            SaveSharedPreferences.clear(this);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_post_announcement, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:

                    return new postAnnouncementActivity();

                case 1:

                    return new uploadDocumentActivity();

                case 2:
                    return new QRCodeActivity();
            }

            return null;
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
    private void animateFab(int position) {
        switch (position) {
            case 0:
                fab.show();
                break;
            case 1:
                fab.show();
                break;

            default:
                fab.hide();
                break;
        }
    }
    public void insertCategory(final Context context, String url, final FileCategory fileCategory) {  //context: what context called this
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);
        //Send data
        try {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        if (success==0) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subjectid", fileCategory.getSubject_id());
                    params.put("filecategory", fileCategory.getFile_category());
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
