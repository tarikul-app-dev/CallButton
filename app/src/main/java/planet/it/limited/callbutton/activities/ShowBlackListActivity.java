package planet.it.limited.callbutton.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import planet.it.limited.callbutton.R;
import planet.it.limited.callbutton.adapter.BlackListAdapter;
import planet.it.limited.callbutton.database.DataHelper;
import planet.it.limited.callbutton.util.BlackListModel;

public class ShowBlackListActivity extends AppCompatActivity implements BlackListAdapter.AdapterCallback{
    ListView lvBlackList ;
    DataHelper dataHelper;
    ArrayList<BlackListModel> blackList;
    BlackListAdapter blackListAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_black_list);
        toolbar = (Toolbar)findViewById(R.id.toolbar_black_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        initViews();

    }

    public void initViews(){

        lvBlackList = (ListView) findViewById(R.id.lv_black_list);
        dataHelper = new DataHelper(ShowBlackListActivity.this);
        dataHelper.open();

        blackList = dataHelper.getAllBlackList();
        blackListAdapter = new BlackListAdapter(blackList,ShowBlackListActivity.this,this);
        lvBlackList.setAdapter(blackListAdapter);



    }

    @Override
    public void onMethodCallback(String bListNumber) {
        dataHelper.removeBlackListItem(bListNumber);
        blackList = dataHelper.getAllBlackList();
        blackListAdapter = new BlackListAdapter(blackList,ShowBlackListActivity.this,this);
        lvBlackList.setAdapter(blackListAdapter);

    }
}
